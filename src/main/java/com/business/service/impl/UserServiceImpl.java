package com.business.service.impl;

import com.business.pojo.User;
import com.business.service.UserService;
import com.mvc.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Program: simplify_mvc
 * @Description:
 * @Author: ZJamss
 * @Create: 2022-07-31 13:33
 **/
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String name, String passwd) {
        return new User(name, passwd);
    }

    @Override
    public List<User> userList() {
        List<User> list = new ArrayList<>();
        list.add(new User("test1","asikhdui"));
        list.add(new User("zjamss","没有密码"));
        list.add(new User("苦茶字","okLetsGo"));
        return list ;
    }
}
