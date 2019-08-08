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
import redis.clients.jedis.Jedis;



@Slf4j
@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersMapper usersMapper;

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
        //获取连接池jedis对象
        Jedis jedis = JedisPoolUtil.getJedis();
        Long key = System.currentTimeMillis();
        jedis.set(String.valueOf(key),value);//这里需要的参数都是字符串类型
        //归还jedis对象到连接池
        jedis.close();
        return ServerResponse.createSuccess(ResponseConst.LOGIN_SUCCESS_MSG,key);
    }

    @Transactional
    @Override
    public ServerResponse findSelf(String key) {
        //获取连接池jedis对象
        Jedis jedis = JedisPoolUtil.getJedis();
        String users = jedis.get(key);
        Users data = new Gson().fromJson(users, Users.class);
        return ServerResponse.createSuccess(ResponseConst.MINE_SUCCESS_MSG,data);
    }
}
