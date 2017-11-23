package com.example.ljh.wechat;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ljh on 2017/11/11.
 */

public class CommunityActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView tvContent,tvUsername,tvDate,tvThumbUp,tvThumbDown;
    private ImageView ivHead,ivThumbUp,ivThumbDown;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
    }

    public void initView(){
        View headView = LayoutInflater.from(this).inflate(R.layout.headview_community,null);
        tvContent = (TextView) headView.findViewById(R.id.tvContent);
        tvUsername = (TextView) headView.findViewById(R.id.tvUserName);
        tvDate = (TextView) headView.findViewById(R.id.tvDate);
        tvThumbUp = (TextView) headView.findViewById(R.id.tvThumbUp);
        tvThumbDown = (TextView) headView.findViewById(R.id.tvThumbDown);
        ivHead = (ImageView) headView.findViewById(R.id.ivHead);
        ivThumbUp = (ImageView) headView.findViewById(R.id.ivThumbUp);
        ivThumbDown = (ImageView) headView.findViewById(R.id.ivThumbDown);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_community);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addView(headView,0);

    }
}
