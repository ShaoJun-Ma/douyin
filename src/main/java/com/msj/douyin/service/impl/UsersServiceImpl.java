package com.msj.douyin.service.impl;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.mapper.VideosMapper;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.pojo.Videos;
import com.msj.douyin.service.UsersService;
import com.msj.douyin.utils.MD5Util;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import redis.clients.jedis.Jedis;

import it.sauronsoftware.jave.Encoder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.util.*;


@Slf4j
@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersMapper usersMapper;

    //获取连接池jedis对象
    @Autowired
    private Jedis jedis;

    //注册
    @Override
    @Transactional
    public ServerResponse register(Users users) {
        String id = String.valueOf(System.currentTimeMillis());
        users.setId(id);
        users.setNickname(users.getUsername());
        //加密
        String password = users.getPassword();
        users.setPassword(MD5Util.getMD5(password+"salt"));

        int resultCount = usersMapper.insert(users);
        if(resultCount < 0){
            log.info(ResponseConst.REGISTER_ERROR_MSG);//日志：注册失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.REGISTER_ERROR_MSG);
        }
        log.info(ResponseConst.REGISTER_SUCCESS_MSG);//注册成功
        //回滚（用来测试）：数据不会更新到数据库中
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ServerResponse.createSucessByCodeMsg(ResponseConst.REGISTER_SUCCESS_MSG);
    }

    //登录
    @Override
    @Transactional
    public ServerResponse login(Users users) {
        //加密
        String password = users.getPassword();
        users.setPassword(MD5Util.getMD5(password+"salt"));
        //1、判断是否有该用户
        Users usersOne = usersMapper.selectOne(users);
        if(usersOne == null){
            log.info(ResponseConst.LOGIN_ERROR_MSG);//登录失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGIN_ERROR_MSG);
        }

        //2、将users数据放在redis缓存中   key:usersId  value:usersData
        String usersData = new Gson().toJson(usersOne);//将对象转化为json格式（json是字符串）
        String usersId = usersOne.getId();
        jedis.set(usersId,usersData);
        //归还jedis对象到连接池
        jedis.close();
        log.info(ResponseConst.LOGIN_SUCCESS_MSG);//登录成功
        return ServerResponse.createSuccess(ResponseConst.LOGIN_SUCCESS_MSG,usersId);//将usersId返回给前端
    }

    //个人信息
    @Override
    @Transactional
    public ServerResponse mine(HttpServletRequest request) {
        String usersId = request.getHeader("usersId");
        String users = jedis.get(usersId);
        if(users == null){
            log.info(ResponseConst.NEED_LOGIN);//用户未登录
            return ServerResponse.createErrorCodeMsg(ResponseConst.NEED_LOGIN);
        }
        Users usersData = new Gson().fromJson(users, Users.class);
        log.info(usersData.toString());//个人信息
        log.info(ResponseConst.MINE_SUCCESS_MSG);//获取个人信息成功
        return ServerResponse.createSuccess(ResponseConst.MINE_SUCCESS_MSG,usersData);
    }

    //上传头像
    @Transactional
    @Override
    public ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request){
        //新建文件夹及文件
        File file = new File("D:/douyin/images/"+mfile.getOriginalFilename());
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        try {
            //MultipartFile转file
            mfile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String usersId = request.getHeader("usersId");
        String users = jedis.get(usersId);
        Users usersOne = new Gson().fromJson(users, Users.class);
        String imagePath = "/"+mfile.getOriginalFilename();
        usersOne.setFaceImage(imagePath);
        synchronized (usersOne){
            //进行两个操作，加锁机制
            usersMapper.updateByPrimaryKeySelective(usersOne);
            String usersData = new Gson().toJson( usersMapper.selectOne(usersOne));
            jedis.set(usersId,usersData);
        }
        log.info(ResponseConst.UPDATE_IMAGE_SUCCESS);//更新头像成功
        return ServerResponse.createSuccess(ResponseConst.UPDATE_IMAGE_SUCCESS,imagePath);
    }

    //退出登录
    @Transactional
    @Override
    public ServerResponse logout(HttpServletRequest request){
        String usersId = request.getHeader("usersId");
        Long resultCount = jedis.del(usersId);
        if(resultCount <= 0){
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGOUT_ERROR);
        }
        return ServerResponse.createErrorCodeMsg(ResponseConst.LOGOUT_SUCCESS);
    }







}
