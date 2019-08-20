package com.msj.douyin.service;

import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.pojo.UsersReport;

public interface UsersReportService {
    ServerResponse submitReport(UsersReport usersReport);
}
