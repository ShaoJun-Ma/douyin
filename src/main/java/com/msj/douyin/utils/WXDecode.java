package com.msj.douyin.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;

public class WXDecode {
    public static String decrypt(String encryptData, String sessionKey, String iv) throws Exception {
        // 被加密的数据
        String result = new String("");
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] encryptedByte = base64Decoder.decodeBuffer(encryptData);
            byte[] sessionKeyByte = base64Decoder.decodeBuffer(sessionKey);
            byte[] ivByte = base64Decoder.decodeBuffer(iv);
            /**
             * 以下为AES-128-CBC解密算法
             */
            SecretKeySpec skeySpec = new SecretKeySpec(sessionKeyByte, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] original = cipher.doFinal(encryptedByte);
            result = new String(original);
        } catch (Exception ex) {
            throw new Exception("Illegal Buffer");
        }
        return result;
    }
}
