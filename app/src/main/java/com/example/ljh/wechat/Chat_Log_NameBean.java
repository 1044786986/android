package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/10/8.
 */

public class Chat_Log_NameBean {

    Chat_Log_NameBean(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
}
