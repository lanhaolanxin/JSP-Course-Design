package com.xt.www.controller;

import com.xt.www.common.BaseController;
import com.xt.www.common.DateUtils;
import com.xt.www.common.PageHelper.Page;
import com.xt.www.common.StringUtil;
import com.xt.www.entity.Comment;
import com.xt.www.entity.Upvote;
import com.xt.www.entity.User;
import com.xt.www.entity.UserContent;
import com.xt.www.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class IndexJspController extends BaseController {
    @Autowired
    UserService userService;

    @Autowired
    UpvoteService upvoteService;

    @Autowired
    SolrService solrService;
    private final static Logger log = Logger.getLogger(IndexJspController.class);

    @Autowired
    UserContentService userContentService;
    @RequestMapping("/index_list")
    public String findAllList(Model model, @RequestParam(value = "keyword",required = false) String keyword,@RequestParam(value = "id",required = false) String id ,
                              @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                              @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        log.info( "===========进入index_list=========" );
        User user = (User)getSession().getAttribute("user");
        if(user!=null){
            model.addAttribute( "user",user );
        }
        if(StringUtils.isNotBlank(keyword)) {
            Page<UserContent> page = solrService.findByKeyWords(keyword, pageNum, pageSize);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
        }
        else {
        Page<UserContent> page =  findAll(null,pageNum,  pageSize);
        model.addAttribute( "page",page );
        }
        return "../index";
    }


    //    文章投票
    @RequestMapping("/upvote")
    @ResponseBody
    public Map<String,Object> upvote(Model model, @RequestParam(value = "id",required = false) long id,
                                     @RequestParam(value = "uid",required = false) Long ud,
                                     @RequestParam(value = "upvote",required = false) int upvote) {
        log.info( "id="+id+",uid="+ud+"upvote="+upvote );
        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");

        if(user == null){
            map.put( "data","fail" );
            return map;
        }
        Upvote up = new Upvote();
        up.setContentId( id );
        up.setuId( user.getId() );
        Upvote upv = upvoteService.findByUidAndConid( up );
        if(upv!=null){
            log.info( upv.toString()+"============" );
        }
        UserContent userContent =   userContentService.findById( id );
        if(upvote == -1){
            if(upv != null ){
                if( "1".equals( upv.getDownvote() ) ){
                    map.put( "data","down" );
                    return map;
                }else {
                    upv.setDownvote( "1" );
                    upv.setUpvoteTime( new Date(  ) );
                    upv.setIp( getClientIpAddress() );
                    upvoteService.update(upv);
                }

            }else {
                up.setDownvote( "1" );
                up.setUpvoteTime( new Date(  ) );
                up.setIp( getClientIpAddress() );
                upvoteService.add(up);
            }

            userContent.setDownvote( userContent.getDownvote()+upvote);
        }else {
            if(upv != null){
                if( "1".equals( upv.getUpvote() ) ){
                    map.put( "data","done" );
                    return map;
                }else {
                    upv.setUpvote( "1" );
                    upv.setUpvoteTime( new Date(  ) );
                    upv.setIp( getClientIpAddress() );
                    upvoteService.update(upv);
                }

            }else {
                up.setUpvote( "1" );
                up.setUpvoteTime( new Date(  ) );
                up.setIp( getClientIpAddress() );
                upvoteService.add(up);
            }


            userContent.setUpvote( userContent.getUpvote() + upvote );
        }
        userContentService.updateById( userContent );
        map.put( "data","success" );
        return map;

    }
    @Autowired
    CommentService commentService;


    @RequestMapping("/reply")
    @ResponseBody
    public Map<String,Object> reply(Model model, @RequestParam(value = "content_id",required = false) Long content_id) {
        Map map = new HashMap<String,Object>(  );
        List<Comment> list = commentService.findAllFirstComment(content_id);
        if(list!=null && list.size()>0){
            for(Comment c:list){
                List<Comment> coments = commentService.findAllChildrenComment( c.getConId(), c.getChildren() );
                if(coments!=null && coments.size()>0){
                    for(Comment com:coments){
                        if(com.getById()!=null ){
                            User byUser = userService.findById( com.getById() );
                            com.setByUser( byUser );
                        }

                    }
                }
                c.setComList( coments );
            }
        }

        map.put( "list",list );

        return map;

    }

    @RequestMapping("/comment")
    @ResponseBody
    public Map<String,Object> comment(Model model, @RequestParam(value = "id",required = false) Long id ,
                                      @RequestParam(value = "content_id",required = false) Long content_id ,
                                      @RequestParam(value = "uid",required = false) Long uid ,
                                      @RequestParam(value = "by_id",required = false) Long bid ,
                                      @RequestParam(value = "oSize",required = false) String oSize,
                                      @RequestParam(value = "comment_time",required = false) String comment_time,
                                      @RequestParam(value = "upvote",required = false) Integer upvote) throws ParseException {
        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");
        if(user == null){
            map.put( "data","fail" );
            return map;
        }
        if(id==null ){

            Date date = DateUtils.toDate( comment_time);

            Comment comment = new Comment();
            comment.setComContent( oSize );
            comment.setCommTime( date );
            comment.setConId( content_id );
            comment.setComId( uid );
            if(upvote==null){
                upvote = 0;
            }
            comment.setById( bid );
            comment.setUpvote( upvote );
            User u = userService.findById( uid );
            comment.setUser( u );
            commentService.add( comment );
            map.put( "data",comment );

            UserContent userContent = userContentService.findById( content_id );
            Integer num = userContent.getCommentNum();
            userContent.setCommentNum( num+1 );
            userContentService.updateById( userContent );

        }else {
            //点赞
            Comment c = commentService.findById( id );
            c.setUpvote( upvote );
            commentService.update( c );


        }

        return map;

    }
    @RequestMapping("/deleteComment")
    @ResponseBody
    public Map<String,Object> deleteComment(Model model, @RequestParam(value = "id",required = false)Long id,
                                            @RequestParam(value = "uid",required = false) Long uid,
                                            @RequestParam(value = "con_id",required = false) Long con_id,
                                            @RequestParam(value = "fid",required = false) Long fid){
       //初始化删除的数量num变量
        int num = 0;
        Map map = new HashMap<String,Object>();
        User user = (User) getSession().getAttribute("user");
        UserContent userContent = userContentService.findById(con_id);
        if (user==null){
            map.put("data","fail");
            return map;
        }
        else {
            if (user.getId().equals(uid)){
                Comment comment  = commentService.findById(id);
                //该评论不为父评论时
              if (StringUtil.isBlank(comment.getChildren())){
                  if (fid!=null){
                      Comment fatherComment = commentService.findById(fid);
                      String newChild = StringUtil.deleteId(fatherComment.getChildren(),id);
                      fatherComment.setChildren(newChild);
                      commentService.update(fatherComment);

                  }
                    commentService.deleteComments(id);
                    num++;
              }
                  else {
                      String child = comment.getChildren();
                      commentService.deleteChildrenComments(child);
                      commentService.deleteComments(id);
                      if (child != null) {
                          String []arr = child.split(",");
                          //删除的评论数量等于子评论数量+1
                          num =num+(arr.length+1);
                      }
                  }
                  if (userContent != null) {
                      if (userContent.getCommentNum()-num>=0){
                          userContent.setCommentNum(userContent.getCommentNum()-num);
                      }
                      else{
                          userContent.setCommentNum(0);
                      }
                      userContentService.updateById(userContent);
                  }
                  map.put("data",userContent.getCommentNum());
              }
            else {
                map.put("data","no-access");
                 }
            }

        return map;
        }






    /*
    * id评论id
    * content_id文章id
    * uid评论用户id
    * by_id被评论者id
    * oSize评论内容
    * comment_time评论时间
    * upvote点赞数
    * */

    @RequestMapping("/comment_child")
    @ResponseBody
    public Map<String,Object> addCommentChild(Model model, @RequestParam(value = "id",required = false) Long id ,
                                              @RequestParam(value = "content_id",required = false) Long content_id ,
                                              @RequestParam(value = "uid",required = false) Long uid ,
                                              @RequestParam(value = "by_id",required = false) Long bid ,
                                              @RequestParam(value = "oSize",required = false) String oSize,
                                              @RequestParam(value = "comment_time",required = false) String comment_time,
                                              @RequestParam(value = "upvote",required = false) Integer upvote) throws ParseException {
        Map map = new HashMap<String,Object>();
        User user = (User)getSession().getAttribute("user");
        if(user == null){
            map.put( "data","fail" );
            return map;
        }
        Date date = DateUtils.toDate(comment_time);
        Comment comment = new Comment();
        comment.setComContent(oSize);
        comment.setCommTime(date);
        comment.setConId(content_id);
        comment.setComId(uid);
        if (upvote==null){
            upvote = 0;
        }
        comment.setUpvote(upvote);
        comment.setById(bid);
        User commentUser = userService.findById(uid);
        comment.setUser(commentUser);
        commentService.add(comment);

        //根据父评论的id查找出父评论对象
        Comment com = commentService.findById(id);
        if (StringUtil.isBlank(com.getChildren())){
            com.setChildren(comment.getId().toString());
        }else {
            com.setChildren(com.getChildren()+","+comment.getId());
        }
        commentService.update(com);
        map.put("data",comment);

        UserContent userContent = userContentService.findById(content_id);
        Integer num = userContent.getCommentNum();
        userContent.setCommentNum(num+1);
        userContentService.updateById(userContent);
        return map;


    }



    }
