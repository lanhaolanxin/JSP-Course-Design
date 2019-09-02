package com.xt.www.controller;

import com.xt.www.common.BaseController;
import com.xt.www.entity.User;
import com.xt.www.entity.UserContent;
import com.xt.www.service.SolrService;
import com.xt.www.service.UserContentService;
import com.xt.www.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class WriteController extends BaseController {
    @Autowired
    UserService userService;
    @Autowired
    SolrService solrService;
    @RequestMapping("/write")
    public String writedream(Model model) {
        User user = (User) getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","请先登录");
            return "../login";
        }
        model.addAttribute("user", user);
        return "load";
    }

    @Autowired
    private UserContentService userContentService;
    @RequestMapping("/doWritedream")
    public String doWritedream(Model model, @RequestParam(value = "id",required = false) String id,
                               @RequestParam(value = "cid",required = false) Long cid,
                               @RequestParam(value = "category",required = false) String category,
                               @RequestParam(value = "txtT_itle",required = false) String txtT_itle,
                               @RequestParam(value = "content",required = false) String content,
                               @RequestParam(value = "private_dream",required = false) String private_dream) {
        User user = (User)getSession().getAttribute("user");
        UserContent userContent = new UserContent();

        if(user == null){
            //未登录
            model.addAttribute( "error","请先登录！" );
            return "../login";
        }
        userContent.setContent( content );
        userContent.setRptTime( new Date(  ) );
        if(cid!=null){
            userContent = userContentService.findById(cid);
        }
        userContent.setCategory( category );
        userContent.setContent( content );
        userContent.setRptTime( new Date(  ) );
        String imgUrl = user.getImgUrl();
        if(StringUtils.isBlank( imgUrl )){
            userContent.setImgUrl( "/images/icon_m.jpg" );
        }else {
            userContent.setImgUrl( imgUrl );
        }
        if("on".equals( private_dream )){
            userContent.setPersonal( "1" );
        }else{
            userContent.setPersonal( "0" );
        }
        if(cid ==null){
        userContent.setNickName(user.getNickName());
        userContent.setTitle( txtT_itle );
        userContent.setCategory(category);
        userContent.setuId( user.getId() );
        userContent.setUpvote( 0 );
        userContent.setDownvote( 0 );
        userContent.setCommentNum( 0 );
        userContentService.addContent( userContent );
        UserContent newUserContent = userContentService.findByUserContentName(userContent.getTitle());
        solrService.addUserContent(newUserContent);
        }
        else {
            userContentService.updateById(userContent);
            solrService.updateUserContent(userContent);
        }
        model.addAttribute("content",userContent);
        return "writesuccess";
    }


    @RequestMapping("/rewrite")
    public String rewrite(Model model,@RequestParam(value = "cid",required = false) Long cid) {
        System.out.println(cid);
        User user = (User) getSession().getAttribute("user");
        if(cid!=null){
            UserContent content = userContentService.findById(cid);
            model.addAttribute("cont",content);
        }
        model.addAttribute("user", user);
        return "load";
    }



}
