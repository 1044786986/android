package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * Created by ljh on 2017/10/16.
 */

public class Main2Activity extends AppCompatActivity{
    private TextView textView1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView1 = (TextView) findViewById(R.id.tv1);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        textView1.setText(message);

        intent.putExtra("result","这是返回的数据");
        setResult(1,intent);
    }
}
