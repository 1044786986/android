package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/10/6.
 */

public class Chat_LogBean {
    private int id;
    private String fromUser;
    private String toUser;
    private String text;
    private byte image[];
    private String date;
    private String voicePath;

    Chat_LogBean(int id,String fromUser, String toUser, String text, byte image[], String date,String voicePath){
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.text = text;
        this.image = image;
        this.date = date;
        this.voicePath = voicePath;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
