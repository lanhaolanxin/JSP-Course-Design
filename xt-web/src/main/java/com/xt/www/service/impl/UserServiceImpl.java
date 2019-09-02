package com.xt.www.service.impl;

import com.xt.www.dao.UserMapper;
import com.xt.www.entity.User;
import com.xt.www.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Override
    public int regist(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User login(String email, String password) {
       User user = new User();
       user.setEmail(email);
       user.setPassword(password);
       user = userMapper.selectOne(user);
       return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return userMapper.selectOne(user);
    }

    @Override
    public User findByPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        return userMapper.selectOne(user);
    }

    @Override
    public User findById(Long id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectOne(user);
    }

    @Override
    public void deleteByEmail(String email) {
        User user = new User();
        user.setEmail(email);
        userMapper.delete(user);
    }

    @Override
    public void update(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }
}
