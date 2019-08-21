package com.msj.douyin.mapper;

import com.msj.douyin.pojo.Videos;
import com.msj.douyin.utils.MyMapper;

import java.util.List;

public interface VideosMapper extends MyMapper<Videos> {
    List<Videos> selectByDesc(String content);
}