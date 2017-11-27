package com.example.ljh.wechat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.attr.button;
import static android.R.attr.layout_centerHorizontal;

/**
 * Created by ljh on 2017/11/24.
 */

public class AActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_fragmentchat2);
        LinearLayout linearLayout_chat = (LinearLayout) findViewById(R.id.linearLayout_chat);
        //View view = (View) findViewById(R.id.view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int layout_width = linearLayout_chat.getWidth();
        int layout_height = linearLayout_chat.getHeight();
        //textView.setWidth(width - layout_width - 300);
        /*ViewGroup.LayoutParams layoutParams = linearLayout_chat.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = layout_height;
        linearLayout_chat.setLayoutParams(layoutParams);*/

    }
}
