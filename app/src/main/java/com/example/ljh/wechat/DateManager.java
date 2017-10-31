package com.example.ljh.wechat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljh on 2017/10/30.
 */

public class DateManager {

    static String getDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
