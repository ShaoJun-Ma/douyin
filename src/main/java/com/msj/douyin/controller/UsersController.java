package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.pojo.Videos;
import com.msj.douyin.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class UsersController {
    @Autowired
    private UsersService usersService;

    //注册
    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/register")
    //从前端传过来的Users是json格式，需要加@RequestBody
    public ServerResponse register(@RequestBody Users users){
        return usersService.register(users);
    }

    //登录
    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public ServerResponse login(@RequestBody Users users){
        return usersService.login(users);
    }

    //个人信息
    @ApiOperation(value = "个人信息",notes = "个人信息的接口")
    @GetMapping("/mine")
    public ServerResponse mine(String usersId){
        return usersService.mine(usersId);
    }

    //上传头像
    @ApiOperation(value = "上传头像",notes = "上传头像的接口")
    @PostMapping("/changeFace")
    public ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request) throws IOException {
        return usersService.changeFace(mfile,request);
    }

    //退出登录
    @ApiOperation(value = "退出登录",notes = "退出登录的接口")
    @PostMapping("/logout")
    public ServerResponse logout(HttpServletRequest request){
        return usersService.logout(request);
    }

    //查看是否关注过
    @ApiOperation(value = "查看是否关注过",notes = "查看是否关注过的接口")
    @PostMapping("/isFollowMe")
    public ServerResponse isFollowMe(String usersId,HttpServletRequest request){
        return usersService.isFollowMe(usersId,request);
    }

    //关注我
    @ApiOperation(value = "关注我",notes = "关注我的接口")
    @PostMapping("/followMe")
    public ServerResponse followMe(String usersId,HttpServletRequest request){
        return usersService.followMe(usersId,request);
    }

    //取消关注
    @ApiOperation(value = "取消关注",notes = "取消关注的接口")
    @PostMapping("/noFollowMe")
    public ServerResponse noFollowMe(String usersId,HttpServletRequest request){
        return usersService.noFollowMe(usersId,request);
    }




}
