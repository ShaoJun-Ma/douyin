package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.service.BgmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "Bgm的接口",tags = {"Bgm的controller"})
public class BgmController {
    @Autowired
    private BgmService bgmService;

    //获取Bgm
    @ApiOperation(value = "获取Bmg",notes = "获取Bgm的接口")
    @PostMapping("/findBgm")
    public ServerResponse findBgm(){
        return bgmService.findBgm();
    }
}
