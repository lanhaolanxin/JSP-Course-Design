package com.xt.www.controller;

import com.xt.www.common.BaseController;
import com.xt.www.common.PageHelper;
import com.xt.www.entity.User;
import com.xt.www.entity.UserContent;
import com.xt.www.service.UserContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ArticleManageController extends BaseController {
    @Autowired
    UserContentService userContentService;
    @RequestMapping("/myArticle")
    public String myArticle(Model model, @RequestParam(value = "id",required = false) String id,
                            @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                            @RequestParam(value = "pageSize",required = false) Integer pageSize){
        User user = (User) getSession().getAttribute("user");
        UserContent content = new UserContent();
        if (user==null){
            model.addAttribute("error","请先登录");
            return "../login";
        }
        content.setPersonal("0");
        pageSize = 4; //默认每页显示4条数据
        content.setuId(user.getId());
        PageHelper.Page<UserContent> page1 =  findAll(content,pageNum,  pageSize); //分页
        model.addAttribute( "page",page1 );
        return "personal";


    }
}


