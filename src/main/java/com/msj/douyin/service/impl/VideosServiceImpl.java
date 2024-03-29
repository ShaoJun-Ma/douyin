package com.msj.douyin.service.impl;

import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.*;
import com.msj.douyin.pojo.*;
import com.msj.douyin.service.VideosService;
import com.msj.douyin.utils.FFMpegUtil;
import com.msj.douyin.vo.UsersAndVideos;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class VideosServiceImpl implements VideosService{
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private BgmMapper bgmMapper;

    //获取作品列表
    @Override
    public ServerResponse  doSelectWork(String usersId){
        Videos videos = new Videos();
        videos.setUserId(usersId);
        List<Videos> videoList = videosMapper.select(videos);
        if(videoList == null){
            log.info(ResponseConst.SELECT_WORK_ERROR);//获取作品成功
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_WORK_ERROR);
        }
        List<UsersAndVideos> usersAndVideosList = assembleUsersAndVideos(videoList);
        log.info(ResponseConst.SELECT_WORK_SUCCESS);//获取作品失败
        return ServerResponse.createSuccess(ResponseConst.SELECT_WORK_SUCCESS,usersAndVideosList);
    }

    //上传作品
    @Override
    public ServerResponse uploadVideo(MultipartFile mfile, HttpServletRequest request,
                                      Videos videosData,String bgmId) {
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

        //通过bgmId查询bgm路径
        String bgmPath = selectBgmPath(bgmId);

        //整合video和bgm
        String videoInput = "D:\\douyin\\videos\\"+mfile.getOriginalFilename();
        String mp3Input = "D:\\douyin\\bgms\\"+bgmPath;
        Double seconds = videosData.getVideoSeconds().doubleValue();
        String videoOuput = "D:\\douyin\\videos\\"+"new"+mfile.getOriginalFilename();
        assembleVideoAndBgm(videoInput,mp3Input,seconds,videoOuput);

        //ffmpeg获取封面图
        String videoPath = "new"+mfile.getOriginalFilename();
        String imagePath = System.currentTimeMillis()+".jpg";
        String imgPath = "D:\\douyin\\images\\"+imagePath;
        FFMpegUtil.processImg(videoOuput,imgPath);

        //获取新增videos信息
        Videos videos = addVideos(videoPath,request,videosData,bgmId,imagePath);
        int resultCount = videosMapper.insert(videos);
        if(resultCount <= 0){
            log.info(ResponseConst.UPLOAD_WORK_ERROR);//上传作品失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.UPLOAD_WORK_ERROR);
        }
        log.info(ResponseConst.UPLOAD_WORK_SUCCESS);//上传作品成功
        return ServerResponse.createSucessByCodeMsg(ResponseConst.UPLOAD_WORK_SUCCESS);
    }

    //获取bgm路径
    private String selectBgmPath(String bgmId){
        Bgm bgm = bgmMapper.selectByPrimaryKey(bgmId);
        String path = bgm.getPath();
        String bgmPath = StringUtils.substringAfter(path,"/");
        return bgmPath;
    }

    //整合video和bgm
    private void assembleVideoAndBgm(String videoInput,String mp3Input,Double time,String videoOuput){
        try {
            FFMpegUtil.convertor(videoInput,mp3Input,time,videoOuput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取新增videos
    private Videos addVideos(String videoPath,HttpServletRequest request,
                             Videos videosData,String bgmId,String imagePath){
        String usersId = request.getHeader("usersId");
        Videos videos = new Videos();
        videos.setId(String.valueOf(System.currentTimeMillis()));
        videos.setUserId(usersId);
        videos.setAudioId(bgmId);
        videos.setVideoDesc(videosData.getVideoDesc());
        videos.setVideoPath("/"+videoPath);
        //获取视频长度、宽度、高度
        videos.setVideoSeconds(videosData.getVideoSeconds());
        videos.setVideoHeight(videosData.getVideoHeight());
        videos.setVideoWidth(videosData.getVideoWidth());
        videos.setLikeCounts(Long.valueOf("0"));
        videos.setStatus(1);
        videos.setCreateTime(new Date());
        videos.setCoverPath("/"+imagePath);
        return videos;
    }

    @Override
    //获取videos
    public ServerResponse selectVideos() {
        List<Videos> videoList = videosMapper.selectAll();
        if(videoList == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_VIDEOS_ERROR);
        }
        //整合users 和videos
        List<UsersAndVideos> usersAndVideosList = assembleUsersAndVideos(videoList);
        return  ServerResponse.createSuccess(ResponseConst.SELECT_VIDEOS_SUCCESS,usersAndVideosList);
    }

    //整合users 和videos
    private List<UsersAndVideos> assembleUsersAndVideos(List<Videos> videoList){
        List<UsersAndVideos> usersAndVideosList = new ArrayList<>();
        if(videoList != null){
            for(Videos video:videoList){
                String userId = video.getUserId();
                Users users = new Users();
                users.setId(userId);
                Users userOne = usersMapper.selectOne(users);
                if(userOne == null){
                    continue;
                }
                UsersAndVideos usersAndVideos = new UsersAndVideos();
                usersAndVideos.setVideos(video);
                usersAndVideos.setUsers(userOne);
                usersAndVideosList.add(usersAndVideos);
            }
            if(usersAndVideosList!=null){
                return usersAndVideosList;
            }
        }
        return null;
    }

    //判断是否收藏过
    @Override
    public ServerResponse isCollect(String videoId,HttpServletRequest request) {
        //查询usersLikeVideos
        UsersLikeVideos usersLikeVideos = selectUsersLikeVideos(videoId, request);
        if(usersLikeVideos == null){
            log.info(ResponseConst.NOT_COLLECT); //还没收藏
            return ServerResponse.createErrorCodeMsg(ResponseConst.NOT_COLLECT);
        }

        log.info(ResponseConst.IS_COLLECT); //已经收藏
        return ServerResponse.createSuccess(ResponseConst.IS_COLLECT,usersLikeVideos);
    }

    //查询usersLikeVideos
    private UsersLikeVideos selectUsersLikeVideos(String videoId,HttpServletRequest request){
        String usersId = request.getHeader("usersId");
        UsersLikeVideos ulVideos = new UsersLikeVideos();
        ulVideos.setUserId(usersId);
        ulVideos.setVideoId(videoId);
        UsersLikeVideos usersLikeVideos = usersLikeVideosMapper.selectOne(ulVideos);
        return usersLikeVideos;
    }

    //收藏video
    @Override
    public ServerResponse likeVideo(String videoId,String publishUserId,
                                    HttpServletRequest request) {
        String usersId = request.getHeader("usersId");
        //增加usersLikeVideos
        int resultCount = addUsersLikeVideos(videoId,usersId);
        if(resultCount <= 0){
            log.info(ResponseConst.LIKE_VIDEO_ERROR);//收藏失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.LIKE_VIDEO_ERROR);
        }
        //更新users表中的收藏数
        boolean flag = true;
        int updateCount = updataUsers(flag,publishUserId);
        if(updateCount<=0){
            log.info(ResponseConst.ADD_ReceiveLikeCounts_ERROR);//增加收藏数失败
        }
        log.info(ResponseConst.ADD_ReceiveLikeCounts_SUCCESS);//增加收藏数成功
        log.info(ResponseConst.LIKE_VIDEO_SUCCESS);//收藏成功
        return ServerResponse.createSucessByCodeMsg(ResponseConst.LIKE_VIDEO_SUCCESS);

    }

    //取消收藏video
    public ServerResponse noLikeVideo(String videoId,String publishUserId,
                                      HttpServletRequest request){
        String usersId = request.getHeader("usersId");
        //删除usersLikeVideos
        int resultCount = deleteUsersLikeVideos(videoId, usersId);
        if(resultCount <= 0){
            log.info(ResponseConst.NO_LIKE_VIDEO_ERROR);//取消收藏失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.NO_LIKE_VIDEO_ERROR);
        }
        //更新users表中的收藏数
        boolean flag = false;
        int updateCount = updataUsers(flag,publishUserId);
        if(updateCount<=0){
            log.info(ResponseConst.DEL_ReceiveLikeCounts_ERROR);//减少收藏数失败
        }
        log.info(ResponseConst.DEL_ReceiveLikeCounts_SUCCESS);//减少收藏数成功
        log.info(ResponseConst.NO_LIKE_VIDEO_SUCCESS);//取消收藏
        return ServerResponse.createSucessByCodeMsg(ResponseConst.NO_LIKE_VIDEO_SUCCESS);
    }

    //增加UsersLikeVideos
    private int addUsersLikeVideos(String videoId,String usersId){
        UsersLikeVideos ulVideos = new UsersLikeVideos();
        ulVideos.setId(String.valueOf(System.currentTimeMillis()));
        ulVideos.setVideoId(videoId);
        ulVideos.setUserId(usersId);
        //新增数据到usersLikeVideos表
        int resultCount = usersLikeVideosMapper.insert(ulVideos);
        return resultCount;
    }

    //删除UsersLikeVideos
    private int deleteUsersLikeVideos(String videoId,String usersId){
        String id = selectUsersLikeVideos(videoId, usersId);
        int resultCount = usersLikeVideosMapper.deleteByPrimaryKey(id);
        return resultCount;
    }

    //查询usersLikeVideos
    private String selectUsersLikeVideos(String videoId,String usersId){
        UsersLikeVideos ulVideos = new UsersLikeVideos();
        ulVideos.setVideoId(videoId);
        ulVideos.setUserId(usersId);
        UsersLikeVideos usersLikeVideos = usersLikeVideosMapper.selectOne(ulVideos);
        return usersLikeVideos.getId();
    }

    //更新users表中的收藏数
    private int updataUsers(boolean flag,String publishUserId){
        //根据id查询Users
        Users users = selectUsers(publishUserId);
        int receiveLikeCounts = users.getReceiveLikeCounts();
        if(!flag){
            //false：减少收藏数
            if(receiveLikeCounts <= 0){//保证收藏数不能小于0
                receiveLikeCounts = 0;
            }
            receiveLikeCounts--;
        }else{
            //true：增加收藏数
            receiveLikeCounts++;
        }
        Users usersOne = new Users();
        usersOne.setId(publishUserId);
        usersOne.setReceiveLikeCounts(receiveLikeCounts);
        int updateCount = usersMapper.updateByPrimaryKeySelective(usersOne);
        return updateCount;
    }

    //根据id查询Users
    private Users selectUsers(String publishUserId){
        Users users = usersMapper.selectByPrimaryKey(publishUserId);
        return users;
    }

    //获取收藏列表
    @Override
    public ServerResponse doSelectLike(String usersId) {
        List<UsersAndVideos> usersAndVideosList = new ArrayList<>();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setUserId(usersId);
        List<UsersLikeVideos> usersLikeVideosList = usersLikeVideosMapper.select(usersLikeVideos);
        if(usersLikeVideosList != null){
            for(UsersLikeVideos u:usersLikeVideosList){
                UsersAndVideos usersAndVideos = new UsersAndVideos();
                String vId = u.getVideoId();
                Videos videos = videosMapper.selectByPrimaryKey(vId);
                String uId = videos.getUserId();
                Users users = usersMapper.selectByPrimaryKey(uId);
                usersAndVideos.setVideos(videos);
                usersAndVideos.setUsers(users);
                usersAndVideosList.add(usersAndVideos);
            }
        }
        if(usersAndVideosList == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_ERROR);
        }
        return ServerResponse.createSuccess(ResponseConst.SELECT_SUCCESS,usersAndVideosList);
    }

    //获取关注列表
    @Override
    public ServerResponse doSelectFollow(String usersId) {//fansId
        List<UsersAndVideos> usersAndVideosList = new ArrayList<>();
        UsersFans usersFans = new UsersFans();
        usersFans.setFanId(usersId);
        List<UsersFans> usersFansList = usersFansMapper.select(usersFans);
        if(usersFansList!=null){
            for(UsersFans u:usersFansList){
                String uId = u.getUserId(); //被关注的usersId
                Users users = usersMapper.selectByPrimaryKey(uId);
                Videos videosOne = new Videos();
                videosOne.setUserId(uId);
                List<Videos> videosList = videosMapper.select(videosOne);
                for(Videos v:videosList) {
                    UsersAndVideos usersAndVideos = new UsersAndVideos();
                    usersAndVideos.setUsers(users);
                    usersAndVideos.setVideos(v);
                    usersAndVideosList.add(usersAndVideos);
                }
            }
        }
        if(usersAndVideosList == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_ERROR);
        }
        return ServerResponse.createSuccess(ResponseConst.SELECT_SUCCESS,usersAndVideosList);
    }

    //搜索视频
    @Override
    public ServerResponse searchVideo(String content) {
        if(content == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SEARCH_ERROR);
        }
        SearchRecords searchRecords = new SearchRecords();
        searchRecords.setContent(content);
        SearchRecords searchRecordsOne = searchRecordsMapper.selectOne(searchRecords);
        if(searchRecordsOne == null){ //search_records没有该数据才插入，有的话就不插入
            //插入数据到search_records
            int resultCount = addSearchRecords(content);
            if(resultCount <= 0){
                return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_ERROR);//添加失败
            }
        }
        //根据视频的描述查询videos
        List<UsersAndVideos> usersAndVideosList = selectVideosByDesc(content);
        if(usersAndVideosList == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SEARCH_NONE);//该资源不存在
        }
        return ServerResponse.createSuccess(ResponseConst.SEARCH_SUCCESS,usersAndVideosList);//查询成功
    }

    //根据视频描述查询videos
    private List<UsersAndVideos> selectVideosByDesc(String content){
        List<Videos> videosList = videosMapper.selectByDesc(content);
        List<UsersAndVideos> usersAndVideosList= new ArrayList<>();
        for(Videos v:videosList){
            UsersAndVideos usersAndVideos = new UsersAndVideos();
            usersAndVideos.setVideos(v);
            String userId = v.getUserId();
            Users users = usersMapper.selectByPrimaryKey(userId);
            usersAndVideos.setUsers(users);
            usersAndVideosList.add(usersAndVideos);
        }
        return usersAndVideosList;
    }

    //插入数据到search_records
    private int addSearchRecords(String content){
        SearchRecords searchRecords = new SearchRecords();
        searchRecords.setContent(content);
        searchRecords.setId(String.valueOf(System.currentTimeMillis()));
        int resultCount = searchRecordsMapper.insert(searchRecords);
        return resultCount;
    }
}
