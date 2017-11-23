package com.example.ljh.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ljh on 2017/11/16.
 */

public class SetUpActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout layout_messageRemind,layout_languageSetting,layout_chatSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        layout_messageRemind = (LinearLayout) findViewById(R.id.layout_messageRemind);
        layout_languageSetting = (LinearLayout) findViewById(R.id.layout_languageSetting);
        layout_chatSetting = (LinearLayout) findViewById(R.id.layout_chatSetting);
        layout_messageRemind.setOnClickListener(this);
        layout_languageSetting.setOnClickListener(this);
        layout_chatSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.layout_messageRemind:
                intent = new Intent(this,MessageRemindActivity.class);
                break;
            case R.id.layout_languageSetting:
                intent = new Intent(this,LanguageSettingActivity.class);
                break;
            case R.id.layout_chatSetting:
                intent = new Intent(this,ChatSettingActivity.class);
                break;
        }
        startActivity(intent);
    }
}
