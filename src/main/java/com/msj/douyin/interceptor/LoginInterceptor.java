package com.msj.douyin.interceptor;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.utils.JedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor{
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Gson gson = new Gson();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        //1、通过key:usersId获取value
        String usersId = request.getHeader("usersId");
        long start = System.currentTimeMillis();
        //拦截：usersId为空  或者  通过usersId取出来的数据为空
        if(StringUtils.isBlank(usersId)){//usersId为空
            log.info(ResponseConst.NEED_LOGIN);//用户未登录
            String needLogin = gson.toJson(ServerResponse.createErrorCodeMsg(ResponseConst.NEED_LOGIN));
            response.getWriter().write(needLogin);
            log.info("运行时间: {}",System.currentTimeMillis()-start);
            return false;
        }else{
            //2、获取连接池jedis对象
            Jedis jedis = JedisPoolUtil.getJedis();
            String users = jedis.get(usersId);  //通过usersId获取用户个人信息
            jedis.close();
            if(users == null){//users是从redis里面取出来的
                Users usersOne = usersMapper.selectByPrimaryKey(usersId);//usersOne是从数据库里面取出来的
                if(usersOne == null){
                    log.info(ResponseConst.SELECT_USERS_ERROR);//无该用户信息
                    //write()里面的参数是String，需要将对象转为json格式（String类型）
                    String needLogin = gson.toJson(ServerResponse.createErrorCodeMsg(ResponseConst.NEED_LOGIN));//用户未登录
                    response.getWriter().write(needLogin);
                    log.info("运行时间: {}",System.currentTimeMillis()-start);
                    return false;
                }
            }
        }

        //放行
        log.info("{}",ServerResponse.createSucessByCodeMsg(ResponseConst.LOGIN_SUCCESS_MSG));//登录成功
        log.info("运行时间: {}",System.currentTimeMillis()-start);
        return true;

    }
}
