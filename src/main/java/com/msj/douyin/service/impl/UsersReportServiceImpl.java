package com.msj.douyin.service.impl;
import com.msj.douyin.common.ResponseConst;
import com.msj.douyin.common.ServerResponse;
import com.msj.douyin.mapper.UsersReportMapper;
import com.msj.douyin.pojo.UsersReport;
import com.msj.douyin.service.UsersReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;


@Slf4j
@Service
public class UsersReportServiceImpl implements UsersReportService{
    @Autowired
    private UsersReportMapper usersReportMapper;
    @Override
    public ServerResponse submitReport(UsersReport usersReport) {
        usersReport.setId(String.valueOf(System.currentTimeMillis()));
        usersReport.setCreateDate(new Date());
        int resultCount = usersReportMapper.insert(usersReport);
        if(resultCount <= 0){
            log.info(ResponseConst.ADD_ERROR);
            return ServerResponse.createErrorCodeMsg(ResponseConst.ADD_ERROR);
        }
        log.info(ResponseConst.ADD_SUCCESS);
        return ServerResponse.createSucessByCodeMsg(ResponseConst.ADD_SUCCESS);
    }
}
