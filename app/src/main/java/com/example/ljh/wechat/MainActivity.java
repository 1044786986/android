package com.example.ljh.wechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jauker.widget.BadgeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private static ImageButton ib_tab_chat,ib_tab_address,ib_tab_share,ib_tab_my;
    private static TextView tvChat,tvAddress,tvShare,tvMy;
    private  LinearLayout linearLayout_chat,linearLayout_address,linearLayout_share,linearLayout_my;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private  Fragment fragment_chat,fragment_address,fragment_share,fragment_my;
    static BadgeView badgeView1,badgeView2,badgeView3,badgeView4;
    static Vibrator vibrator;

    static String username;
    static String toUser;
    static int ChatCount = 0;
    private static boolean isFirstPlay = true;

    private Handler handler = new Handler();

    static FragmentAddress fragmentAddress;
    static MainActivity Instance;

    static final String Ip = "192.168.155.1";
    static final String SearchMyHead = "http://" + Ip + ":8080/wechat/SearchMyHeadServlet";
    static final String  SearchFriendsServlet = "http://" + Ip +":8080/wechat/SearchFriendsServlet";
    static final String SetPostServlet = "http://" + Ip + ":8080/wechat/SetPostServlet2";
    static final String GetPostServlet = "http://" + Ip + ":8080/wechat/GetPost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity();
        /*//透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
    }

    private void startActivity(){
        Instance = this;
        getUserName();  //获取当前用户名
        getSetting();
        initView();
        selected(4);
        selected(3);
        selected(2);
        fragmentAddress = new FragmentAddress();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linearLayout_chat:
                selected(1);
                break;
            case R.id.linearLayout_address:
                selected(2);
                break;
            case R.id.linearLayout_share:
                selected(3);
                break;
            case R.id.linearLayout_my:
                selected(4);
                break;
        }
    }

    public void select1(){
        selected(1);
    }

    public void selected(int i){
         fragmentManager = getSupportFragmentManager();
         fragmentTransaction = fragmentManager.beginTransaction();

        setNormalColor();
        hideFragment();
        switch (i){
            case 1:
                ib_tab_chat.setImageResource(R.drawable.ic_chat_bubble_outline_blue_24dp);
                tvChat.setTextColor(Color.rgb(0,188,212));

                if(fragment_chat == null){
                    fragment_chat = new FragmentChat();
                    fragmentTransaction.add(R.id.Main_FrameLayout,fragment_chat);

                }else {
                    fragmentTransaction.show(fragment_chat);
                }
                break;
            case 2:
                ib_tab_address.setImageResource(R.drawable.ic_group_blue_24dp);
                tvAddress.setTextColor(Color.rgb(0,188,212));

                if(fragment_address == null){
                    fragment_address = new FragmentAddress();
                    fragmentTransaction.add(R.id.Main_FrameLayout,fragment_address);
                }else {
                    fragmentTransaction.show(fragment_address);
                    fragmentAddress.ShowRemind(LoginActivity.count);
                }

                break;
            case 3:
                ib_tab_share.setImageResource(R.drawable.ic_chrome_reader_mode_blue_24dp);
                tvShare.setTextColor(Color.rgb(0,188,212));

                if(fragment_share == null){
                    fragment_share = new FragmentShare();
                    fragmentTransaction.add(R.id.Main_FrameLayout,fragment_share);
                }else {
                    fragmentTransaction.show(fragment_share);
                }
                break;
            case 4:
                ib_tab_my.setImageResource(R.drawable.ic_menu_blue_24dp);
                tvMy.setTextColor(Color.rgb(0,188,212));

                if(fragment_my == null){
                    fragment_my = new FragmentMy();
                    fragmentTransaction.add(R.id.Main_FrameLayout,fragment_my);
                }else {
                    fragmentTransaction.show(fragment_my);
                }
                break;
        }
            fragmentTransaction.commit();
    }

    public void hideFragment(){
        if(fragment_chat != null){
            fragmentTransaction.hide(fragment_chat);
        }
        if(fragment_address != null){
            fragmentTransaction.hide(fragment_address);
        }
        if(fragment_share != null){
            fragmentTransaction.hide(fragment_share);
        }
        if(fragment_my != null){
            fragmentTransaction.hide(fragment_my);
        }
    }


    public void setNormalColor(){
        ib_tab_chat.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
        ib_tab_address.setImageResource(R.drawable.ic_group_black_24dp);
        ib_tab_share.setImageResource(R.drawable.ic_chrome_reader_mode_black_24dp);
        ib_tab_my.setImageResource(R.drawable.ic_menu_black_24dp);

        tvChat.setTextColor(Color.rgb(0,0,0));
        tvAddress.setTextColor(Color.rgb(0,0,0));
        tvShare.setTextColor(Color.rgb(0,0,0));
        tvMy.setTextColor(Color.rgb(0,0,0));
    }

    public void getUserName(){
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }

    public static void ShowRemind(final int i, final int count){
        switch (i){
            case 1:
                badgeView1.setBadgeCount(count);
                break;
            case 2:
                badgeView2.setBadgeCount(count);
                break;
            case 3:
                badgeView3.setBadgeCount(count);
                break;
            case 4:
                badgeView4.setBadgeCount(count);
                break;
        }
    }

    static void HideRemind(int i,int count){
        switch (i){
            case 1:
                if(ChatCount > 0){
                    badgeView1.setBadgeCount(ChatCount - count);
                    ChatCount-=count;
                }
                break;
            case 2:
                badgeView2.setBadgeCount(0);
                break;
            case 3:
                badgeView3.setBadgeCount(0);
                break;
            case 4:
                badgeView4.setBadgeCount(0);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isFirstPlay == false){
            /**
             * 清除通知
             */
            for(int i =0;i<NotificationManager1.IdList.size();i++) {
                NotificationManager1.notificationManager.cancel(NotificationManager1.IdList.get(i));
            }
            /**
             * 更新聊天列表
             */
            FragmentChat.updateAdapter();
            Log.i("tag","-----------------onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(isFirstPlay == false){
        /**
         * 清除通知

        for(int i =0;i<NotificationManager1.IdList.size();i++) {
            NotificationManager1.notificationManager.cancel(NotificationManager1.IdList.get(i));
        }
        /**
         * 更新聊天列表

            /*FragmentChat.updateAdapter();
            Log.i("tag","-----------------onResume");
        }*/
        isFirstPlay = false;
    }

    /**
     * 获取消息提醒设置
     */
    public void getSetting(){
        MessageRemindActivity messageRemindActivity = new MessageRemindActivity();
        //messageRemindActivity.addSharePreferences();
        messageRemindActivity.addSharePreferences();
    }

    public void initView(){
        ib_tab_chat = (ImageButton) findViewById(R.id.ib_tab_chat);
        ib_tab_address = (ImageButton) findViewById(R.id.ib_tab_address);
        ib_tab_share = (ImageButton) findViewById(R.id.ib_tab_share);
        ib_tab_my = (ImageButton) findViewById(R.id.ib_tab_my);

        tvChat = (TextView) findViewById(R.id.tvChat);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvShare = (TextView) findViewById(R.id.tvShare);
        tvMy = (TextView) findViewById(R.id.tvMy);

        linearLayout_chat = (LinearLayout) findViewById(R.id.linearLayout_chat);
        linearLayout_address = (LinearLayout) findViewById(R.id.linearLayout_address);
        linearLayout_share = (LinearLayout) findViewById(R.id.linearLayout_share);
        linearLayout_my = (LinearLayout) findViewById(R.id.linearLayout_my);
        linearLayout_chat.setOnClickListener(this);
        linearLayout_address.setOnClickListener(this);
        linearLayout_share.setOnClickListener(this);
        linearLayout_my.setOnClickListener(this);

        badgeView1 = new BadgeView(this);
        badgeView2 = new BadgeView(this);
        badgeView3 = new BadgeView(this);
        badgeView4 = new BadgeView(this);
        badgeView1.setTargetView(linearLayout_chat);
        badgeView2.setTargetView(linearLayout_address);
        badgeView3.setTargetView(linearLayout_share);
        badgeView4.setTargetView(linearLayout_my);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    /**
     * 重写返回按钮，使程序点击返回按钮不退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
