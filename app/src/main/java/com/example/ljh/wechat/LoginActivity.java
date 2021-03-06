package com.example.ljh.wechat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ljh on 2017/9/14.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btLogin; //登录按钮
    private EditText etUserName, etPassWord; //输入用户名和密码
    private CheckBox cbPassWord, cbAccount;    //保存密码和自动登录
    private TextView tvRegister;    //跳转注册页面
    private ProgressDialog progressDialog;
    static String result;   //服务器返回的结果
    private String username;    //用户名
    private String password;    //密码
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static Socket socket;

    private InputStream inputStream;    //输入流
    private InputStreamReader inputStreamReader;
    private OutputStream outputStream;  //输出流
    private BufferedReader bufferedReader;

    static int count = 0; //提醒消息的数量

    static DatabaseHelper databaseHelper;
    static SQLiteDatabase sqLiteDatabase;

    private IntentFilter intentFilter;
    private NetWorkChangeReceiver mNetWorkChangeReceiver;
    FragmentAddress fragmentAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();
        databaseHelper = new DatabaseHelper(LoginActivity.this,"ljh.db",null,2);
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        result = null;
        initNetWorkChangeReceiver();
        initView();
        getSetting();
        Check();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btLogin:
                username = etUserName.getText() + "";
                password = etPassWord.getText() + "";
                if (username == null || username == "") { //用户名为空
                    showDialog();
                } else if (password == null || password == "") {   //密码为空
                    showDialogPassWord();
                } else {
                    //SendData();

                    showDialogLogin();
                    login();
                }
                break;
            case R.id.tvRegister:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //intent.getPare
                startActivity(intent);
                break;

        }
    }

    /**
     *
     */
    public void Check() {
         username = sharedPreferences.getString("username", "");
         password = sharedPreferences.getString("password", "");

        if (username != "" && password == "") {     //如果帐号为空的，
            cbPassWord.setChecked(false);
            cbAccount.setChecked(true);
            etUserName.setText(username);
        } else if(password != ""){
            cbAccount.setChecked(true);
            cbPassWord.setChecked(true);
            etUserName.setText(username);
            etPassWord.setText(password);
        }

        if(username != "" && password != "" && cbPassWord.isChecked()){
            showDialogLogin();
            login();
        }

        cbPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbPassWord.isChecked() && !cbAccount.isChecked()) {
                    cbAccount.setChecked(true);
                }
            }
        });

        cbAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbPassWord.isChecked() && !cbAccount.isChecked()) {
                    cbPassWord.setChecked(false);
                }
            }
        });
    }

    /**
     * 保存帐号密码
     */
    public void AddSharedPreferences() {
        if (cbAccount.isChecked()) {
            editor.putString("username", username);
        }else{
            editor.remove("username");
        }
        if(cbPassWord.isChecked()){
            editor.putString("password", password);
        }
        else {
            editor.remove("password");
        }
        editor.commit();
    }

    /**
     * 发送数据到服务器
     */
    public void login() {
        new Thread() {
            @Override
            public void run() {
                try {
                    /**
                     * 向服务器发送数据
                     */
                    socket = new Socket(MainActivity.Ip,8888);
                    OutputStream outputStream = socket.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                    PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("type","login");
                    map.put("username",username);
                    map.put("password",EncodeManager.ShaEncode(password));
                    printWriter.print(new JSONObject(map));
                    printWriter.flush();
                    socket.shutdownOutput();
                    /**
                     * 从服务器接收数据
                     */
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String result = null;
                    while ((result = bufferedReader.readLine()) != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        Message message = handler.obtainMessage();
                        String type = jsonObject.getString("type");
                        if(type.equals("login")){
                            message.obj = jsonObject;
                            message.sendToTarget();
                        }
                        else if(type.equals("AddFriend")){
                            count++;    //消息数量+1
                            message.obj = jsonObject;
                            message.sendToTarget();
                        }
                        else if(type.equals("AddFriend_Answer")){
                            String answer = jsonObject.getString("answer");
                            if(answer.equals("true")) {
                                String fromUser = jsonObject.getString("fromUser");
                                String headString = jsonObject.getString("head");
                                byte head[] = Base64.decode(headString, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(head, 0, head.length);
                                fragmentAddress = new FragmentAddress();
                                fragmentAddress.setDatalist(fromUser, bitmap);
                                fragmentAddress.updataRecycleView();
                            }

                        }else if(type.equals("text")){
                            String messageString = jsonObject.getString("message");
                            String fromUser = jsonObject.getString("fromUser");
                            String date = jsonObject.getString("date");
                            InsertChatLogInSqlite(fromUser,MainActivity.username,messageString,null,date,null);
                            FragmentChat fragmentChat = new FragmentChat();
                            fragmentChat.addDatalist(fromUser,MainActivity.username,messageString,null,date,null);
                            Log.i("aa","UserChat_State = " + UserChatActivity.UserChat_State);
                            /**
                             * 如果不在聊天界面里面
                             */
                            if(UserChatActivity.UserChat_State != 1 && !UserChatActivity.toUser.equals(fromUser)){
                                MainActivity.ChatCount++;//未读消息总数
                                //MainActivity.vibrator.vibrate(500);
                                FragmentChat.updateAdapter();
                                if(MessageRemindActivity.msg){  //开启了消息提醒
                                    NotificationManager1.sendNotification(fromUser,messageString,MainActivity.Instance,MainActivity.ChatCount);
                                }
                            }
                            message.obj = jsonObject;
                            message.sendToTarget();
                        }
                        else if(type.equals("sendImageMessage")){
                            String fromUser = jsonObject.getString("fromUser");
                            String toUser = jsonObject.getString("toUser");
                            String date = jsonObject.getString("date");
                            String imageString = jsonObject.getString("image");

                            FragmentChat fragmentChat = new FragmentChat();
                            byte image[] = Base64.decode(imageString,Base64.DEFAULT);
                            InsertChatLogInSqlite(fromUser,toUser,null,image,date,null);
                            fragmentChat.addDatalist(fromUser,toUser,null,image,date,null);

                            if(UserChatActivity.UserChat_State != 1 && !UserChatActivity.toUser.equals(fromUser)){
                                MainActivity.ChatCount++;//未读消息总数
                                //MainActivity.vibrator.vibrate(500);
                                FragmentChat.updateAdapter();
                                if(MessageRemindActivity.msg){
                                    NotificationManager1.sendNotification(fromUser,"图片",LoginActivity.this,1);
                                }
                                }

                            message.obj = jsonObject;
                            message.sendToTarget();

                        }
                        else if(type.equals("sendVoiceMessage")){
                            String fromUser = jsonObject.getString("fromUser");
                            String toUser = jsonObject.getString("toUser");
                            String date = jsonObject.getString("date");
                            String fileString = jsonObject.getString("voice");
                            /*FileInputStream fileInputStream = new FileInputStream(fileString);
                            FileDescriptor fileDescriptor = fileInputStream.getFD();*/
                            File file = new File(getApplication().getFilesDir().getPath(),date+".amr");
                            if(!file.exists()){
                                file.createNewFile();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            byte voice[] = Base64.decode(fileString,Base64.DEFAULT);
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(voice);
                            byte[] buffer = new byte[1024];
                            int byteread = 0;
                            while ((byteread = byteArrayInputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, byteread); // 文件写操作
                            }
                            //fileOutputStream.write(voice);
                            fileOutputStream.close();
                            InsertChatLogInSqlite(fromUser,toUser,null,null,date,date+".amr");
                            FragmentChat fragmentChat = new FragmentChat();
                            fragmentChat.addDatalist(fromUser,toUser,null,null,date,date+".amr");

                            if(UserChatActivity.UserChat_State != 1 && !UserChatActivity.toUser.equals(fromUser)){
                                MainActivity.ChatCount++;//未读消息总数
                                //MainActivity.vibrator.vibrate(500);
                                FragmentChat.updateAdapter();
                                if(MessageRemindActivity.msg){
                                    NotificationManager1.sendNotification(fromUser,"语音消息",LoginActivity.this,1);
                                }
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            try {
                JSONObject jsonObject = new JSONObject(msg.obj+"");
                String type = jsonObject.getString("type");
                if(type.equals("login")) {      //登录
                    String result = jsonObject.getString("result");
                    if (result.equals("true")) {
                        AddSharedPreferences();
                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else{
                        Toast.makeText(LoginActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
                else if(type.equals("AddFriend")){  //添加好友
                    MainActivity.ShowRemind(2,count);
                    NewFriendActivity newFriendActivity = new NewFriendActivity();
                    newFriendActivity.setDatalist(jsonObject);
                }
                else if(type.equals("text")){
                    MainActivity.ShowRemind(1,MainActivity.ChatCount);
                }
                else if(type.equals("sendImageMessage")){
                    MainActivity.ShowRemind(1,MainActivity.ChatCount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void InsertChatLogInSqlite(String fromUser,String toUser,String text,byte image[],String date,String voiceName){
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("fromUser",fromUser);
        contentValues.put("toUser",toUser);
        contentValues.put("text",text);
        contentValues.put("image",image);
        contentValues.put("date",date);
        contentValues.put("voicePath",voiceName);
        sqLiteDatabase.insert("chat_log",null,contentValues);
        sqLiteDatabase.close();
    }

    /**
     * 初始化APP设置
     */
    public void getSetting(){
        String currentLanguage = sharedPreferences.getString("currentLanguage","");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if(currentLanguage.equals("跟随系统")){
            conf.setLocale(Locale.getDefault());
        }else if(currentLanguage.equals("简体中文")){
            conf.setLocale(Locale.SIMPLIFIED_CHINESE);
        }else if(currentLanguage.equals("english")){
            conf.setLocale(Locale.ENGLISH);
        }
        res.updateConfiguration(conf, dm);
    }

    /**
     * 监听网络广播
     */
    public void initNetWorkChangeReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetWorkChangeReceiver = new NetWorkChangeReceiver();
        registerReceiver(mNetWorkChangeReceiver,intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetWorkChangeReceiver);
    }

    /**
     * 初始化
     */
    public void initView() {
        btLogin = (Button) findViewById(R.id.btLogin);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassWord = (EditText) findViewById(R.id.etPassWord);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        btLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        cbPassWord = (CheckBox) findViewById(R.id.cbPassWord);
        cbAccount = (CheckBox) findViewById(R.id.cbAccount);

        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("请输入用户名");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogPassWord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("请输入密码");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogLogin() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在登录...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}