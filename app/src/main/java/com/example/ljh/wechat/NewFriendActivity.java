package com.example.ljh.wechat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljh on 2017/9/25.
 */

public class NewFriendActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private TextView tvBack;
    private LinearLayoutManager linearLayoutManager;

    private RecycleViewAdapter_NewFriend adapter_newFriend;

    private static List<AddFriendBean> datalist = new ArrayList<AddFriendBean>();

    private Handler handler = new Handler();
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriend);
        getSupportActionBar().hide();
        getData();
        initView();
    }

    public void setDatalist(JSONObject jsonObject){
        Bitmap bitmap = null;
        try {
            String username = jsonObject.getString("fromUser");
            String data = jsonObject.getString("data");
            String date = getTime();
            String head1 = jsonObject.getString("head");
            byte[] head = Base64.decode(head1,Base64.DEFAULT);
            if(head != null){
                bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
            }
            datalist.add(new AddFriendBean(username,data,bitmap,"null",date));
            insertSqlite(MainActivity.username,username,head,data,"null",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加记录添加到sqlite数据库
     */
    public void insertSqlite(String my,String friend,byte head[],String data,String state,String date){
        sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("my",my);
        contentValues.put("friend",friend);
        contentValues.put("head",head);
        contentValues.put("data",data);
        contentValues.put("state",state);
        contentValues.put("date",date);
        sqLiteDatabase.insert("add_address",null,contentValues);
        sqLiteDatabase.close();
        updateAdapter();
    }

    public void updateSqlite(String friend,String state){
        sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("state",state);
        sqLiteDatabase.update("add_address",contentValues,"friend=?",new String[]{friend});
        updateAdapter();
    }

    void getData(){
        datalist.clear();
        sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        Cursor cursor = sqLiteDatabase.query("add_address",new String[]{"friend","head","data","state","date"},"my=?",
                new String[]{MainActivity.username},null,null,null);
        while (cursor.moveToNext()){
            String friend = cursor.getString(cursor.getColumnIndex("friend"));
            byte head[] = cursor.getBlob(cursor.getColumnIndex("head"));
            String data = cursor.getString(cursor.getColumnIndex("data"));
            String state = cursor.getString(cursor.getColumnIndex("state"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
            datalist.add(new AddFriendBean(friend,data,bitmap,state,date));
        }
        sqLiteDatabase.close();
    }

    public void updateAdapter(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter_newFriend.notifyDataSetChanged();
            }
        });
    }

    public String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String string = simpleDateFormat.format(date);
        return string;
    }

    public void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.RecycleView_NewFriend);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter_newFriend = new RecycleViewAdapter_NewFriend(NewFriendActivity.this,datalist);
        recyclerView.setAdapter(adapter_newFriend);

        tvBack = (TextView) findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewFriendActivity.this.finish();
            }
        });
    }
}
