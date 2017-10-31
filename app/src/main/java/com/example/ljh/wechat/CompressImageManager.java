package com.example.ljh.wechat;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by ljh on 2017/10/30.
 */

public class CompressImageManager {

    static Bitmap CompressImage(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f,0.5f);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return bitmap;
    }
}
