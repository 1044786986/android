package com.example.ljh.wechat;

/**
 * Created by ljh on 2017/10/17.
 */

public class SendImageMessageBean {

    private String imageString;

    SendImageMessageBean(String imageString){
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }


}
