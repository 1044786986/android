package com.example.ljh.wechat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * Created by ljh on 2017/9/18.
 */

public class FragmentMy extends Fragment implements View.OnClickListener{
    private static ImageView ivHead;    //头像
    private TextView tvUserName;        //用户名
    private static Bitmap bitmap;       //当前用户头像
    private LinearLayout linearLayout_KaoShiApp;    //跳转app
    private LinearLayout layout_setting,layout_quit,layout_myInformation;            //设置

    private static SQLiteDatabase sqLiteDatabase;
    private Handler handler = new Handler();

    private PreViewManager preViewManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,null);
        preViewManager = new PreViewManager();
        initView(view);
        updateHead();
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.ivHead:
                intent = new Intent(getActivity(),ModifyHeadActivity.class);
                intent.putExtra("Bitmap",preViewManager.BitmapToByte(bitmap));
                startActivity(intent);
                break;
            case R.id.layout_setting:
                intent = new Intent(getActivity(),SetUpActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_quit:
                SharedPreferences.Editor editor = LoginActivity.editor;
                editor.remove("password");  //取消记住密码自动登录
                editor.commit();

                bitmap = null;

                intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    public void updateHead(){

        if(bitmap == null){
            getHead2();
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(bitmap != null){
                        ivHead.setImageBitmap(bitmap);
                    }else {
                        ivHead.setImageResource(R.mipmap.ic_person_black_24dp);
                    }
                }
            });
        }
    }

   /* public static Bitmap getHead(){
        Bitmap bitmap = null;
        //sqLiteDatabase = LoginActivity.databaseHelper.getReadableDatabase();
        sqLiteDatabase = MainActivity.Instance.openOrCreateDatabase("ljh.db",0,null);
        //sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);
        Cursor cursor = sqLiteDatabase.query("user_head",new String[]{"username","head"},"username=?",new String[]{MainActivity.username},null,null,null);
        if(cursor != null){
            while (cursor.moveToNext()){
                image = cursor.getBlob(cursor.getColumnIndex("head"));
                bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            }
        }
        sqLiteDatabase.close();
        return bitmap;
    }*/

    /**
     * 第一次登录,数据库没有头像资源,从远程数据库获取
     */
    public void getHead2(){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)
                        .readTimeout(5000,TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("type","myHead")
                        .add("username",MainActivity.username)
                        .build();
                final Request request = new Request.Builder()
                        .post(requestBody)
                        .url(MainActivity.SearchMyHead)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();

                            if(result != null || result != ""){
                                byte head[] = Base64.decode(result,Base64.DEFAULT);
                                bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                                insertMyHeadToSqlite(MainActivity.username,head);
                            }else {
                                bitmap = null;
                            }
                            Message message = handler2.obtainMessage();
                            message.obj = bitmap;
                            message.sendToTarget();

                    }
                });
            }
        }.start();

    }

    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            if(bitmap != null){
                ivHead.setImageBitmap(bitmap);
            }else{
                ivHead.setImageResource(R.mipmap.ic_person_black_24dp);
            }
        }
    };

    public void insertMyHeadToSqlite(String myName,byte head[]){
        sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",myName);
        contentValues.put("head",head);
        sqLiteDatabase.insert("user_head",null,contentValues);
        sqLiteDatabase.close();
    }

    public static Bitmap getMyHead(){
        return bitmap;
    }

    public void initView(View view){
        ivHead = (ImageView) view.findViewById(R.id.ivHead);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserName.setText(MainActivity.username);
        ivHead.setOnClickListener(this);

        //linearLayout_KaoShiApp = (LinearLayout) view.findViewById(R.id.linearLayout_KaoShiApp);
        layout_setting = (LinearLayout) view.findViewById(R.id.layout_setting);
        layout_quit = (LinearLayout) view.findViewById(R.id.layout_quit);
        layout_myInformation = (LinearLayout) view.findViewById(R.id.layout_myInformation);
        //linearLayout_KaoShiApp.setOnClickListener(this);
        layout_myInformation.setOnClickListener(this);
        layout_setting.setOnClickListener(this);
        layout_quit.setOnClickListener(this);
    }
}
