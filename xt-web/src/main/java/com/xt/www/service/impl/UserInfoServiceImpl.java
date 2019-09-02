package com.xt.www.service.impl;

import com.xt.www.dao.UserInfoMapper;
import com.xt.www.entity.User;
import com.xt.www.entity.UserInfo;
import com.xt.www.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public UserInfo getUserInfo(User user) {
        Long uid = user.getId();
        UserInfo userInfo = new UserInfo();
        userInfo.setuId(uid);
        userInfo = (UserInfo) userInfoMapper.selectOne(userInfo);
        return userInfo;
    }

    @Override
    public void insertUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }
}
