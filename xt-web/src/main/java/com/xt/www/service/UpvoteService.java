package com.xt.www.service;

import com.xt.www.entity.Upvote;

public interface UpvoteService {
    Upvote findByUidAndConid(Upvote upvote);

    void update(Upvote upv);

    void add(Upvote up);
}
