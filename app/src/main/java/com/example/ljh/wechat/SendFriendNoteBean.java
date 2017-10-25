package com.example.ljh.wechat;

import android.graphics.Bitmap;

/**
 * Created by ljh on 2017/9/27.
 */

public class SendFriendNoteBean {

    SendFriendNoteBean(String type,String fromUser,String toUser,String data,byte head[]){
        this.type = type;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.data = data;
        this.head = head;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    private String type;
    private String fromUser;
    private String toUser;
    private String data;
    private byte[] head;


}
