package com.msj.douyin.service.impl;

import com.google.gson.Gson;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersFansMapper;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.mapper.VideosMapper;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.pojo.UsersFans;
import com.msj.douyin.pojo.Videos;
import com.msj.douyin.service.UsersService;
import com.msj.douyin.utils.MD5Util;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import redis.clients.jedis.Jedis;

import it.sauronsoftware.jave.Encoder;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.util.*;


@Slf4j
@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;

    //获取连接池jedis对象
    @Autowired
    private Jedis jedis;

    //注册
    @Override
    public ServerResponse register(Users users) {
        String id = String.valueOf(System.currentTimeMillis());
        users.setId(id);
        users.setNickname(users.getUsername());
        //加密
        String password = users.getPassword();
        users.setPassword(MD5Util.getMD5(password+"salt"));

        int resultCount = usersMapper.insert(users);
        if(resultCount < 0){
            log.info(ResponseConst.REGISTER_ERROR_MSG);//日志：注册失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.REGISTER_ERROR_MSG);
        }
        log.info(ResponseConst.REGISTER_SUCCESS_MSG);//注册成功
        //回滚（用来测试）：数据不会更新到数据库中
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return ServerResponse.createSucessByCodeMsg(ResponseConst.REGISTER_SUCCESS_MSG);
    }

    //登录
    @Override
    public ServerResponse login(Users users) {
        //加密
        String password = users.getPassword();
        users.setPassword(MD5Util.getMD5(password+"salt"));
        //1、判断是否有该用户
        Users usersOne = usersMapper.selectOne(users);
        if(usersOne == null){
            log.info(ResponseConst.LOGIN_ERROR_MSG);//登录失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGIN_ERROR_MSG);
        }

        //2、将users数据放在redis缓存中   key:usersId  value:usersData
        String usersData = new Gson().toJson(usersOne);//将对象转化为json格式（json是字符串）
        String usersId = usersOne.getId();
        jedis.set(usersId,usersData);
        //归还jedis对象到连接池
        jedis.close();
        log.info(ResponseConst.LOGIN_SUCCESS_MSG);//登录成功
        return ServerResponse.createSuccess(ResponseConst.LOGIN_SUCCESS_MSG,usersId);//将usersId返回给前端
    }

    //个人信息
    @Override
    public ServerResponse mine(String usersId) {
        String users = jedis.get(usersId);
        jedis.close();
        if(users == null){
            Users usersOne = usersMapper.selectByPrimaryKey(usersId);
            if(usersOne == null){
                log.info(ResponseConst.SELECT_USERS_ERROR);//无该用户信息
                return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_USERS_ERROR);
            }else{
                return ServerResponse.createSuccess(ResponseConst.SELECT_USERS_SUCCESS,usersOne);
            }
        }
        Users usersData = new Gson().fromJson(users, Users.class);
        log.info(usersData.toString());//个人信息
        log.info(ResponseConst.SELECT_USERS_SUCCESS);//获取个人信息成功
        return ServerResponse.createSuccess(ResponseConst.SELECT_USERS_SUCCESS,usersData);
    }

    //上传头像
    @Override
    public ServerResponse changeFace(MultipartFile mfile,HttpServletRequest request){
        //新建文件夹及文件
        File file = new File("D:/douyin/images/"+mfile.getOriginalFilename());
        File parentFile = file.getParentFile();
        parentFile.mkdirs();
        try {
            //MultipartFile转file
            mfile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String usersId = request.getHeader("usersId");
        String users = jedis.get(usersId);
        Users usersOne = new Gson().fromJson(users, Users.class);
        String imagePath = "/"+mfile.getOriginalFilename();
        usersOne.setFaceImage(imagePath);
        synchronized (usersOne){
            //进行两个操作，加锁机制
            usersMapper.updateByPrimaryKeySelective(usersOne);
            String usersData = new Gson().toJson( usersMapper.selectOne(usersOne));
            jedis.set(usersId,usersData);
        }
        log.info(ResponseConst.UPDATE_IMAGE_SUCCESS);//更新头像成功
        return ServerResponse.createSuccess(ResponseConst.UPDATE_IMAGE_SUCCESS,imagePath);
    }

    //退出登录
    @Override
    public ServerResponse logout(HttpServletRequest request){
        String usersId = request.getHeader("usersId");
        Long resultCount = jedis.del(usersId);
        if(resultCount <= 0){
            return ServerResponse.createErrorCodeMsg(ResponseConst.LOGOUT_ERROR);
        }
        return ServerResponse.createErrorCodeMsg(ResponseConst.LOGOUT_SUCCESS);
    }

    //查看是否关注过
    @Override
    public ServerResponse isFollowMe(String usersId, HttpServletRequest request) {
        String fanId = request.getHeader("usersId");
        UsersFans usersFans = selectUsersFans(usersId, fanId);
        if(usersFans == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_USERS_FANS_ERROR);
        }
        return ServerResponse.createSucessByCodeMsg(ResponseConst.SELECT_USERS_FANS_SUCCESS);
    }

    //关注
    @Override
    public ServerResponse followMe(String usersId,HttpServletRequest request) {
        boolean flag = true;
        String fanId = request.getHeader("usersId");
        //关注他人：增加users表关注者
        int addCount = addUserFollow(fanId,flag);
        if(addCount <= 0){
            log.info(ResponseConst.ADD_USERS_FOLLOW_ERROR);//增加关注失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_USERS_FOLLOW_ERROR);
        }else{
            log.info(ResponseConst.ADD_USERS_FOLLOW_SUCCESS);//增加关注成功
        }

        //增加粉丝
        int resultCount = addUsesFans(usersId, fanId);
        if(resultCount <= 0){
            log.info(ResponseConst.ADD_USERS_FANS_ERROR);//增加粉丝失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_USERS_FANS_ERROR);
        }else{
            log.info(ResponseConst.ADD_USERS_FANS_SUCCESS);//增加粉丝成功
        }

        //被关注者：增加users表粉丝数
        int updateCount = updateFansCount(usersId, flag);
        if(updateCount <= 0){
            log.info(ResponseConst.FOLLOW_ME_ERROR);//关注失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.FOLLOW_ME_ERROR);
        }
        log.info(ResponseConst.FOLLOW_ME_SUCCESS);//关注成功
        return ServerResponse.createSucessByCodeMsg(ResponseConst.FOLLOW_ME_SUCCESS);
    }

    //取消关注
    @Override
    public ServerResponse noFollowMe(String usersId, HttpServletRequest request) {
        String fanId = request.getHeader("usersId");
        boolean flag = false;

        //关注他人：减少users表关注者
        int addCount = addUserFollow(fanId,flag);
        if(addCount <= 0){
            log.info(ResponseConst.ADD_USERS_FOLLOW_ERROR);//增加关注失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_USERS_FOLLOW_ERROR);
        }
        log.info(ResponseConst.ADD_USERS_FOLLOW_SUCCESS);//增加关注成功

        //删除粉丝
        int resultCount = deleteUsersFans(usersId,fanId);
        if(resultCount <= 0){
            log.info(ResponseConst.DEL_USERS_FANS_ERROR); //删除粉丝失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.DEL_USERS_FANS_ERROR);
        }
        log.info(ResponseConst.DEL_USERS_FANS_SUCCESS);//删除粉丝成功

        //被关注者：减少users表粉丝数
        int updateCount = updateFansCount(usersId, flag);
        if(updateCount <= 0){
            log.info(ResponseConst.CANCEL_FOLLOW_ME_ERROR);//取消关注失败
            return ServerResponse.createErrorCodeMsg(ResponseConst.CANCEL_FOLLOW_ME_ERROR);
        }
        log.info(ResponseConst.CANCEL_FOLLOW_ME_SUCCESS);//取消关注成功
        return ServerResponse.createSucessByCodeMsg(ResponseConst.CANCEL_FOLLOW_ME_SUCCESS);
    }

    //更新关注者（增加/减少）
    private int addUserFollow(String usersId,boolean flag){
        Users users = usersMapper.selectByPrimaryKey(usersId);
        if(users == null){
            log.info(ResponseConst.SELECT_USERS_ERROR);
        }
        if(!flag){
            if(users.getFansCounts()<=0){
                users.setFollowCounts(0);
            }else{
                users.setFollowCounts(users.getFansCounts()-1);
            }
        }else{
            users.setFollowCounts(users.getFollowCounts()+1);
        }
        int resultCount = usersMapper.updateByPrimaryKeySelective(users);
        return resultCount;
    }

    //删除粉丝
    private int deleteUsersFans(String usersId,String fanId){
        UsersFans usersFans = selectUsersFans(usersId, fanId);
        if(usersFans == null){
            log.info(ResponseConst.SELECT_USERS_FANS_ERROR);//该数据不存在
        }
        int resultCount = usersFansMapper.deleteByPrimaryKey(usersFans.getId());
        return resultCount;
    }

    //增加粉丝
    private int addUsesFans(String usersId,String fanId){
        UsersFans usersFans = new UsersFans();
        usersFans.setId(String.valueOf(System.currentTimeMillis()));
        usersFans.setUserId(usersId);
        usersFans.setFanId(fanId);
        int resultCount = usersFansMapper.insert(usersFans);
        return resultCount;
    }

    //查询粉丝
    private UsersFans selectUsersFans(String usersId,String fanId){
        if(usersId == null || fanId == null){
            return null;
        }
        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(usersId);
        usersFans.setFanId(fanId);
        UsersFans usersFansOne = usersFansMapper.selectOne(usersFans);
        return usersFansOne;
    }

    //更新users表中的粉丝数（增加/减少）
    private int updateFansCount(String usersId,boolean flag){
        //通过id查users
        Users usersOne = usersMapper.selectByPrimaryKey(usersId);
        if(!flag){//粉丝数减1
            if(usersOne.getFansCounts() <= 0){
                usersOne.setFansCounts(0);
            }else{
                usersOne.setFansCounts(usersOne.getFansCounts()-1);
            }
        }else{
            //粉丝数加1
            usersOne.setFansCounts(usersOne.getFansCounts()+1);
        }
        int updateCount = usersMapper.updateByPrimaryKeySelective(usersOne);
        return updateCount;
    }


}
