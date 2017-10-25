package com.example.ljh.wechat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ljh on 2017/10/11.
 */

public class ChatLogCompartor implements Comparator<Chat_LogBean>{
    long date1 = 0;
    long date2 = 0;
    int result = 0;
    @Override
    public int compare(Chat_LogBean o1, Chat_LogBean o2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date1 = simpleDateFormat.parse(o1.getDate()).getTime();
            date2 = simpleDateFormat.parse(o2.getDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date1 > date2){
            result = 1;
        }else if(date1<date2) {
            result = -1;
        }
        return result;
    }
}
