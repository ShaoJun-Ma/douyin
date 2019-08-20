package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.Videos;
import com.msj.douyin.service.VideosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "videos作品的接口",tags = {"videos作品的controller"})
public class VideosController {
    @Autowired
    private VideosService videosService;

    //获取作品列表
    @ApiOperation(value = "发布过的作品",notes = "发布过的作品的接口")
    @GetMapping("/doSelectWork")
    public ServerResponse doSelectWork(String usersId){
        return videosService.doSelectWork(usersId);
    }

    //上传作品
    @ApiOperation(value = "上传作品",notes = "上传作品的接口")
    @RequestMapping("/uploadVideo")
    public ServerResponse uploadVideo(MultipartFile mfile, HttpServletRequest request,
                                      Videos videosData){
        return videosService.uploadVideo(mfile,request,videosData);
    }

    //获取videos
    @ApiOperation(value = "查询videos",notes = "查询videos的接口")
    @GetMapping("/selectVideos")
    public ServerResponse selectVideos(){
        return videosService.selectVideos();
    }

    //判断是否收藏过
    @ApiOperation(value = "判断是否收藏",notes = "判断是否收藏的接口")
    @GetMapping("/isCollect")
    public ServerResponse isCollect(String videoId,HttpServletRequest request){
        return videosService.isCollect(videoId,request);
    }

    //收藏videos
    @ApiOperation(value = "收藏videos",notes = "收藏videos的接口")
    @GetMapping("/likeVideo")
    public ServerResponse likeVideo(String videoId,String publishUserId,
                                    HttpServletRequest request){
        return videosService.likeVideo(videoId,publishUserId,request);
    }

    //取消收藏video
    @ApiOperation(value = "取消收藏videos",notes = "取消收藏videos的接口")
    @GetMapping("/noLikeVideo")
    public ServerResponse noLikeVideo(String videoId,String publishUserId,
                                     HttpServletRequest request) {
        return videosService.noLikeVideo(videoId, publishUserId,request);
    }

    //获取收藏列表
    @ApiOperation(value = "收藏过的videosList",notes = "收藏过的videosList的接口")
    @GetMapping("/doSelectLike")
    public ServerResponse doSelectLike(String usersId){
        return videosService.doSelectLike(usersId);
    }

    //获取关注列表
    @ApiOperation(value="获取关注列表",notes = "获取关注列表的接口")
    @GetMapping("/doSelectFollow")
    public ServerResponse doSelectFollow(String usersId){
        return videosService.doSelectFollow(usersId);
    }
}
