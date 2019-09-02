package com.xt.www.service;

import com.xt.www.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentService {
//    根据文章id查出所有评论
    List<Comment> selectAll(@Param("cid") Long cid);
//    根据文章id查出所有一级评论
    List<Comment> findAllFirstComment(@Param("cid") Long cid);
//    根据文章id和二级评论ids查询出所有评论
    List<Comment> findAllChildrenComment(@Param("cid") Long cid,@Param("children") String children);
//    插入评论并返回主键id 返回值是影响行数 id在Comment对象中
    int insertComment(Comment comment);


    Comment findById(Long id);

    void update(Comment c);

    int add(Comment comment);

    void deleteChildrenComments(String child);

    void deleteComments(Long id);
}

