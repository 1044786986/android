package com.example.ljh.wechat;

import android.graphics.Bitmap;

/**
 * Created by ljh on 2017/9/20.
 */

public class AddressBean {

    private Bitmap bitmap;
    private String name;

    AddressBean(String name,Bitmap bitmap){
        this.name = name;
        this.bitmap = bitmap;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
