package com.msj.douyin.service.impl;

import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.VideosMapper;
import com.msj.douyin.pojo.Videos;
import com.msj.douyin.service.VideosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideosServiceImpl implements VideosService{
    @Autowired
    private VideosMapper videosMapper;

    //获取连接池jedis对象
    @Autowired
    private Jedis jedis;


    //发布过的作品
    @Transactional
    @Override
    public ServerResponse doSelectWork(HttpServletRequest request){
        String usersId = request.getHeader("usersId");
        Videos videos = new Videos();
        videos.setUserId(usersId);
        List<Videos> videoList = videosMapper.select(videos);
        if(videoList == null){
            log.info(ResponseConst.SELECT_WORK_ERROR);//获取作品成功
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_WORK_ERROR);
        }
        log.info(ResponseConst.SELECT_WORK_SUCCESS);//获取作品失败
        return ServerResponse.createSuccess(ResponseConst.SELECT_WORK_SUCCESS,videoList);
    }

    //上传作品
    @Override
    public ServerResponse uploadVideo(MultipartFile mfile, HttpServletRequest request,
                                      Videos videosData) {
        //将上传视频放在D:/douyin/videos/下面
        File file = new File("D:/douyin/videos/" + mfile.getOriginalFilename());
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        try {
            //将前端传来的mfile放在新的file中
            mfile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取新增videos信息
        Videos videos = addVideos(mfile,request,videosData);
        int resultCount = videosMapper.insert(videos);
        if(resultCount <= 0){
            log.info(ResponseConst.UPLOAD_WORK_ERROR);//上传作品失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.UPLOAD_WORK_ERROR);
        }
        log.info(ResponseConst.UPLOAD_WORK_SUCCESS);//上传作品成功
        return ServerResponse.createSucessByCodeMsg(ResponseConst.UPLOAD_WORK_SUCCESS);
    }

    //获取新增videos
    private Videos addVideos(MultipartFile mfile,HttpServletRequest request,Videos videosData){
        String usersId = request.getHeader("usersId");
        Videos videos = new Videos();
        videos.setId(String.valueOf(System.currentTimeMillis()));
        videos.setUserId(usersId);
        videos.setVideoDesc(videosData.getVideoDesc());
        videos.setVideoPath("/"+mfile.getOriginalFilename());
        //获取视频长度、宽度、高度
        videos.setVideoSeconds(videosData.getVideoSeconds());
        videos.setVideoHeight(videosData.getVideoHeight());
        videos.setVideoWidth(videosData.getVideoWidth());
        videos.setLikeCounts(Long.valueOf("0"));
        videos.setStatus(1);
        videos.setCreateTime(new Date());
        return videos;
    }
}
