package com.example.ljh.wechat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljh on 2017/9/22.
 */

public class AddFriendNoteActivity extends AppCompatActivity implements View.OnClickListener{
    static Socket socket;

    private String data;
    private String toUser;
    private byte image[];

    private EditText etNote;
    private TextView tvCancel;
    private TextView tvSend;

    LoginActivity loginActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriendnote);

        loginActivity = new LoginActivity();
        Intent intent = getIntent();
        toUser = intent.getStringExtra("username");
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvCancel:
                Intent intent = new Intent(this,AddFriendActivity.class);
                startActivity(intent);
                AddFriendNoteActivity.this.finish();
                break;
            case R.id.tvSend:
                Send();
                AddFriendNoteActivity.this.finish();
                break;
        }
    }

    /**
     * 发送当前用户数据给服务器
     */
    public void Send(){

        new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket(MainActivity.Ip,8888);
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    data = etNote.getText()+"";
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("fromUser",MainActivity.username);
                    map.put("data",data);
                    map.put("toUser",toUser);
                    map.put("type","AddFriend");
                    map.put("head",Base64.encodeToString(getByte(),Base64.DEFAULT));//将Byte[]数组转化为String类型
                    JSONObject jsonObject = new JSONObject(map);
                    jsonObject.put("head2",getByte());
                    Log.i("tag","jsonObject = " + jsonObject);
                    printWriter.println(jsonObject);

                    printWriter.flush();
                    socket.shutdownOutput();
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取当前用户的头像
     */
    public byte[] getByte(){
        SQLiteDatabase sqLiteDatabase = loginActivity.databaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("user_head",new String[]{"username","head"},"username=?",new String[]{MainActivity.username},null,null,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                image = cursor.getBlob(cursor.getColumnIndex("head"));
            }
        }
        sqLiteDatabase.close();
        return image;
    }

    public void initView(){
        etNote = (EditText) findViewById(R.id.etNote);
        etNote.setText("我是" + MainActivity.username);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSend = (TextView) findViewById(R.id.tvSend);
        tvCancel.setOnClickListener(this);
        tvSend.setOnClickListener(this);
    }
}
