package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/9/19.
 */

public class RecycleViewAdapter_chat extends RecyclerView.Adapter<RecycleViewAdapter_chat.ViewHolder>{
    private List<Chat_LogBean>list;
    private List<List_chat_logBean>datalist;
    private List<AddressBean>userlist;

    private LayoutInflater layoutInflater;
    private static Context context;

    private RecycleViewItemClickListener listener = null;

    FragmentAddress fragmentAddress;
    static FragmentChat fragmentChat;

    RecycleViewAdapter_chat(Context context, List<List_chat_logBean> datalist,List<AddressBean> userlist){
        this.datalist = datalist;
        this.layoutInflater = LayoutInflater.from(context);
        this.userlist = userlist;
        this.context = context;
        list = new ArrayList<Chat_LogBean>();
        fragmentAddress = new FragmentAddress();
        fragmentChat = new FragmentChat();
    }

    public interface RecycleViewItemClickListener{
        void onItemClick(View view,int position);
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_fragmentchat2,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        list = datalist.get(position).getList();    //获取聊天记录
        String friendName= datalist.get(position).getUsername();
        int len = list.size();
        holder.friendName.setText(datalist.get(position).getUsername());
        holder.friendImage.setImageBitmap(fragmentAddress.getFriendHead(friendName));
        FragmentChat.getBadgeView(holder.friendImage,context,datalist.get(position).getUnread());

            /**
             * 判断最后一条记录是否为图片,如果是则显示“图片”
             */
            if(list.size() != 0){
                byte head[] = list.get(len - 1).getImage();
                String path = list.get(len - 1).getVoicePath();
                if (head != null) {
                    holder.chatLog.setText("图片");
                }else if(path != null){
                    holder.chatLog.setText("语音消息");
                }
                else {
                    holder.chatLog.setText(list.get(len - 1).getText());
                }
            }

            /**
             * 设置监听
             */
            if (listener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        listener.onItemClick(holder.itemView, position);
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView friendImage;
        TextView friendName;
        TextView chatLog;
        //MHorizontalScrollView horizontalScrollView;
        //Button btDelete;


        public ViewHolder(View itemView) {
            super(itemView);
            friendImage = (ImageView) itemView.findViewById(R.id.ivHead);
            friendName = (TextView) itemView.findViewById(R.id.tvUserName);
            chatLog = (TextView) itemView.findViewById(R.id.tvContent);
           // btDelete = (Button) itemView.findViewById(R.id.btDelete);
            //horizontalScrollView = new MHorizontalScrollView(context);
//            horizontalScrollView = (MHorizontalScrollView) itemView.findViewById(R.id.horizontalScrollView);
        }
    }
}
