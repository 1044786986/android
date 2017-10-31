package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jauker.widget.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class FragmentChat extends Fragment{
    private RecyclerView recyclerView;
    private SearchView searchView;
    private LinearLayout fragment_chat_layout;
    private LinearLayoutManager linearLayoutManager;

    private static List<List_chat_logBean> datalist;//所有数据集
    private static List<List_chat_logBean> datalist2;
    private List<AddressBean> userlist;

    private static RecycleViewAdapter_chat recycleViewAdapter_chat;
    private RecycleViewAdapter_chat.ViewHolder viewHolder;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private OkHttpClient okHttpClient;

    private String url = MainActivity.SearchFriendsServlet;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,null,false);
        datalist = new ArrayList<List_chat_logBean>();
        userlist = new ArrayList<AddressBean>();
        datalist2 = new ArrayList<List_chat_logBean>();
        initView(view);
        new Thread(){
            @Override
            public void run() {
                super.run();
                    getAddress();   //获取联系人
                    getChatLog();   //获取聊天记录
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recycleViewAdapter_chat = new RecycleViewAdapter_chat(getActivity(),datalist,userlist);
                        recyclerView.setAdapter(recycleViewAdapter_chat);
                        setOnClick();
                    }
                });
            }
        }.start();
        return view;
    }


    public void getChatLog(){
        List<String> list = new ArrayList<String>();//暂时保存好友名
        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);
        Cursor cursor = sqLiteDatabase.query("recent_chat",new String[]{"friend"},"my=?",
                new String[]{MainActivity.username},null,null,null);
        while (cursor.moveToNext()){
            String friend = cursor.getString(cursor.getColumnIndex("friend"));
            list.add(friend);
        }
        if(cursor.getCount() != 0) {    //如果该用户有好友

            for (int i = 0; i < list.size(); i++) {
                String friendName = list.get(i);
                List<Chat_LogBean>LogList = new ArrayList<Chat_LogBean>();  //暂时保存聊天记录
                /**
                 * 我发给好友的记录
                 */
                Cursor cursor1 =
                        sqLiteDatabase.query("chat_log",new String[]{"fromUser","toUser","text","image","date","voicePath"},
                        "fromUser=? and toUser=?",new String[]{MainActivity.username,friendName},null,null,null);
                while (cursor1.moveToNext()) {
                    String fromUser = cursor1.getString(cursor1.getColumnIndex("fromUser"));
                    String toUser = cursor1.getString(cursor1.getColumnIndex("toUser"));
                    String text = cursor1.getString(cursor1.getColumnIndex("text"));
                    byte image[] = cursor1.getBlob(cursor1.getColumnIndex("image"));
                    String date = cursor1.getString(cursor1.getColumnIndex("date"));
                    String voicePath = cursor1.getString(cursor1.getColumnIndex("voicePath"));
                    LogList.add(new Chat_LogBean(fromUser, toUser, text,image, date,voicePath));
                }
                /**
                 * 好友发给我的记录
                 */
                Cursor cursor2 =
                        sqLiteDatabase.query("chat_log",new String[]{"fromUser","toUser","text","image","date","voicePath"},
                                "fromUser=? and toUser=?",new String[]{friendName,MainActivity.username},null,null,null);
                while (cursor2.moveToNext()) {
                    String fromUser = cursor2.getString(cursor2.getColumnIndex("fromUser"));
                    String toUser = cursor2.getString(cursor2.getColumnIndex("toUser"));
                    String text = cursor2.getString(cursor2.getColumnIndex("text"));
                    byte image[] = cursor2.getBlob(cursor2.getColumnIndex("image"));
                    String date = cursor2.getString(cursor2.getColumnIndex("date"));
                    String voicePath = cursor2.getString(cursor2.getColumnIndex("voicePath"));
                    LogList.add(new Chat_LogBean(fromUser, toUser, text,image, date,voicePath));
                }

                if(LogList.size() != 0){
                    Collections.sort(LogList, new ChatLogCompartor());  //按日期进行排序
                    datalist.add(new List_chat_logBean(friendName,LogList,0));
                }
            }

        }
    }

    public void setOnClick(){
        recycleViewAdapter_chat.setRecycleViewItemClickListener(new RecycleViewAdapter_chat.RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                viewHolder = new RecycleViewAdapter_chat.ViewHolder(view);
                int count = datalist.get(position).getUnread(); //记录当前好友有多少未读消息
                MainActivity.HideRemind(1,count);   //刷新总共未读消息
                datalist.get(position).setUnread(0);    //重置当前好友的未读消息
                hideBadgeView(viewHolder.friendImage);

                String toUser = viewHolder.friendName.getText()+""; //获取好友名
                Intent intent = new Intent(getActivity(),UserChatActivity.class);
                intent.putExtra("toUser",toUser);
                startActivity(intent);
            }
        });
    }

    public void addDatalist(String fromUser,String toUser,String text,byte image[],String date,String voicePath){
        int i;
        String name = null;
        if(toUser.equals(MainActivity.username)) {   //如果是好友发给我
            for (i = 0; i < datalist.size(); i++) {
                name = datalist.get(i).getUsername();   //好友在集合中的位置
                if (name.equals(fromUser)) {
                    break;
                }
            }
            name = fromUser;
        }else {
            for(i=0;i<datalist.size();i++){     //我发给好友
                name = datalist.get(i).getUsername();
                if(name.equals(toUser)){
                    break;
                }
            }
            name = toUser;
        }
        //i等于datalist的长度的话,该用户不存在于聊天记录表,增加该用户和聊天记录
        if(i == datalist.size() && fromUser.equals(MainActivity.username)){
            List<Chat_LogBean>list = new ArrayList<Chat_LogBean>(); //临时聊天记录集合
            list.add(new Chat_LogBean(fromUser,toUser,text,image,date,voicePath));
            datalist.add(new List_chat_logBean(name,list,0));
        }else if(i == datalist.size() && !fromUser.equals(MainActivity.username)){
            List<Chat_LogBean>list = new ArrayList<Chat_LogBean>(); //临时聊天记录集合
            list.add(new Chat_LogBean(fromUser,toUser,text,image,date,voicePath));
            datalist.add(new List_chat_logBean(name,list,1));
        }
        else if(i != datalist.size() && fromUser.equals(MainActivity.username)){   //我发送的消息
            datalist.get(i).getList().add(new Chat_LogBean(fromUser,toUser,text,image,date,voicePath));
            //updataFriendItem(i); //更新好友位置
        }else if(i != datalist.size() && !fromUser.equals(MainActivity.username)){  //我接受的消息
            datalist.get(i).getList().add(new Chat_LogBean(fromUser,toUser,text,image,date,voicePath));
            datalist.get(i).setUnread(datalist.get(i).getUnread() + 1); //增加未读消息数量
            //updataFriendItem(i); //更新好友位置
        }
        updataFriendItem(i); //更新好友位置

        if(UserChatActivity.UserChat_State == 1) {  //更新聊天界面列表
            fragment_UserChat.updateAdapter();
        }else{
            updateAdapter();
        }
    }

    /**
     * 删除聊天记录
     */
    public void removeDatalist(String friendName){
        int i=0;
        for(i = 0;i<datalist.size();i++){
            if(datalist.get(i).getUsername().equals(friendName)){
                break;
            }
        }
        if(i != datalist.size()){
            datalist.remove(i);
        }
    }

    /**
     * 根据用户名获取聊天记录
     */
    public List<Chat_LogBean> getDatalist(String friendName){
        int i;
        List<Chat_LogBean>list = new ArrayList<Chat_LogBean>();
        for(i=0;i<datalist.size();i++){
            if(datalist.get(i).getUsername().equals(friendName)){
                break;
            }
        }
        if(i != datalist.size()){
            list = datalist.get(i).getList();
        }
        return list;
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
                        .add("username",MainActivity.username)
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
                                userlist.add(new AddressBean(username,bitmap));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }.start();
    }

    /**
     * 更新好友的位置
     */
    private void updataFriendItem(int i){
        List<Chat_LogBean> list = datalist.get(i).getList();
        String FriendName = datalist.get(i).getUsername();
        int unread = datalist.get(i).getUnread();
        datalist.remove(i);
        datalist.add(0,new List_chat_logBean(FriendName,list,unread));
    }

    /**
     * 更新用户聊天列表
     */
    public static void updateAdapter(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                recycleViewAdapter_chat.notifyDataSetChanged();
            }
        });
    }

     public static void getBadgeView(View view, Context context, int i){
        BadgeView badgeView = (BadgeView) view.getTag();
         if(badgeView == null){
             badgeView = new BadgeView(context);
             badgeView.setTargetView(view);
             view.setTag(badgeView);
         }
         badgeView.setBadgeCount(i);
         badgeView.setTextSize(15);
    }

    public void hideBadgeView(View view){
        BadgeView badgeView = (BadgeView) view.getTag();
        if(badgeView != null){
            badgeView.setBadgeCount(0);
        }
    }

    public void initView(View view){
        searchView = (SearchView) view.findViewById(R.id.SearchView_chat);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView_chat);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        fragment_chat_layout = (LinearLayout) view.findViewById(R.id.fragment_chat_layout);
        fragment_chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });
    }

}
