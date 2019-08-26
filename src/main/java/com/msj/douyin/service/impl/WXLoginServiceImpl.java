package com.msj.douyin.service.impl;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.model.WXSessionModel;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.service.WXLoginService;
import com.msj.douyin.utils.FileUtil;
import com.msj.douyin.utils.HttpClientUtil;
import com.msj.douyin.utils.JsonUtil;
import com.msj.douyin.utils.WXDecode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Service
public class WXLoginServiceImpl implements WXLoginService {
    @Autowired
    private Jedis jedis;
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public ServerResponse wxLogin(String code, String encryptedData, String iv) {
        //得到的字符串用“+”代替“%2B”
        encryptedData = StringUtils.replace(encryptedData, "%2B", "+");
        //model包含 唯一标识用户的id(key) 和 用户的信息(value)
        WXSessionModel model = getModel(code);

        if (model.getOpenid() == null) {
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGIN_ERROR_MSG);
        }
        //存入session到redis
        String sessionKey = model.getSession_key();

        String decrypt = null;
        try {
            decrypt = WXDecode.decrypt(encryptedData, sessionKey, iv);
            Map<String, String> users = new Gson().fromJson(decrypt, Map.class);
            if (users != null) {
                addUsers(users,model.getOpenid());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ServerResponse.createSuccess(ResponseConst.LOGIN_SUCCESS_MSG, model.getOpenid());
    }

    //得到用户的唯一标识id和用户的信息
    private WXSessionModel getModel(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> param = new HashMap<>();
        param.put("appid", "wxa18ef52cec7a99ef");
        param.put("secret", "ba6460a5a14b6e12f34f1f1d59b77ce1");
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");
        String wxResult = HttpClientUtil.doGet(url, param);
        WXSessionModel model = JsonUtil.jsonToPojo(wxResult, WXSessionModel.class);
        return model;
    }

    //添加用户信息
    private void addUsers(Map<String, String> users,String openId) {
        Users usersOne = new Users();
        usersOne.setId(users.get("openId"));
        usersOne.setUsername(users.get("nickName"));
        usersOne.setNickname(users.get("nickName"));
        usersOne.setFaceImage(users.get("avatarUrl"));
        usersOne.setFansCounts(0);
        usersOne.setReceiveLikeCounts(0);
        usersOne.setFollowCounts(0);

        String url = users.get("avatarUrl");
        String method = "GET";
        String filePath = "D:\\douyin\\images\\";
        String imageUrl = null;
        try {
            imageUrl = FileUtil.saveUrlAs(url, filePath, method);
        } catch (Exception e) {
            e.printStackTrace();
        }
        usersOne.setFaceImage(imageUrl);
        if(usersOne != null){
            jedis.set(openId,users.toString());
        }
        usersMapper.insert(usersOne);
    }

}

