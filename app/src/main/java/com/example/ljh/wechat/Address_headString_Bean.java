package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/10/3.
 */

public class Address_headString_Bean {

    private String head;
    private String username;

    Address_headString_Bean(String username,String head){
        this.username = username;
        this.head = head;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
