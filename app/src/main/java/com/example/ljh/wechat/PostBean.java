package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/11/19.
 */

public class PostBean {


    private String comment_id;
    private String username;
    private String headString;
    private String content;
    private String imageString;
    private String date;

    PostBean(String comment_id,String username,String headString,String content,String imageString,String date){
        this.comment_id = comment_id;
        this.username = username;
        this.headString = headString;
        this.content = content;
        this.imageString = imageString;
        this.date = date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadString() {
        return headString;
    }

    public void setHeadString(String headString) {
        this.headString = headString;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
