package com.xt.www.controller;

import com.xt.www.common.*;
import com.xt.www.entity.User;
import com.xt.www.entity.UserContent;
import com.xt.www.entity.UserInfo;
import com.xt.www.service.SolrService;
import com.xt.www.service.UserContentService;
import com.xt.www.service.UserInfoService;
import com.xt.www.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class PersonController extends BaseController {
    @Autowired
    SolrService solrService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

//    跳转到个人中心页面
    @RequestMapping("/user")
    public String toUser(Model model ){
        User user = (User) getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","您还未登陆");
            return "../login";
        }
        else
        return "profile";
    }

//    跳转到个人文章管理页面
    @RequestMapping("/personal")
    public String personal(Model model){
        User user = (User) getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","您还未登陆");
            return "../login";
        } else
        return "redirect:list";
    }

    //退出登录
    @RequestMapping("/toLogout")
    public String logout(Model model){
        User user = (User) getSession().getAttribute("usr");
        if (user==null){
            model.addAttribute("error","您还未登录");
            return "../login";
        }
        {
            getSession().invalidate();
            return "../login";
        }
    }


//    密码更新
    @RequestMapping("/updatePassword")
    public String updatePassword(Model model ,@RequestParam(value = "password",required = false)String password,
                                 @RequestParam(value ="userId",required = false)Long userId){
        User user = new User();
        user.setId(userId);
        user.setPassword(MD5Util.encodeToHex(Constants.SALT+password));
        userService.update(user);
        return "user";
    }


//    保存图片
    @RequestMapping("/saveImage")
    @ResponseBody
    public Map<String,Object> saveImage(Model model, @RequestParam(value = "url",required = false) String url) {
        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");
        System.out.println(user);
        user.setImgUrl(url);
        userService.update(user);
        map.put("msg","success");
        return map;
    }



    @Autowired
    private UserContentService userContentService;
    /**
     * 初始化个人主页数据
     * @param model
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public String findList(Model model, @RequestParam(value = "id",required = false) String id,
                           @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                           @RequestParam(value = "pageSize",required = false) Integer pageSize) {
        User user = (User)getSession().getAttribute("user");
        UserContent content = new UserContent();
        UserContent uc = new UserContent();
        if(user!=null){
            model.addAttribute( "user",user );
            content.setuId( user.getId() );
            uc.setuId(user.getId());
        }else{
            return "../login";
        }

        //查询梦分类
        List<UserContent> categorys = userContentService.findCategoryByUid(user.getId());
        model.addAttribute( "categorys",categorys );
        //发布的梦 不含私密梦
        content.setPersonal("0");
        pageSize = 4; //默认每页显示4条数据
        PageHelper.Page<UserContent> page =  findAll(content,pageNum,  pageSize); //分页

        model.addAttribute( "page",page );

        //查询私密梦
        uc.setPersonal("1");
        PageHelper.Page<UserContent> page2 =  findAll(uc,pageNum,  pageSize);
        model.addAttribute( "page2",page2 );

        //查询热梦
        UserContent uct = new UserContent();
        uct.setPersonal("0");
        PageHelper.Page<UserContent> hotPage =  findAllByUpvote(uct,pageNum,  pageSize);
        model.addAttribute( "hotPage",hotPage );
        return "personal";
    }
    /**
     * 根据分类名称查询所有文章
     * @param model
     * @param category
     * @return
     */
    @RequestMapping("/findByCategory")
    @ResponseBody
    public Map<String,Object> findByCategory(Model model, @RequestParam(value = "category",required = false) String category,@RequestParam(value = "pageNum",required = false) Integer pageNum ,
                                             @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");
        if(user==null) {
            map.put("pageCate","fail");
            return map;
        }
        pageSize = 4; //默认每页显示4条数据
        PageHelper.Page<UserContent> pageCate = userContentService.findByCategory(category,user.getId(),pageNum,pageSize);
        map.put("pageCate",pageCate);
        return map;
    }


    /**
     * 根据用户id查询私密梦
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/findPersonal")
    @ResponseBody
    public Map<String,Object> findPersonal(Model model,@RequestParam(value = "pageNum",required = false) Integer pageNum , @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");
        if(user==null) {
            map.put("page2","fail");
            return map;
        }
        pageSize = 4; //默认每页显示4条数据
        PageHelper.Page<UserContent> page = userContentService.findPersonal(user.getId(),pageNum,pageSize);
        map.put("page2",page);
        return map;
    }

    @RequestMapping("/deleteContent")
    public String deleteContent(Model model, @RequestParam(value = "cid",required = false) Long cid) {

        User user = (User)getSession().getAttribute("user");
        if(user==null) {
            return "../login";
        }
        userContentService.deleteById(cid);
        solrService.deleteById(cid);
        return "redirect:/list?manage=manage";
    }

    /**
     * 根据文章id查看文章
     * @param model
     * @param cid
     * @return
     */
    @RequestMapping("/watch")
    public String watchContent(Model model, @RequestParam(value = "cid",required = false) Long cid){
        User user = (User)getSession().getAttribute("user");
        if(user == null){
            //未登录
            model.addAttribute( "error","请先登录！" );
            return "../login";
        }
        UserContent userContent = userContentService.findById(cid);
        model.addAttribute("cont",userContent);
        return "watch";
    }

    @RequestMapping("/saveUserInfo")
    public String saveUserInfo(Model model,@RequestParam(value = "name",required = false) String name,
                               @RequestParam(value = "birthday",required = false) String birthday,
                               @RequestParam(value = "address",required = false) String address) throws ParseException {
        User user = (User) getSession().getAttribute("user");
        UserInfo userInfo = userInfoService.getUserInfo(user);

        if (user == null) {
            model.addAttribute("error","未登录");
            return "../login";
        }
        else if (userInfo!=null){
            userInfo.setuId(user.getId());
            userInfo.setName(name);
            userInfo.setAddress(address);
            userInfo.setBirthday(DateUtils.toDate(birthday));
            userInfoService.updateUserInfo(userInfo);
        }
        else {
            userInfo = new UserInfo();
            userInfo.setuId(user.getId());
            userInfo.setName(name);
            userInfo.setAddress(address);
            userInfo.setBirthday(DateUtils.toDate(birthday));
            userInfoService.insertUserInfo(userInfo);
        }

        return "profile";
    }

//    密码修改页面跳转
    @RequestMapping("/repassword")
    public String repassword(){
        return "user";
    }


}

