package com.msj.douyin.service.impl;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.service.UsersService;
import com.msj.douyin.utils.JedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;


@Slf4j
@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersMapper usersMapper;

    //获取连接池jedis对象
    @Autowired
    private Jedis jedis;

    @Transactional
    @Override
    public ServerResponse addUsers(Users users) {
        String id = String.valueOf(System.currentTimeMillis());
        users.setId(id);
        users.setNickname(users.getUsername());
        int resultCount = usersMapper.insert(users);
        if(resultCount < 0){
            return ServerResponse.createErrorCodeMsg(ResponseConst.REGISTER_ERROR_MSG);

        }
        log.info("注册成功");
        //回滚（用来测试）
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        ServerResponse sucess = ServerResponse.createSucessByCodeMsg(ResponseConst.REGISTER_SUCCESS_MSG);
        return sucess;
    }

    @Transactional
    @Override
    public ServerResponse findUsers(Users users) {
        Users usersOne = usersMapper.selectOne(users);
        if(usersOne == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGIN_ERROR_MSG);
        }
        //将对象转化为json格式（json是字符串）
        String value = new Gson().toJson(usersOne);
        Long key = System.currentTimeMillis();
        jedis.set(String.valueOf(key),value);//这里需要的参数都是字符串类型
        //归还jedis对象到连接池
        jedis.close();
        return ServerResponse.createSuccess(ResponseConst.LOGIN_SUCCESS_MSG,key);
    }

    @Transactional
    @Override
    public ServerResponse findSelf(HttpServletRequest request) {
        String key = request.getHeader("userId");
        String users = jedis.get(key);
        Users data = new Gson().fromJson(users, Users.class);
        return ServerResponse.createSuccess(ResponseConst.MINE_SUCCESS_MSG,data);//获取个人信息成功
    }

    @Override
    public ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request) throws IOException {
        File file = new File("D:/douyin/"+mfile.getOriginalFilename());
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        mfile.transferTo(file);
        String key = request.getHeader("userId");
        String users = jedis.get(key);
        Users usersOne = new Gson().fromJson(users, Users.class);
        usersOne.setFaceImage("/"+mfile.getOriginalFilename());
        usersMapper.updateByPrimaryKeySelective(usersOne);
        String value = new Gson().toJson(usersOne);
        jedis.set(key,value);
        return null;
    }
}
