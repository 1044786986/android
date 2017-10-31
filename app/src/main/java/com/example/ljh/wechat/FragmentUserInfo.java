package com.example.ljh.wechat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by ljh on 2017/10/26.
 */

public class FragmentUserInfo extends Fragment implements View.OnClickListener{
    private ImageView ivHead;
    private TextView tvUserName,tvArticle;
    private LinearLayout layout_article;
    private Button btSendMessage,btDelete;

    private String toUser;

    private Handler handler = new Handler(Looper.getMainLooper());

    FragmentUserInfo(String toUser){
        this.toUser = toUser;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentuserinfo,null);
        initView(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSendMessage:
                UserChatActivity.userChatActivity.showFragment(UserChatActivity.USERCHAT);
                break;
            case R.id.btDeleteFriend:
                showAlertDialog();
                break;
        }
    }

    public void deleteFriend(){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .connectTimeout(5000,TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("type","deleteFriend")
                        .add("fromUser",MainActivity.username)
                        .add("toUser",toUser)
                        .build();
                Request request = new Request.Builder()
                        .url(MainActivity.SearchFriendsServlet)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"当前无网络,删除好友失败",Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        FragmentAddress fragmentAddress = new FragmentAddress();
                        fragmentAddress.removeDatalist(toUser);
                        //fragmentAddress.updataRecycleView();
                        FragmentChat fragmentChat = new FragmentChat();
                        fragmentChat.removeDatalist(toUser);
                        //fragmentChat.updateAdapter();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"已删除好友",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });

            }
        }.start();
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要删除该联系人吗");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void initView(View view){
        ivHead = (ImageView) view.findViewById(R.id.ivHead);
        tvArticle = (TextView) view.findViewById(R.id.tvArticle);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        layout_article = (LinearLayout) view.findViewById(R.id.layout_article);
        btSendMessage = (Button) view.findViewById(R.id.btSendMessage);
        btDelete = (Button) view.findViewById(R.id.btDeleteFriend);
        btSendMessage.setOnClickListener(this);
        btDelete.setOnClickListener(this);

        tvUserName.setText(toUser);
        ivHead.setImageBitmap(FragmentAddress.getFriendHead(toUser));
    }


}
