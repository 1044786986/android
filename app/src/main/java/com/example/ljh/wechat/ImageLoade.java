package com.example.ljh.wechat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Created by ljh on 2017/10/16.
 */

public class ImageLoade implements ImageLoader{
    @Override
    public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
            if(path != null){
                Bitmap bitmap = CompressImage(BitmapFactory.decodeFile(path));
                imageView.setImageBitmap(bitmap);
            }else{
                Toast.makeText(activity,"failed to gegt image",Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void clearMemoryCache() {
        clearMemoryCache();
    }

    public Bitmap CompressImage(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(0.1f,0.1f);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return bitmap;
    }
}
