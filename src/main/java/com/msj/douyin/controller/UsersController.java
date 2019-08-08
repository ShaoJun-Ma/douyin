package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.rmi.ServerError;

@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class UsersController {
    @Autowired
    private UsersService usersService;

    //注册
    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/register")
    //post请求对象加@RequestBody
    public ServerResponse register(@RequestBody Users users){
        return usersService.addUsers(users);
    }

    //登录
    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public ServerResponse login(@RequestBody Users users){
        return usersService.findUsers(users);
    }

    //个人信息
    @ApiOperation(value = "个人信息",notes = "个人信息的接口")
    @GetMapping("/mine")
    public ServerResponse mine(String key){
        return usersService.findSelf(key);
    }

    //上传头像
    @ApiOperation(value = "上传头像",notes = "上传头像的接口")
    @PostMapping("/changeFace")
    public void changeFace(MultipartFile mfile) throws IOException {
        File file = new File("D:/douyin/"+mfile.getOriginalFilename()+".jpg");
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        mfile.transferTo(file);
    }


}
