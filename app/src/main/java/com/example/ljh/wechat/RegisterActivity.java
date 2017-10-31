package com.example.ljh.wechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/9/17.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvLogin;
    private EditText etUserName,etPassWord,etPassWord2,etEmail;
    private Button btRegister;
    private String url = "http://192.168.191.1:8080/wechat/RegisterServlet";
    private OkHttpClient okHttpClient;
    RegisterBean bean;
    private String username,password,password2,email;
    private String result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btRegister:
                Check();
                break;
            case R.id.tvLogin:
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result1 = msg.obj+"";
            Log.i("tag","---------msg=" + result1);

            if(result1.equals("true")){
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(RegisterActivity.this,"注册成功!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void SendRegister(){
        bean = new RegisterBean();

        new Thread(){
            @Override
            public void run() {
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .readTimeout(5000,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("username",username)
                        .add("password",EncodeManager.ShaEncode(password))  //对密码进行加密
                        .add("email",email)
                        .build();

                Request request = new Request.Builder()
                        .post(requestBody)
                        .url(url)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        result = response.body().string();
                        response.body().close();

                        Message message = handler.obtainMessage();
                        message.obj = result;
                        message.sendToTarget();
                    }
                });
            }
        }.start();
    }

    public void initView(){
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        btRegister = (Button) findViewById(R.id.btRegister);

        tvLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassWord = (EditText) findViewById(R.id.etPassWord);
        etPassWord2 = (EditText) findViewById(R.id.etPassWord2);
        etEmail = (EditText) findViewById(R.id.etEmail);
    }

    public void Check(){
         username = etUserName.getText()+"";
         password = etPassWord.getText()+"";
         password2 = etPassWord2.getText()+"";
         email = etEmail.getText()+"";

        if(username == null ){
            showDialog();
        }else if(password == null){
            showDialogPassWord();
        }else if(!password.equals(password2)){
            showDialogPassWord2();
        }else if(email == null || email.indexOf(".") == -1 || email.indexOf("@") == -1 ){
            showDialogEmail();
        }
        else if(!email.matches("/^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$/")){
            showDialogEmail();
        }
        else {
            SendRegister();
        }
    }

    public void showDialog(){
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
    public void showDialogPassWord(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("请输入正确的密码格式");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void showDialogPassWord2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("两次输入的密码不一致");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showDialogEmail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("请输入正确的邮箱格式");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
