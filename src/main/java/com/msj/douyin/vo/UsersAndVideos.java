package com.msj.douyin.vo;

import com.msj.douyin.pojo.Users;
import com.msj.douyin.pojo.Videos;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "用户和videos的组合对象",description = "这是用户和videos的组合对象")
public class UsersAndVideos {
    private Users users;
    private Videos videos;

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }
}
