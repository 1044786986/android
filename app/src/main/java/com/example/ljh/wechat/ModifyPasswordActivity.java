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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/11/23.
 */

public class ModifyPasswordActivity extends AppCompatActivity{
    private EditText etOriginalPassword,etPassword,etPassword2;     //原始密码，修改密码，确认密码
    private Button btSubmit;
    private EncodeManager encodeManager;
    private TextView tvBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypassword);
        getSupportActionBar().hide();

        encodeManager = new EncodeManager();
        initView();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.obj + "";
            if (result.equals("false")) {
                showDialogPassWord();
            } else if (result.equals("true")) {
                Toast.makeText(ModifyPasswordActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else if(result.equals("getDataFalse")){
                Toast.makeText(ModifyPasswordActivity.this, "网络错误!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void modifyPassword(final String password){
        ExecutorService executorService = ThreadManager.startThread();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .connectTimeout(5000,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("type","modifyPassword")
                        .add("password",encodeManager.ShaEncode(password))
                        .add("username",MainActivity.username)
                        .build();
                Request request = new Request.Builder()
                        .post(requestBody)
                        .url(MainActivity.RegisterServlet)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = handler.obtainMessage();
                        message.obj = "getDataFalse";
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Message message = handler.obtainMessage();
                        if(result.equals("false")){
                            message.obj = "false";
                        }else if(result.equals("true")){
                            message.obj = "true";
                        }
                        message.sendToTarget();
                    }
                });
            }
        });
    }


    public void initView(){
        tvBack = (TextView) findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etOriginalPassword = (EditText) findViewById(R.id.etOriginalPassword);
        etPassword = (EditText) findViewById(R.id.etPassWord);
        etPassword2 = (EditText) findViewById(R.id.etPassWord2);
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originalPassword = etOriginalPassword.getText()+"";
                String password = etPassword.getText()+"";
                String password2 = etPassword2.getText()+"";
                if(originalPassword == "" || password == "" || password2 == ""){
                    showDialog();
                }
                else if(!password.equals(password2)){
                    showDialogPassWord2();
                    return;
                }else{
                    modifyPassword(password);
                }
            }
        });
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("请输入完整的数据!");
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
        builder.setMessage("密码错误!");
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
}
