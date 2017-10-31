package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/9/20.
 */

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{
    private OkHttpClient okHttpClient;
    private SearchView searchView;
    private ListView listView;
    private TextView tvBack;
    private LinearLayout activity_addfriend_layout;

    private String url = MainActivity.SearchFriendsServlet;
    private String username = null;
    private String result;
    private String friend = null; //判断该用户是否已经是好友
    private List<AddressBean> datalist;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        getSupportActionBar().hide();
        datalist = new ArrayList<AddressBean>();
        initView();
    }

    public void SendData(){
        new Thread(){
            @Override
            public void run() {
                okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .connectTimeout(5000,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("type","Search")
                        .add("username",username)   //好友名字
                        .add("myname",MainActivity.username)    //当前用户名
                        .build();

                final Request request = new Request.Builder()
                        .post(requestBody)
                        .url(url)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            result = response.body().string();
                            response.body().close();

                            JSONObject jsonObject = new JSONObject(result);
                            String username = jsonObject.getString("username");
                            String headString  = jsonObject.getString("head");
                            friend = jsonObject.getString("friend");
                            byte head[] = Base64.decode(headString,Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                            datalist.clear();
                            datalist.add(new AddressBean(username,bitmap));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ListViewAdapter_Add adapter = new ListViewAdapter_Add(AddFriendActivity.this,datalist,friend);
                                listView.setAdapter(adapter);
                            }
                        });

                    }
                });
            }
        }.start();
    }

    public void initView(){
        listView = (ListView) findViewById(R.id.ListView_address);
        searchView = (SearchView) findViewById(R.id.SearchView_add);
        tvBack = (TextView) findViewById(R.id.tvBack);
        activity_addfriend_layout = (LinearLayout) findViewById(R.id.activity_addfriend_layout);
        tvBack.setOnClickListener(this);
        activity_addfriend_layout.setOnClickListener(this);
        //searchView.setSubmitButtonEnabled(true);
        searchView.onActionViewExpanded();      //让searchview默认打开
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                username = s;
                SendData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /**
             * 返回
             */
            case R.id.tvBack:
                AddFriendActivity.this.finish();
                break;
            /**
             * 隐藏软键盘
             */
            case R.id.activity_addfriend_layout:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                break;
        }
    }
}
