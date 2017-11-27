package com.example.ljh.wechat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by ljh on 2017/11/26.
 */

public class UserListCompartor implements Comparator<List_chat_logBean>{
    long date1 = 0;
    long date2 = 0;
    int result = 0;
    SimpleDateFormat simpleDateFormat;
    @Override
    public int compare(List_chat_logBean o1, List_chat_logBean o2) {
        try {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date1 = simpleDateFormat.parse(o1.getList().get(o1.getList().size()-1).getDate()).getTime();
            date2 = simpleDateFormat.parse(o2.getList().get(o2.getList().size()-1).getDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date1 > date2){
            result = -1;
        }else if(date2 > date1){
            result = 1;
        }
        return result;
    }
}
