package com.example.ljh.wechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;

/**
 * Created by ljh on 2017/11/1.
 */

public class PreviewActivity extends AppCompatActivity{
    private ImageView ivPreview;
    private RelativeLayout layout_back;
    private Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().hide();
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        layout_back = (RelativeLayout) findViewById(R.id.layout_back);

        Intent intent = getIntent();
        bitmap = byteToBitmap(intent.getByteArrayExtra("bitmap"));
        ivPreview.setImageBitmap(bitmap);

        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap.recycle();
                finish();
            }
        });
    }

    public Bitmap byteToBitmap(byte byte1[]){
       return bitmap = BitmapFactory.decodeByteArray(byte1,0,byte1.length);
    }
}
