package com.msj.douyin.service.impl;

import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.CommentsMapper;
import com.msj.douyin.mapper.UsersMapper;
import com.msj.douyin.pojo.Comments;
import com.msj.douyin.pojo.Users;
import com.msj.douyin.service.CommentService;
import com.msj.douyin.utils.TimeDiffUtil;
import com.msj.douyin.vo.UsersAndComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.events.Comment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private UsersMapper usersMapper;

    //评论
    @Override
    public ServerResponse leaveComment(String comment, String toUserId, String videoId,
                                       HttpServletRequest request) {
        String fromUserId = request.getHeader("usersId");
        int resultCount = addComment(comment, toUserId, videoId,fromUserId);
        if(resultCount <= 0){
            return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_ERROR);
        }
        return ServerResponse.createSucessByCodeMsg(ResponseConst.ADD_SUCCESS);
    }
    
    private int addComment(String comment, String toUserId, String videoId,String fromUserId){
        Comments comments = new Comments();
        comments.setId(String.valueOf(System.currentTimeMillis()));
        comments.setVideoId(videoId);
        comments.setComment(comment);
        comments.setFromUserId(fromUserId);
        comments.setCreateTime(new Date());
        int resultCount = commentsMapper.insert(comments);
        return resultCount;
    }
    
    //查询评论
    @Override
    public ServerResponse selectComment(String videoId) {
        Comments comments = new Comments();
        comments.setVideoId(videoId);
        List<UsersAndComment> usersAndCommentList = new ArrayList<>();
        List<Comments> commentList = commentsMapper.select(comments);
        for(Comments c:commentList){
            String fromUserId = c.getFromUserId();
            Users users = selectUsers(fromUserId);
            UsersAndComment uc = new UsersAndComment();
            uc.setId(c.getId());
            uc.setComment(c.getComment());
            uc.setFromUserId(fromUserId);
            uc.setFaceImage(users.getFaceImage());
            uc.setNickname(users.getNickname());

            //时间差
            Date d1 = new Date();
            Date d2 = c.getCreateTime();
            String timeAgoStr = TimeDiffUtil.timeDiff(d1, d2);
            uc.setTimeAgoStr(timeAgoStr);
            usersAndCommentList.add(uc);
        }
        if(usersAndCommentList == null){
            return ServerResponse.createSucessByCodeMsg(ResponseConst.SELECT_ERROR);
        }
        return ServerResponse.createSuccess(ResponseConst.SELECT_SUCCESS,usersAndCommentList);
    }
    private Users selectUsers(String id){
        Users users = usersMapper.selectByPrimaryKey(id);
        if(users != null){
            return users;
        }
        return null;
    }
}
