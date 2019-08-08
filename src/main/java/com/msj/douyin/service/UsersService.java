package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Users;

import javax.servlet.http.HttpSession;

public interface UsersService {
    ServerResponse addUsers(Users users);
    ServerResponse findUsers(Users users);
    ServerResponse findSelf(String key);
}
