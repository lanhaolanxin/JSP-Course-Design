package com.xt.www.controller;

import com.xt.www.common.CodeCaptchaServlet;
import com.xt.www.common.MD5Util;
import com.xt.www.common.StringUtil;
import com.xt.www.entity.User;
import com.xt.www.mail.SendEmail;
import com.xt.www.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class RegisterController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @RequestMapping("/doRegister")
    public String doRegister(Model model,@RequestParam(value = "email",required = false)String email,
                             @RequestParam(value ="password",required = false)String password,
                             @RequestParam(value ="phone",required = false)String phone,
                                 @RequestParam(value = "code",required = false)String code,
                             @RequestParam(value = "nick_name" ,required = false)String nick_name) {
        if (StringUtil.isBlank(code)) {
            model.addAttribute("error","非法注册，请重新注册");
            return "../register";
        }
        int b = checkValidateCode(code);
        if (b==-1){
            model.addAttribute("error","验证码超时，请重新注册");
        }
        if (b==0){
            model.addAttribute("error","验证码错误，请重新输入");
            return "../register";
        }
            User user = userService.findByEmail(email);
        if (user != null) {
            return "../register";
        }
        else {
            user = new User();
            user.setEmail(email);
            user.setPassword(MD5Util.encodeToHex("salt"+password));
            user.setPhone(phone);
            user.setNickName(nick_name);
            user.setState("0");
            user.setEnable("0");
            user.setImgUrl("/images/icon_m.jpg");
            String validateCode = MD5Util.encodeToHex("salt"+email+password);
            redisTemplate.opsForValue().set(email, validateCode, 24, TimeUnit.HOURS);// 24小时 有效激活 redis保存激活码
            System.out.println(redisTemplate.opsForValue().get(email));
            userService.regist(user);
            SendEmail.sendEmailMessage(email,validateCode);
            String message = email +","+validateCode;
            model.addAttribute("message",message);
            return  "regist/registerSuccess";
        }
    }
//    匹配验证码的正确性
public int checkValidateCode(String code){
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String vercode =(String) attrs.getRequest().getSession().getAttribute("VERCODE_KEY");
        if (null == vercode){
            return -1;
        }
        if(!code.equalsIgnoreCase(vercode.toString())){
            return 0;
        }
        else
            return 1;
}

//再次发送邮件
    public Map<String,Object> sendEmail(Model model){
        Map<String ,Object> map = new HashMap<>();
        ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        String email = (String) attrs.getRequest().getAttribute("email");
        String validateCode = (String) attrs.getRequest().getAttribute("validateCode");
        SendEmail.sendEmailMessage(email,validateCode);
        map.put("success","success");
        return map;
    }

//    账号激活
    @RequestMapping("/activecode")
     public String active(Model model){
        ServletRequestAttributes attrs  = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String email  = attrs.getRequest().getParameter("email");
        String validateCode =  attrs.getRequest().getParameter("validateCode");
        String code = redisTemplate.opsForValue().get(email);
        System.out.println(code);
         User trueUser = userService.findByEmail(email);
        if (trueUser!=null&&trueUser.getState().equals("1")){
            model.addAttribute("success","用户已激活，请登录");
            return "../login";
        }
        if (code==null){
            model.addAttribute("fail","您的激活码已经过期，请重新注册");
            userService.deleteByEmail(email);
            return "regist/activeFail";
        }
        if (validateCode!=null&&validateCode.equals(code)){
            trueUser.setEnable("1");
            trueUser.setState("1");
            userService.update(trueUser);
            model.addAttribute("email",email);
            return "/regist/activeSuccess";
        }
        else{
            model.addAttribute("fail","激活码错误，请重新激活");
            return "/regist/activeFail";
        }
     }
//    检查手机号码
@RequestMapping("/checkPhone")
@ResponseBody
    public Map<String,Object> checkPhone(Model model, @RequestParam (value = "phone",required = false)String phone ){
        Map <String,Object> map = new HashMap<>();
    User user = userService.findByPhone(phone);
    if (user == null){
        map.put("message","success");
    }
    else
        map.put("message","fail");
    return map;
}

//检查邮箱
@RequestMapping("/checkEmail")
    @ResponseBody
    public Map<String,Object> checkEmail(Model model ,@RequestParam(value = "email",required = false) String email){
    Map<String,Object> map = new HashMap<>();
    User user = new User();
    user = userService.findByEmail(email);
    if (user == null){
        map.put("message","success");
    }
    else{ map.put("message","fail");}

    return map;

}

@RequestMapping("/checkCode")
    @ResponseBody
    public Map<String,Object> checkCode(Model model,@RequestParam(value = "code",required = false)String code){
    Map<String,Object> map = new HashMap<>();
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    String vcode = (String) attrs.getRequest().getSession().getAttribute(CodeCaptchaServlet.VERCODE_KEY);
    if (code.equals(vcode)){
        map.put("message","success");
    }else{
        map.put("message","failed");
    }
    return map;

}

}

