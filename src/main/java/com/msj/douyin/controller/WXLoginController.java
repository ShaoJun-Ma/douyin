package com.msj.douyin.controller;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.model.WXSessionModel;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.service.WXLoginService;
import com.msj.douyin.utils.HttpClientUtil;
import com.msj.douyin.utils.JsonUtil;
import com.msj.douyin.utils.WXDecode;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WXLoginController {
    @Autowired
    private WXLoginService wxLoginService;


    @PostMapping("/wxLogin")
    public ServerResponse wxLogin(String code,String encryptedData,String iv) {
        return wxLoginService.wxLogin(code, encryptedData, iv);
    }
}
