package com.example.ljh.wechat;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by ljh on 2017/10/29.
 */

public class ShareBean {

    private String username;
    private String headString;
    private String title;
    private String content;
    private String date;
    private String comment;
    private String nice;
    private String id;
    private String type;

    private List<String>list;

    ShareBean(String id,String username,String type,String title,String content,String date,String comment,String nice,List<String>list){
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.date = date;
        this.comment = comment;
        this.nice = nice;
        this.list = list;
        this.type = type;
    }

    public String getHeadString(){
        return headString;
    }

    public void setHeadString(String headString){
        this.headString = headString;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNice() {
        return nice;
    }

    public void setNice(String nice) {
        this.nice = nice;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
