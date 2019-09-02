package com.xt.www.service.impl;

import com.xt.www.dao.UpvoteMapper;
import com.xt.www.entity.Upvote;
import com.xt.www.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpvoteServiceImpl implements UpvoteService {
    @Autowired
    UpvoteMapper upvoteMapper;

    @Override
    public void add(Upvote up) {
        upvoteMapper.insert(up);
    }

    @Override
    public void update(Upvote upv) {
        upvoteMapper.updateByPrimaryKey(upv);
    }

    @Override
    public Upvote findByUidAndConid(Upvote upvote) {
       Upvote up = (Upvote) upvoteMapper.selectOne(upvote);
       return up;
    }
}
