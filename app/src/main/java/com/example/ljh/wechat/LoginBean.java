package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/9/27.
 */

public class LoginBean {

    private String type;
    private String username;
    private String password;

    LoginBean(String type,String username,String password){
        this.type = type;
        this.username = username;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
