package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.pojo.Videos;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface UsersService {
    ServerResponse register(Users users);
    ServerResponse login(Users users);
    ServerResponse mine(String usersId);
    ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request);
    ServerResponse isFollowMe(String usersId,HttpServletRequest request);
    ServerResponse logout(HttpServletRequest request);
    ServerResponse followMe(String usersId,HttpServletRequest request);
    ServerResponse noFollowMe(String usersId,HttpServletRequest request);


}
