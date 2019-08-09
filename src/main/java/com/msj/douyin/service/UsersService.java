package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Users;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UsersService {
    ServerResponse addUsers(Users users);
    ServerResponse findUsers(Users users);
    ServerResponse findSelf(HttpServletRequest request);
    ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request) throws IOException;
}
