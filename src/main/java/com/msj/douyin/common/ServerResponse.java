package com.msj.douyin.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data //等同于get(),set()方法
@JsonInclude(JsonInclude.Include.NON_NULL)//保证序列化Json的时候，如果值为null，会不显示这个key
public class ServerResponse{
    private int status;
    private String msg;
    private Object data;


    public ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ServerResponse(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static ServerResponse createSucessByCodeMsg(String msg){
        return new ServerResponse(ResponseConst.SUCCESS_CODE,msg);

    }

    public static ServerResponse createErrorCodeMsg(String msg){
        return new ServerResponse(ResponseConst.ERROR_CODE,msg);
    }

    public static ServerResponse createSuccess(String msg,Object data){
        return new ServerResponse(ResponseConst.SUCCESS_CODE,msg,data);
    }

    public static ServerResponse createFail(String msg,Object data){
        return new ServerResponse(ResponseConst.ERROR_CODE,msg,data);
    }
}
