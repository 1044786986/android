package com.example.ljh.wechat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by ljh on 2017/9/26.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String name = "ljh.db"; //数据库名称
    private static final int version = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE user_friends (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "my VARCHAR, friend VARCHAR )");

        sqLiteDatabase.execSQL("CREATE TABLE user_head (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "username VARCHAR, head BLOB )");

        sqLiteDatabase.execSQL("CREATE TABLE chat_log (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "fromUser VARCHAR ,toUser VARCHAR,text VARCHAR,image BLOB,date VARCHAR,voicePath VARCHAR)");

        sqLiteDatabase.execSQL("CREATE TABLE recent_chat (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "my VARCHAR ,friend VARCHAR)");

        sqLiteDatabase.execSQL("CREATE TABLE add_address (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "my VARCHAR ,friend VARCHAR ,head BLOB ,data VARCHAR,state VARCHAR ,date VARCHAR)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i == 1 && i1 == 2){
            Log.i("tag","----------数据库升级啦");
        }
    }
}
