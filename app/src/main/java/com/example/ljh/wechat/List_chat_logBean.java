package com.example.ljh.wechat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/10/8.
 */

public class List_chat_logBean {

    private String username;
    private List<Chat_LogBean> list = new ArrayList<Chat_LogBean>();
    private int unread;

    List_chat_logBean(String username,List<Chat_LogBean>list,int unread){
        this.list = list;
        this.username = username;
        this.unread = unread;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Chat_LogBean> getList() {
        return list;
    }

    public void setList(List<Chat_LogBean> list) {
        this.list = list;
    }
}
