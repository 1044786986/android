package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ljh on 2017/10/9.
 */

public class RecycleViewAdapter_UserChat extends RecyclerView.Adapter<RecycleViewAdapter_UserChat.ViewHolder> implements View.OnClickListener{
    private LayoutInflater layoutInflater;
    private List<Chat_LogBean>datalist;
    private RecycleViewItemClickListener listener;
    private Bitmap myHead;
    private Bitmap friendHead;
    private Context context;
    private static String voicePath = null;
    private boolean FriendIsStart = false; //是否正在播放朋友的语音
    private boolean MyIsStart = false;//是否正在播放自己的语音

    FragmentAddress fragmentAddress;
    FragmentMy fragmentMy;
    MediaPlayManager mediaPlayManager;

    RecycleViewAdapter_UserChat(Context context, List<Chat_LogBean>datalist){
        this.layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        fragmentAddress = new FragmentAddress();
        fragmentMy = new FragmentMy();
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           /* case R.id.ivVoice_left:
                if(FriendIsStart == false && MyIsStart == false){
                    MediaPlayManager.play
                            (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                    FriendIsStart = true;
                }else if(FriendIsStart && MyIsStart == false){
                    MediaPlayManager.pause();
                    FriendIsStart = false;
                }else if(FriendIsStart == false && MyIsStart){
                    MediaPlayManager.pause();
                    MediaPlayManager.play
                            (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                    FriendIsStart = true;
                    MyIsStart = false;
                }
                break;
            case R.id.ivVoice_right:
                if(FriendIsStart == false && MyIsStart == false){
                    MediaPlayManager.play
                            (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                    MyIsStart = true;
                }else if(MyIsStart && FriendIsStart == false){
                    MediaPlayManager.release();
                    MyIsStart = false;
                }else if(MyIsStart == false && FriendIsStart){
                    MediaPlayManager.release();
                    MediaPlayManager.play
                            (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                    MyIsStart = true;
                    FriendIsStart = false;
                }
                break;*/
        }
    }

    public interface RecycleViewItemClickListener{
        void onItemClick(View view,int position);
        void onLongItemClick(View view,int position);
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_userchat3,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int time=0; //语音消息时长
        String fromUser = datalist.get(position).getFromUser(); //谁发的消息
        String text = datalist.get(position).getText(); //消息内容
        String date = datalist.get(position).getDate(); //发送时间
        byte head[] = datalist.get(position).getImage();  //发送的图片流
        final String voicePath = datalist.get(position).getVoicePath();//获取语音路径
        if(voicePath != null){
            time = getVoiceTime(context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath);
            Log.i("tag","-------------VoiceTime=" + time);
        }
        /**
         * 我发送的消息
         */
        if(fromUser.equals(MainActivity.username)){ //我发给好友的消息
            /**
             * 隐藏所有控件
             */
            holder.tvMyText.setVisibility(View.INVISIBLE);
            holder.ivMyImage.setVisibility(View.GONE);
            holder.ivVoiceRight.setVisibility(View.GONE);
            holder.tvVoiceRight.setVisibility(View.INVISIBLE);

            holder.tvFriendText.setVisibility(View.GONE);
            holder.ivFriendImage.setVisibility(View.GONE);
            holder.ivFriendHead.setVisibility(View.GONE);
            holder.ivVoiceLeft.setVisibility(View.GONE);
            holder.tvVoiceLeft.setVisibility(View.GONE);
            /**
             * 显示需要的控件
             */
            myHead = fragmentMy.getMyHead();
            holder.ivMyHead.setImageBitmap(myHead);
            holder.ivMyHead.setVisibility(View.VISIBLE);

            if(text != null ){
                holder.tvMyText.setText(text);
                holder.tvMyText.setVisibility(View.VISIBLE);
            }else if(head != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                holder.ivMyImage.setImageBitmap(bitmap);
                holder.ivMyImage.setVisibility(View.VISIBLE);
            }else if(voicePath != null){
                holder.tvVoiceRight.setVisibility(View.VISIBLE);
                holder.tvVoiceRight.setText(time+"s");
                holder.ivVoiceRight.setVisibility(View.VISIBLE);
                holder.ivVoiceRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(FriendIsStart == false && MyIsStart == false){
                            MediaPlayManager.play
                                    (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                            MyIsStart = true;
                        }else if(MyIsStart && FriendIsStart == false){
                            MediaPlayManager.pause();
                            MyIsStart = false;
                        }else if(MyIsStart == false && FriendIsStart){
                            MediaPlayManager.pause();
                            MediaPlayManager.play
                                    (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                            MyIsStart = true;
                            FriendIsStart = false;
                        }
                    }
                });
            }


        }else if(!fromUser.equals(MainActivity.username)){  //好友发送的消息
            holder.ivFriendImage.setVisibility(View.GONE);
            holder.tvFriendText.setVisibility(View.INVISIBLE);
            holder.ivVoiceLeft.setVisibility(View.GONE);
            holder.tvVoiceLeft.setVisibility(View.GONE);

            holder.tvMyText.setVisibility(View.GONE);
            holder.ivMyImage.setVisibility(View.GONE);
            holder.ivMyHead.setVisibility(View.GONE);
            holder.ivVoiceRight.setVisibility(View.GONE);
            holder.tvVoiceRight.setVisibility(View.GONE);

            friendHead = fragmentAddress.getFriendHead(fromUser);
            holder.ivFriendHead.setImageBitmap(friendHead);
            holder.ivFriendHead.setVisibility(View.VISIBLE);

            if(text != null ){
                holder.tvFriendText.setText(text);
                holder.tvFriendText.setVisibility(View.VISIBLE);
            }else if(head != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                holder.ivFriendImage.setImageBitmap(bitmap);
                holder.ivFriendImage.setVisibility(View.VISIBLE);
            }else if(voicePath != null){
                holder.tvVoiceLeft.setVisibility(View.VISIBLE);
                holder.tvVoiceLeft.setText(time+"s");
                holder.ivVoiceLeft.setVisibility(View.VISIBLE);
                holder.ivVoiceLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(FriendIsStart == false && MyIsStart == false){
                            MediaPlayManager.play
                                    (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                            FriendIsStart = true;
                        }else if(FriendIsStart && MyIsStart == false){
                            MediaPlayManager.release();
                            FriendIsStart = false;
                        }else if(FriendIsStart == false && MyIsStart){
                            MediaPlayManager.release();
                            MediaPlayManager.play
                                    (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,context);
                            FriendIsStart = true;
                            MyIsStart = false;
                        }
                    }
                });
            }

        }
        holder.tvDate.setText(date);

        if(listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    listener.onItemClick(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    /**
     *获取语音消息的时长
     */
    public int getVoiceTime(String filePath){
        int time = 0;
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescriptor);
            mediaPlayer.prepare();
            time = (int)mediaPlayer.getDuration() / 1000;
            Log.i("tag","-------------VoiceTime=" + time);
            mediaPlayer.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivMyHead;
        ImageView ivFriendHead;
        ImageView ivMyImage;
        ImageView ivFriendImage;
        ImageView ivVoiceLeft;
        ImageView ivVoiceRight;
        TextView tvVoiceLeft;
        TextView tvVoiceRight;
        TextView tvMyText;
        TextView tvFriendText;
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ivMyHead = (ImageView) itemView.findViewById(R.id.ivMyHead);
            ivFriendHead = (ImageView) itemView.findViewById(R.id.ivFriendHead);
            ivMyImage = (ImageView) itemView.findViewById(R.id.ivMyImage);
            ivFriendImage = (ImageView) itemView.findViewById(R.id.ivFriendImage);
            ivVoiceLeft = (ImageView) itemView.findViewById(R.id.ivVoice_left);
            ivVoiceRight = (ImageView) itemView.findViewById(R.id.ivVoice_right);

            tvMyText = (TextView) itemView.findViewById(R.id.tvMyText);
            tvFriendText = (TextView) itemView.findViewById(R.id.tvFriendText);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvVoiceLeft = (TextView) itemView.findViewById(R.id.tvVoiceLeft);
            tvVoiceRight = (TextView) itemView.findViewById(R.id.tvVoiceRight);
        }
    }
}
