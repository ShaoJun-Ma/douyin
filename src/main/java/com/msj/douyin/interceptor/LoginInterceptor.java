package com.msj.douyin.interceptor;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.utils.JedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Gson gson = new Gson();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        //1、通过key=userId获取value
        String value = request.getHeader("userId");

        if(StringUtils.isBlank(value)){
            String needLogin = gson.toJson(ServerResponse.createErrorCodeMsg(ResponseConst.NEED_LOGIN));//用户未登录
            response.getWriter().write(needLogin);
            return false;
        }else{
            //2、获取连接池jedis对象
            Jedis jedis = JedisPoolUtil.getJedis();
            String users = jedis.get(value);  //通过value获取用户个人信息
            if(users == null){
                //write()里面的参数是String，需要将对象转为json格式（String类型）
                String needLogin = gson.toJson(ServerResponse.createErrorCodeMsg(ResponseConst.NEED_LOGIN));//用户未登录
                response.getWriter().write(needLogin);
                return false;
            }
        }


        log.info("{}",ServerResponse.createSucessByCodeMsg(ResponseConst.LOGIN_SUCCESS_MSG));//登录成功
        return true;  //放行

    }
}