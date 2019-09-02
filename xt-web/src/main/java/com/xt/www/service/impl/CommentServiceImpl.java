package com.xt.www.service.impl;

import com.xt.www.dao.CommentMapper;
import com.xt.www.entity.Comment;
import com.xt.www.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;


    public int add(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    public List<Comment> findAll(Long cid) {
        return commentMapper.selectAll(cid);
    }

    @Override
    public List<Comment> selectAll(Long cid) {
        return commentMapper.selectAll(cid);
    }

    @Override
    public void deleteComments(Long id) {
        commentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteChildrenComments(String child) {
        if (child != null) {
            String []str = child.split(",");
            for (int i =0 ;i<str.length;i++){
                Long temp = Long.parseLong(str[i]);
                commentMapper.deleteByPrimaryKey(temp);
            }
        }

    }

    public List<Comment> findAllFirstComment(Long cid)
    {
        return commentMapper.findAllFirstComment(cid);
    }

    public List<Comment> findAllChildrenComment(Long cid, String children) {
        return commentMapper.findAllChildrenComment(cid,children);
    }

    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insert(comment);
    }

    @Override
    public Comment findById(Long id) {
        return commentMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Comment c) {
        commentMapper.updateByPrimaryKeySelective(c);
    }


}
