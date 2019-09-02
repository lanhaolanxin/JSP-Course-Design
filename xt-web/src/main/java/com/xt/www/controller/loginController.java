package com.xt.www.controller;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.xt.www.common.BaseController;
import com.xt.www.common.Constants;
import com.xt.www.common.MD5Util;
import com.xt.www.common.RandStringUtils;
import com.xt.www.entity.OpenUser;
import com.xt.www.entity.User;
import com.xt.www.service.OpenUserService;
import com.xt.www.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class loginController extends BaseController {
    @Autowired
    UserService userService;

    @Autowired// redis数据库操作模板
    private RedisTemplate<String, String> redisTemplate;// jdbcTemplate HibernateTemplate\

    @RequestMapping("/doLogin")
    public String doLogin(Model model, @RequestParam(value = "username",required = false) String email,
                          @RequestParam(value = "password",required = false) String password,
                          @RequestParam(value = "code",required = false) String code,
                          @RequestParam(value = "telephone",required = false) String telephone,
                          @RequestParam(value = "phone_code",required = false) String phone_code,
                          @RequestParam(value = "state",required = false) String state,
                          @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        //判断是否是手机登录
        if (StringUtils.isNotBlank(telephone)) {
            //手机登录
            String yzm = redisTemplate.opsForValue().get( telephone );//从redis获取验证码
            if(phone_code.equals(yzm)){
                //验证码正确
                User user = userService.findByPhone(telephone);
                getSession().setAttribute("user", user);
                model.addAttribute("user", user);

                return "user";

            }else {
                //验证码错误或过期
                model.addAttribute("error","phone_fail");
                return  "../login";
            }

        } else {
            //账号登录

            if (StringUtils.isBlank(code)) {
                model.addAttribute("error", "fail");
                return "../login";
            }
            int b = checkValidateCode(code);
            if (b == -1) {
                model.addAttribute("error", "fail");
                return "../login";
            } else if (b == 0) {
                model.addAttribute("error", "fail");
                return "../login";
            }
            password = MD5Util.encodeToHex(Constants.SALT + password);
            User user = userService.login(email, password);
            if (user != null) {
                if ("0".equals(user.getState())) {
                    //未激活
                    model.addAttribute("email", email);
                    model.addAttribute("error", "active");
                    return "../login";
                }

                getSession().setAttribute("user", user);
                model.addAttribute("user", user);
                return "user";
            } else {

                model.addAttribute("email", email);
                model.addAttribute("error", "fail");
                return "../login";
            }

        }

    }

    //    匹配验证码的正确性
    public int checkValidateCode(String code) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String vercode = (String) attrs.getRequest().getSession().getAttribute("VERCODE_KEY");
        if (null == vercode) {
            return -1;
        }
        if (!code.equalsIgnoreCase(vercode.toString())) {
            return 0;
        } else
            return 1;
    }



    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;// mq消息模板.

    /**
     * 发送手机验证码
     * @param model
     * @param telephone
     * @return
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public Map<String,Object> index(Model model, @RequestParam(value = "telephone",required = false) final String telephone ) {
        Map map = new HashMap<String,Object>(  );
        try { //  发送验证码操作
            final String code = RandStringUtils.getCode();
            redisTemplate.opsForValue().set(telephone, code, 60, TimeUnit.SECONDS);// 60秒 有效 redis保存验证码
            // 调用ActiveMQ jmsTemplate，发送一条消息给MQ
            jmsTemplate.send("login_msg", new MessageCreator() {
                public Message createMessage(javax.jms.Session session) throws JMSException {
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("telephone",telephone);
                    mapMessage.setString("code", code);
                    return mapMessage;
                }
            });
        } catch (Exception e) {
            map.put( "msg",false );
        }
        map.put( "msg",true );
        return map;

    }

    @RequestMapping("/to_login")
    public String toLogin(Model model) {
        HttpServletRequest request = getRequest();
        String url = "";
        try {
            url = new Oauth().getAuthorizeURL(request);
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        return "redirect:"+url;
    }

    @Autowired
    private OpenUserService openUserService;
    @RequestMapping("/qq_login")
    public String qqLogin(Model model) {
        User user = null;
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(getRequest());
            String accessToken   = null, openID   = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();//授权令牌token
                tokenExpireIn = accessTokenObj.getExpireIn();//过期时间

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();//用户唯一标识
                // 利用获取到的accessToken 去获取当前用户的openid --------- end

                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                    OpenUser openUser =  openUserService.findByOpenId( openID );
                    if(openUser == null){
                        redisTemplate.opsForValue().set(openID, accessToken, 90, TimeUnit.DAYS);// 有效期三个月
                        openUser = new OpenUser();
                        user = new User();
                        user.setEmail( openID );
                        user.setPassword(MD5Util.encodeToHex(Constants.SALT+accessToken) );
                        user.setEnable( "0" );
                        user.setState("1");
                        user.setImgUrl( userInfoBean.getAvatar().getAvatarURL50() );//设置qq头像
                        userService.regist( user );
                        openUser.setOpenId( openID );
                        openUser.setAccessToken( accessToken );
                        openUser.setAvatar( userInfoBean.getAvatar().getAvatarURL50() );
                        openUser.setExpiredTime( tokenExpireIn);
                        openUser.setNickName( userInfoBean.getNickname() );
                        openUser.setOpenType( Constants.OPEN_TYPE_QQ );
                        openUser.setuId( user.getId());
                        openUserService.add( openUser );
                    }else {
                        String token = redisTemplate.opsForValue().get( openID );//从redis获取accessToken
                        if(token==null){
                            //已过期
                            openUser.setAccessToken( accessToken );
                            openUser.setAvatar( userInfoBean.getAvatar().getAvatarURL50() );
                            openUser.setExpiredTime( tokenExpireIn);
                            openUser.setNickName( userInfoBean.getNickname() );
                            openUserService.update(openUser);
                        }
                        user = userService.findById( openUser.getuId() );
                    }

                }

            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
        getSession().setAttribute("user",user);
        return "redirect:/list";
    }
}
