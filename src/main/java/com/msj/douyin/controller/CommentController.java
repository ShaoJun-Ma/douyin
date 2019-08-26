package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.service.CommentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "评论的接口",tags = {"评论的controller"})
public class CommentController {
    @Autowired
    private CommentService commentService;

    //评论
    @PostMapping("/leaveComment")
    public ServerResponse leaveComment(String comment,String toUserId,String videoId,
                                       HttpServletRequest request){
        return commentService.leaveComment(comment,toUserId,videoId,request);
    }

    //查询评论
    @PostMapping("/selectCommentList")
    public ServerResponse selectComment(String videoId){
        return commentService.selectComment(videoId);
    }
}
