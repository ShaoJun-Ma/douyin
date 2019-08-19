package com.msj.douyin.common;

public class ResponseConst {
    public final static int ERROR_CODE = 0;  //错误码
    public final static int SUCCESS_CODE = 1; //成功码
    public final static int IS_CODE = 2; //已经存在

    public final static String REGISTER_SUCCESS_MSG = "注册成功";
    public final static String REGISTER_ERROR_MSG = "注册失败";
    public final static String IS_REGISTER = "该用户已经注册";

    public final static String LOGIN_SUCCESS_MSG = "登录成功";
    public final static String LOGIN_ERROR_MSG = "登录失败";

    public final static String MINE_SUCCESS_MSG = "获取个人信息成功";
    public final static String MINE_ERROR_MSG = "获取个人信息失败";

    public final static String NEED_LOGIN = "用户未登录";

    public final static String UPDATE_IMAGE_SUCCESS = "更新头像成功";

    public final static String LOGOUT_SUCCESS = "退出登录成功";
    public final static String LOGOUT_ERROR = "退出登录失败";

    public final static String SELECT_WORK_SUCCESS = "获取作品成功";
    public final static String SELECT_WORK_ERROR = "获取作品失败";

    public final static String SELECT_USERS_ERROR = "无该用户信息";
    public final static String SELECT_USERS_SUCCESS = "成功获取用户信息";

    public final static String UPLOAD_WORK_SUCCESS = "上传作品成功";
    public final static String UPLOAD_WORK_ERROR = "上传作品失败";

    public final static String SELECT_BGM_SUCCESS = "获取Bgm成功";
    public final static String SELECT_BGM_ERROR = "获取Bgm失败";

    public final static String BGM_PATH = "127.0.0.1:8080/bgms";
    public final static String IMAGES_PATH = "../../127.0.0.1:8080/images";

    public final static String SELECT_VIDEOS_SUCCESS = "查看videos成功";
    public final static String SELECT_VIDEOS_ERROR = "查看videos失败";

    public final static String LIKE_VIDEO_SUCCESS = "收藏成功";
    public final static String LIKE_VIDEO_ERROR = "收藏失败";

    public final static String NO_LIKE_VIDEO_SUCCESS = "取消收藏";
    public final static String NO_LIKE_VIDEO_ERROR = "取消收藏失败";

    public final static String IS_COLLECT = "已经收藏";
    public final static String NOT_COLLECT = "还没收藏";

    public final static String ADD_ReceiveLikeCounts_SUCCESS = "users表增加收藏数成功";
    public final static String ADD_ReceiveLikeCounts_ERROR = "users表增加收藏数失败";

    public final static String DEL_ReceiveLikeCounts_SUCCESS = "users表减少收藏数成功";
    public final static String DEL_ReceiveLikeCounts_ERROR = "users表减少收藏数失败";

    public final static String FOLLOW_ME_SUCCESS = "关注成功";
    public final static String FOLLOW_ME_ERROR = "关注失败";

    public final static String CANCEL_FOLLOW_ME_SUCCESS = "取消关注成功";
    public final static String CANCEL_FOLLOW_ME_ERROR = "取消关注失败";

    public final static String ADD_USERS_FANS_SUCCESS = "增加粉丝成功";
    public final static String ADD_USERS_FANS_ERROR = "增加粉丝失败";

    public final static String ADD_USERS_FOLLOW_SUCCESS = "增加关注成功";
    public final static String ADD_USERS_FOLLOW_ERROR = "增加关注失败";

    public final static String DEL_USERS_FANS_SUCCESS = "删除粉丝成功";
    public final static String DEL_USERS_FANS_ERROR = "删除粉丝失败";

    public final static String SELECT_USERS_FANS_ERROR = "该数据不存在";
    public final static String SELECT_USERS_FANS_SUCCESS = "已经关注过";


    public final static String SELECT_ERROR = "查询失败";
    public final static String SELECT_SUCCESS = "查询成功";
}
