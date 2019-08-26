package com.msj.douyin.utils;

import java.util.Date;

public class TimeDiffUtil {
    public static String timeDiff(Date d1,Date d2){
        try {
            long diff = d1.getTime() - d2.getTime();//毫秒
            long rs;
            if( (rs =diff/1000) < 60){//秒
                return rs+"秒前";
            }else if((rs = rs/60) < 60){//分钟
                return rs+"分钟前";
            }else if((rs = rs/60) < 60){//小时
                return rs+"小时前";
            }else if((rs = rs/24) < 24){
                return rs+"天前";
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
