package com.example.ljh.wechat;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ljh on 2017/11/16.
 */

public class ChatSettingActivity extends AppCompatActivity{
    private LinearLayout layout_clearChatLog;
    private TextView tvBack;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatsetting);
        getSupportActionBar().hide();

        layout_clearChatLog = (LinearLayout) findViewById(R.id.layout_clearChatLog);
        layout_clearChatLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        tvBack = (TextView) findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void clearChatLog(){
        sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        sqLiteDatabase.delete("chat_log","",new String[]{});
        sqLiteDatabase.close();

        FragmentChat.datalist.clear();
        FragmentChat.updateAdapter();
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("确定要清除聊天记录吗")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearChatLog();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
    }
}
