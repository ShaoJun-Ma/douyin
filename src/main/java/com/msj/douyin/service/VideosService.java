package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Videos;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface VideosService {
    ServerResponse doSelectWork(HttpServletRequest request);
    ServerResponse uploadVideo(MultipartFile mfile, HttpServletRequest request, Videos videosData);
    ServerResponse selectVideos();
    ServerResponse isCollect(String videoId,HttpServletRequest request);
    ServerResponse likeVideo(String videoId,String publishUserId,HttpServletRequest request);
    ServerResponse noLikeVideo(String videoId,String publishUserId,HttpServletRequest request);
}
