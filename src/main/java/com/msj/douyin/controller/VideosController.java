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

    //发布过的作品
    @ApiOperation(value = "发布过的作品",notes = "发布过的作品的接口")
    @PostMapping("/doSelectWork")
    public ServerResponse doSelectWork(HttpServletRequest request){
        return videosService.doSelectWork(request);
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


}
