package com.example.ljh.wechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ljh on 2017/11/16.
 */

public class MessageRemindActivity extends AppCompatActivity implements View.OnClickListener{
    private SwitchCompat switch_msg,switch_voice,switch_shock;
    private LinearLayout layout_voice_shock;

    static boolean msg;
    static boolean voice;
    static boolean shock;

    static SharedPreferences sharedPreferences = LoginActivity.sharedPreferences;
    static SharedPreferences.Editor editor = LoginActivity.editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgremind);

        layout_voice_shock = (LinearLayout) findViewById(R.id.layout_voice_shock);
        switch_msg = (SwitchCompat) findViewById(R.id.switch_msg);
        switch_voice = (SwitchCompat) findViewById(R.id.switch_voice);
        switch_shock = (SwitchCompat) findViewById(R.id.switch_shock);
        switch_msg.setOnClickListener(this);
        switch_voice.setOnClickListener(this);
        switch_shock.setOnClickListener(this);
        layout_voice_shock.setOnClickListener(this);

        /*sharedPreferences = getSharedPreferences("messageRemind",MODE_PRIVATE);
        editor = sharedPreferences.edit();*/
        addSharePreferences();

        switch_msg.setChecked(msg);
        switch_voice.setChecked(voice);
        switch_shock.setChecked(shock);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_msg:
                if(!switch_msg.isChecked()){    //取消消息提醒，同时使声音和震动不能选
                    switch_voice.setChecked(false);
                    switch_shock.setChecked(false);
                    layout_voice_shock.setVisibility(View.GONE);
                    editor.putBoolean("msg",false);
                    editor.putBoolean("voice",false);
                    editor.putBoolean("shock",false);
                    msg = false;
                    voice = false;
                    shock = false;
                }else{
                    layout_voice_shock.setVisibility(View.VISIBLE);
                    editor.putBoolean("msg",true);
                    msg = true;
                    voice = true;
                    shock = true;
                }
                break;
            case R.id.switch_voice:
                if(switch_voice.isChecked()){
                    voice  = true;
                    editor.putBoolean("voice",true);
                }else {
                    voice = false;
                    editor.putBoolean("voice",false);
                }
                break;
            case R.id.switch_shock:
                if(switch_shock.isChecked()){
                    shock = true;
                    editor.putBoolean("shock",true);
                }else {
                    shock = false;
                    editor.putBoolean("shock",false);
                }
                break;
        }
        editor.commit();
    }

    public void getSetting(){
        msg = sharedPreferences.getBoolean("msg",msg);
        voice = sharedPreferences.getBoolean("voice",voice);
        shock = sharedPreferences.getBoolean("shock",shock);

    }

    /**
     * 没有手动设置过,默认设置提醒
     */
    public void addSharePreferences(){
        String msgRemindString = sharedPreferences.getString("msgRemind","");
        if(msgRemindString == null || msgRemindString == ""){
            editor.putString("msgRemind","true");
            editor.putBoolean("msg",true);
            editor.putBoolean("voice",true);
            editor.putBoolean("shock",true);
            editor.commit();
        }else{
            getSetting();
        }
    }
}
