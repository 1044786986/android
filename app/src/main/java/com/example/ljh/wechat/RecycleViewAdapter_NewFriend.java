package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by ljh on 2017/9/27.
 */

public class RecycleViewAdapter_NewFriend extends RecyclerView.Adapter<RecycleViewAdapter_NewFriend.ViewHolder>{

    private List<AddFriendBean> datalist;
    private LayoutInflater layoutInflater;
    private Handler handler = new Handler();

    NewFriendActivity newFriendActivity;

    RecycleViewAdapter_NewFriend(Context context,List<AddFriendBean> datalist){
        this.datalist = datalist;
        this.layoutInflater = LayoutInflater.from(context);
        newFriendActivity = new NewFriendActivity();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_newfriend,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvUsername.setText(datalist.get(position).getUsername());
        holder.tvNote.setText(datalist.get(position).getData());
        holder.tvDate.setText(datalist.get(position).getDate());
        holder.ivHead.setImageBitmap(datalist.get(position).getHead());

            holder.btAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SendResult(MainActivity.username,datalist.get(position).getUsername(),"true",holder.btAccept,holder.btRefuse);
                    newFriendActivity.updateSqlite(datalist.get(position).getUsername(),"true");
                }
            });

            holder.btRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SendResult(MainActivity.username,datalist.get(position).getUsername(),"false",holder.btAccept,holder.btRefuse);
                    newFriendActivity.updateSqlite(datalist.get(position).getUsername(),"false");
                }
            });
        /**
         * 判断该好友是否已添加
         */
        String state = datalist.get(position).getUsername();
        if(state.equals("true")){
            Accept(holder);
        }else if(state.equals("false")){
            Refuse(holder);
        }
        }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        TextView tvNote;
        TextView tvDate;
        ImageView ivHead;
        Button btAccept;
        Button btRefuse;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = (TextView) itemView.findViewById(R.id.tvFromName);
            tvNote = (TextView) itemView.findViewById(R.id.tvNote);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            ivHead = (ImageView) itemView.findViewById(R.id.ivHead);
            btAccept = (Button) itemView.findViewById(R.id.btAccept);
            btRefuse = (Button) itemView.findViewById(R.id.btRefuse);
        }
    }

    public void SendResult(final String fromUser,final String toUser,final String answer,final Button btAccept,final Button btRefuse){
        new Thread(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("192.168.56.1",8888);
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream);
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("type","AddFriend_Answer");
                    map.put("fromUser",fromUser);//我的名字
                    map.put("toUser",toUser);   //好友名字
                    if(answer.equals("true")){
                        map.put("answer","true");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btAccept.setOnClickListener(null);
                                btRefuse.setOnClickListener(null);
                                btAccept.setBackgroundColor(Color.rgb(214,215,215));
                                btAccept.setText("已接受");
                                btRefuse.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else {
                        map.put("answer","false");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btAccept.setOnClickListener(null);
                                btRefuse.setOnClickListener(null);
                                btAccept.setBackgroundColor(Color.rgb(214,215,215));
                                btAccept.setText("已拒绝");
                                btRefuse.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    JSONObject jsonObject = new JSONObject(map);
                    printWriter.println(jsonObject);
                    printWriter.flush();
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void Accept(final ViewHolder holder){
        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.btAccept.setOnClickListener(null);
                holder.btRefuse.setOnClickListener(null);
                holder.btAccept.setBackgroundColor(Color.rgb(214,215,215));
                holder.btAccept.setText("已接受");
                holder.btRefuse.setVisibility(View.INVISIBLE);
            }
        });
    }

    void Refuse(final ViewHolder holder){
        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.btAccept.setOnClickListener(null);
                holder.btRefuse.setOnClickListener(null);
                holder.btAccept.setBackgroundColor(Color.rgb(214,215,215));
                holder.btAccept.setText("已拒绝");
                holder.btRefuse.setVisibility(View.INVISIBLE);
            }
        });
    }
}
