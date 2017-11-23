package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.FileNotFoundException;

/**
 * Created by ljh on 2017/10/30.
 */

public class CompressImageManager {

    public Bitmap CompressToAlbum(String path){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    /**
     * 压缩图片
     */
    public Bitmap CompressToCamera(Context context, Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();//压缩图片
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri),null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
