package com.msj.douyin.service.impl;

import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.BgmMapper;
import com.msj.douyin.pojo.Bgm;
import com.msj.douyin.service.BgmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j

public class BgmServiceImpl implements BgmService{
    @Autowired
    private BgmMapper bgmMapper;

    @Override
    public ServerResponse findBgm() {
        List<Bgm> bgmList = bgmMapper.selectAll();
        if(bgmList == null){
            return ServerResponse.createErrorCodeMsg(ResponseConst.SELECT_BGM_SUCCESS);
        }
        for(Bgm bgm:bgmList){
            String path = bgm.getPath();
            bgm.setPath(ResponseConst.BGM_PATH+path);
        }
        return ServerResponse.createSuccess(ResponseConst.SELECT_BGM_SUCCESS,bgmList);
    }
}
