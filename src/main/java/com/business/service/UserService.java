package com.business.service;

import com.business.pojo.User;

import java.util.List;

public interface UserService {
    User getUser(String name,String passwd);
    List<User> userList();
}
