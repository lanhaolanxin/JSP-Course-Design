package com.xt.www.service;

import com.xt.www.entity.User;
import com.xt.www.entity.UserInfo;

public interface UserInfoService {
    void insertUserInfo(UserInfo userInfo);
    UserInfo getUserInfo(User user);
    void updateUserInfo(UserInfo userInfo);
}
