package com.example.ljh.wechat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by ljh on 2017/9/19.
 */

public class CreateSqlite extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);

        /*sqLiteDatabase.execSQL("CREATE TABLE user_friends (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "my VARCHAR, friend VARCHAR )");*/
        /*sqLiteDatabase.execSQL("CREATE TABLE user_head (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "username VARCHAR, head BLOB )");*/

        sqLiteDatabase.execSQL("DROP TABLE chat_log");

       /* sqLiteDatabase.execSQL("CREATE TABLE chat_log (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "fromUser VARCHAR,toUser VARCHAR,text VARCHAR,image BLOB,date VARCHAR,voicePath VARCHAR)");*/

        /*sqLiteDatabase.execSQL("CREATE TABLE recent_chat (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "my VARCHAR ,friend VARCHAR)");*/

        /*sqLiteDatabase.execSQL("CREATE TABLE add_address (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "my VARCHAR ,friend VARCHAR ,head BLOB ,data VARCHAR,state VARCHAR ,date VARCHAR)");*/

        sqLiteDatabase.close();
    }
}
