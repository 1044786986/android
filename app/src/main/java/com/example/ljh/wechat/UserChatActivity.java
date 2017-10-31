package com.example.ljh.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by ljh on 2017/10/5.
 */

public class UserChatActivity extends FragmentActivity{
    Fragment fragment_userChat,FragmentUserInfo;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private static String toUser;
    static int UserChat_State = 0;

    static  final int USERCHAT = 1;
    static final int USERINFO = 2;

    static UserChatActivity userChatActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userchat);
        userChatActivity = this;
        initView();
        showFragment(USERCHAT);
        UserChat_State = 1;
    }

    public void showFragment(int i){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        switch (i){
            case USERCHAT:
               /* if(fragmentManager.findFragmentByTag(toUser) == null){
                    fragment_userChat = new fragment_UserChat(toUser);*/
                if(fragment_userChat == null){
                    fragment_userChat = new fragment_UserChat(toUser);
                    fragmentTransaction.add(R.id.UserChat_FrameLayout,fragment_userChat);
                }else{
                    fragmentTransaction.show(fragment_userChat);
                }
                break;
            case USERINFO:
                if(FragmentUserInfo == null){
                    FragmentUserInfo = new FragmentUserInfo(toUser);
                    fragmentTransaction.add(R.id.UserChat_FrameLayout,FragmentUserInfo);
                }else{
                    fragmentTransaction.show(FragmentUserInfo);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    public void hideFragment(FragmentTransaction fragmentTransaction){
        if(fragment_userChat != null){
            fragmentTransaction.hide(fragment_userChat);
        }
        if(FragmentUserInfo != null){
            fragmentTransaction.hide(FragmentUserInfo);
        }
    }

    public void initView(){
        Intent intent = getIntent();
        toUser = intent.getStringExtra("toUser");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserChat_State = 0;
        //UserChatActivity.this.finish();
        Log.i("tag","-------------Activity is onDestroy");

    }
}
