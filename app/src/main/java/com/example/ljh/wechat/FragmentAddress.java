package com.example.ljh.wechat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jauker.widget.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
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

public class FragmentAddress extends Fragment implements View.OnClickListener{
        private RecyclerView recyclerView;
        private SearchView searchView;
        private ImageView ivAdd;    //增加好友
        private TextView tvFriendCount; //好友数量
        private LinearLayout linearLayout_newfriend;    //新的朋友
        private LinearLayout fragment_address_layout;
        static BadgeView badgeView; //消息红点提示
        private LinearLayoutManager linearLayoutManager;

        private OkHttpClient okHttpClient;
        private Handler handler = new Handler(Looper.getMainLooper());

        private static List<AddressBean> datalist;
        private String username = null;
        private String url = MainActivity.SearchFriendsServlet;

        private static RecycleViewAdapter_address recycleViewAdapter_address;

        private FragmentManager fragmentManager;
        private FragmentTransaction fragmentTransaction;

        public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_address,null);
            datalist = new ArrayList<AddressBean>();

            initView(view);
            getAddress();

            return  view;
    }

    public void getAddress(){
        new Thread(){
            @Override
            public void run() {
                okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("type","address")
                        .add("username",username)
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
                            String result = response.body().string();
                            response.body().close();

                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("friend");

                            Gson gson = new Gson();
                            List<Address_headString_Bean> list = new ArrayList<Address_headString_Bean>();
                            list = gson.fromJson(jsonArray.toString(),new TypeToken<List<Address_headString_Bean>>(){}.getType());
                            for(int i=0;i<list.size();i++){
                                String username = list.get(i).getUsername();
                                String headString = list.get(i).getHead();
                                byte head[] = Base64.decode(headString,Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                                datalist.add(new AddressBean(username,bitmap));
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    recycleViewAdapter_address = new RecycleViewAdapter_address(getActivity(), datalist);
                                    recyclerView.setAdapter(recycleViewAdapter_address);
                                    setItemOnClickListener(recycleViewAdapter_address);
                                    tvFriendCount.setText("总共有" + datalist.size() + "位好友");
                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    mainActivity.select1();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }.start();
    }


    public void setItemOnClickListener(RecycleViewAdapter_address recycleViewAdapter_address){
        recycleViewAdapter_address.setItemSelectListener(new RecycleViewAdapter_address.RecycleView_Address_ItemViewListener() {
            @Override
            public void onItemClick(View view, int position) {
                String toUser = datalist.get(position).getName();   //获取当前聊天对象的名字
                insetrIntoSqlite(toUser);   //保存到最近聊天的用户

                Intent intent = new Intent(getActivity(),UserChatActivity.class);//跳转到聊天界面
                intent.putExtra("toUser",toUser);
                startActivity(intent);
            }
        });
    }

    public void insetrIntoSqlite(String friend){
        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);
        Cursor cursor = sqLiteDatabase.query("recent_chat",new String[]{"my","friend"},"my=? and friend=?",
        new String[]{MainActivity.username,friend},null,null,null);
        if(cursor.getCount() == 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("my",MainActivity.username);
            contentValues.put("friend",friend);
            sqLiteDatabase.insert("recent_chat",null,contentValues);
        }
        sqLiteDatabase.close();
    }

    public void setDatalist(String username,Bitmap bitmap){
        datalist.add(new AddressBean(username,bitmap));
    }

    public  void updataRecycleView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                recycleViewAdapter_address.notifyDataSetChanged();
                tvFriendCount.setText("总共有" + datalist.size() + "位好友");
            }
        });
    }

    public static Bitmap getFriendHead(String friend){
        int i;
        Bitmap bitmap = null;
        for(i = 0;i<datalist.size();i++){
            if(datalist.get(i).getName().equals(friend)){
                break;
            }
        }
        if(i != datalist.size()){
           bitmap = datalist.get(i).getBitmap();
        }else{

        }
        return  bitmap;
    }

    public void removeDatalist(String toUser){
        int i =0;
        for(i = 0; i<datalist.size();i++){
            if(datalist.get(i).getName().equals(toUser)){
                break;
            }
        }
        datalist.remove(i);
    }

    public void ShowRemind(int i){
        badgeView.setBadgeCount(i);
    }

    public void HideRemind(){
        badgeView.setBadgeCount(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibAdd:
                Intent intent = new Intent(getActivity(),AddFriendActivity.class);
                startActivity(intent);
                break;
            case R.id.LinearLayout_NewFriend:
                Intent intent1 = new Intent(getActivity(),NewFriendActivity.class);
                startActivity(intent1);
                MainActivity.HideRemind(2,0);
                LoginActivity.count = 0;
                HideRemind();
                break;
            case R.id.fragment_address_layout:
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                break;
        }
    }

    public void initView(View view){

        username = MainActivity.username;
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        searchView = (SearchView) view.findViewById(R.id.SearchView_address);
        searchView.setIconifiedByDefault(true);    //不用点击放大镜就可以打开

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.RecycleView_address);
        recyclerView.setLayoutManager(linearLayoutManager);

        ivAdd = (ImageView) view.findViewById(R.id.ibAdd);
        ivAdd.setOnClickListener(this);

        linearLayout_newfriend = (LinearLayout) view.findViewById(R.id.LinearLayout_NewFriend);
        fragment_address_layout = (LinearLayout) view.findViewById(R.id.fragment_address_layout);
        linearLayout_newfriend.setOnClickListener(this);
        fragment_address_layout.setOnClickListener(this);

        badgeView = new BadgeView(getContext());
        badgeView.setTargetView(linearLayout_newfriend);

        tvFriendCount = (TextView) view.findViewById(R.id.tvFriendCount);
    }

}
