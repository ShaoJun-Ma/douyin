package com.msj.douyin.controller;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.UsersReport;
import com.msj.douyin.service.UsersReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "举报视频的接口",tags = {"举报视频的controller"})
public class UsersReportController {
    @Autowired
    private UsersReportService usersReportService;

    @ApiOperation(value = "用户提交举报视频",notes = "用户提交举报视频的接口")
    @PostMapping("/submitReport")
    public ServerResponse submitReport(@RequestBody UsersReport usersReport){
        return usersReportService.submitReport(usersReport);
    }
}
