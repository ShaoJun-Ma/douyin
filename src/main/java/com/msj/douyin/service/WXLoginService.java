package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;

public interface WXLoginService {
    ServerResponse wxLogin(String code, String encryptedData, String iv);
}
