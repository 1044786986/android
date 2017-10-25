package com.example.ljh.wechat;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.util.Date;

/**
 * Created by ljh on 2017/9/25.
 */

public class AddFriendBean {

    private String username;
    private String data;
    private Bitmap head;
    private String date;
    private String state;

    AddFriendBean(){}

    AddFriendBean(String username, String data, Bitmap head, String state ,String date){
        this.username = username;
        this.data = data;
        this.head = head;
        this.date = date;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Bitmap getHead() {
        return head;
    }

    public void setHead(Bitmap head) {
        this.head = head;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
