package com.niecf.community.service;

import com.niecf.community.dao.UserMapper;
import com.niecf.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private  UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
