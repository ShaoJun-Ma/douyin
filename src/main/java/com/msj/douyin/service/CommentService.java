package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;

public interface CommentService {
    ServerResponse leaveComment(String comment, String toUserId, String videoId,
                                HttpServletRequest request);
    ServerResponse selectComment(String videoId);
}
