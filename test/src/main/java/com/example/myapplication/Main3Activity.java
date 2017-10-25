package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ljh on 2017/10/16.
 */

public class Main3Activity extends AppCompatActivity implements View.OnClickListener{
    private Button button1;
    private EditText editText1;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        button1 = (Button) findViewById(R.id.bt1);
        editText1 = (EditText) findViewById(R.id.et1);
        textView = (TextView) findViewById(R.id.tv2);

        button1.setOnClickListener(this);

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            String result = data.getStringExtra("result");
            textView.setText(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt1:
        }
    }
}
