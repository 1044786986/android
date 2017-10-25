package com.example.ljh.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by ljh on 2017/10/5.
 */

public class UserChatActivity extends FragmentActivity{
    private Fragment fragment_userChat;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String toUser;
    static int UserChat_State = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userchat);
        initView();
        showFragment();
        UserChat_State = 1;
    }

    public void showFragment(){
        if(fragmentManager.findFragmentByTag(toUser) == null){
            fragment_userChat = new fragment_UserChat(toUser);
            fragmentTransaction.add(R.id.UserChat_FrameLayout,fragment_userChat);
        }else{
            fragmentTransaction.show(fragmentManager.findFragmentByTag(toUser));
        }
        fragmentTransaction.commit();
    }

    public void initView(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Intent intent = getIntent();
        toUser = intent.getStringExtra("toUser");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserChat_State = 0;
    }
}
