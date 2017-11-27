package com.example.ljh.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ljh on 2017/11/23.
 */

public class MyInformationActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout layout_modifyPassword,layout_myPost;   //修改密码，我的帖子
    private ImageView ivHead;
    private TextView tvUsername;

    private PreViewManager preViewManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_myinformation);

        preViewManager = new PreViewManager();
        initView();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.layout_modifyPassword:
                intent = new Intent(this,ModifyPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_myPost:
                intent = new Intent(this,MyPostActivity.class);
                startActivity(intent);
                break;
            case R.id.ivHead:
                intent = new Intent(this,PreviewActivity.class);
                intent.putExtra("bitmap",preViewManager.BitmapToByte(FragmentMy.getMyHead()));
                startActivity(intent);
                break;
        }
    }

    public void initView(){
        layout_modifyPassword = (LinearLayout) findViewById(R.id.layout_modifyPassword);
        layout_myPost = (LinearLayout) findViewById(R.id.layout_myPost);
        layout_modifyPassword.setOnClickListener(this);
        layout_myPost.setOnClickListener(this);

        tvUsername = (TextView) findViewById(R.id.tvUserName);
        tvUsername.setText(MainActivity.username);
        ivHead = (ImageView) findViewById(R.id.ivHead);
        ivHead.setImageBitmap(FragmentMy.getMyHead());
        ivHead.setOnClickListener(this);
    }


}
