package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
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

    private boolean FriendIsStart = false; //是否正在播放朋友的语音
    private boolean MyIsStart = false;//是否正在播放自己的语音
    private int pos = 0;
    private int count = 1;
    private final int MYPLAYING = 1; //正在播放我的语音
    private final int FRIENDPLAYING = 2;//正在播放朋友的语音消息
    private final int UNPLAY = 3; //没有播放语音

    private Message message;
    private Handler handler;

    FragmentMy fragmentMy;

    RecycleViewAdapter_UserChat(Context context, List<Chat_LogBean>datalist){
        this.layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        fragmentMy = new FragmentMy();
        this.context = context;
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
        int time = 0; //语音消息时长
        String fromUser = datalist.get(position).getFromUser(); //谁发的消息
        String text = datalist.get(position).getText(); //消息内容
        String date = datalist.get(position).getDate(); //发送时间
        final byte head[] = datalist.get(position).getImage();  //发送的图片流
        final String voicePath = datalist.get(position).getVoicePath();//获取语音路径
        if (voicePath != null) {
            time = getVoiceTime(context.getApplicationContext().getFilesDir().getPath() + "/" + voicePath);
            Log.i("tag", "-------------VoiceTime=" + time);
        }
        holder.tvDate.setText(date);
        /**
         * 我发送的消息
         */
        if (fromUser.equals(MainActivity.username)) { //我发给好友的消息
            /**
             * 隐藏所有控件
             */
            holder.tvMyText.setVisibility(View.GONE);
            holder.ivMyImage.setVisibility(View.GONE);
            holder.ivVoiceRight.setVisibility(View.GONE);
            holder.tvVoiceRight.setVisibility(View.GONE);

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

            if (text != null) {
                holder.tvMyText.setText(text);
                holder.tvMyText.setVisibility(View.VISIBLE);
                setOnClick(holder.tvMyText,position);   //设置监听
            } else if (head != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(head, 0, head.length);
                holder.ivMyImage.setImageBitmap(bitmap);
                holder.ivMyImage.setVisibility(View.VISIBLE);
                setOnClick(holder.ivMyImage,position);
                holder.ivMyImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,PreviewActivity.class);
                        intent.putExtra("bitmap",head);
                        context.startActivity(intent);
                    }
                });
            } else if (voicePath != null) {
                holder.tvVoiceRight.setVisibility(View.VISIBLE);
                holder.tvVoiceRight.setText(time + "s");
                holder.ivVoiceRight.setVisibility(View.VISIBLE);
                setOnClick(holder.ivVoiceRight,position);
                holder.ivVoiceRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        if (FriendIsStart == false && MyIsStart == false) {
                            MyIsStart = true;   //正在播放标志
                            pos = position;
                            playMyVoice(voicePath, holder.ivVoiceRight);    //开始播放
                        } else if (MyIsStart && FriendIsStart == false && pos == position) {
                            MyIsStart = false;
                            MediaPlayManager.pause(holder.ivVoiceRight,handler);    //暂停播放
                        } else if (MyIsStart && FriendIsStart == false && pos != position) {
                            MediaPlayManager.pause(holder.ivVoiceRight,handler);
                            playMyVoice(voicePath, holder.ivVoiceRight);
                        } else if (MyIsStart == false && FriendIsStart) {
                            MyIsStart = true;
                            FriendIsStart = false;
                            MediaPlayManager.pause(holder.ivVoiceRight,handler);    //暂停播放
                            playMyVoice(voicePath, holder.ivVoiceRight);    //开始播放
                        }

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (MyIsStart) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int resId = context.getResources().getIdentifier("v_anim" + count, "drawable",
                                                        context.getPackageName());
                                                count++;
                                                if (count == 4) {
                                                    count = 1;
                                                }
                                                holder.ivVoiceRight.setImageResource(resId);    //播放动画
                                            }
                                        });
                                        Thread.sleep(300);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                    }
                });
            }

        } else if (!fromUser.equals(MainActivity.username)) {  //好友发送的消息
            holder.ivFriendImage.setVisibility(View.GONE);
            holder.tvFriendText.setVisibility(View.GONE);
            holder.ivVoiceLeft.setVisibility(View.GONE);
            holder.tvVoiceLeft.setVisibility(View.GONE);

            holder.tvMyText.setVisibility(View.GONE);
            holder.ivMyImage.setVisibility(View.GONE);
            holder.ivMyHead.setVisibility(View.GONE);
            holder.ivVoiceRight.setVisibility(View.GONE);
            holder.tvVoiceRight.setVisibility(View.GONE);

            friendHead = FragmentAddress.getFriendHead(fromUser);
            holder.ivFriendHead.setImageBitmap(friendHead);
            holder.ivFriendHead.setVisibility(View.VISIBLE);

            if (text != null) {
                holder.tvFriendText.setText(text);
                holder.tvFriendText.setVisibility(View.VISIBLE);
                setOnClick(holder.tvFriendText,position);
            } else if (head != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(head, 0, head.length);
                holder.ivFriendImage.setImageBitmap(bitmap);
                holder.ivFriendImage.setVisibility(View.VISIBLE);
                setOnClick(holder.ivFriendImage,position);
                holder.ivFriendImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,PreviewActivity.class);
                        intent.putExtra("bitmap",head);
                        context.startActivity(intent);
                    }
                });
            } else if (voicePath != null) {
                holder.tvVoiceLeft.setVisibility(View.VISIBLE);
                holder.tvVoiceLeft.setText(time + "s");
                holder.ivVoiceLeft.setVisibility(View.VISIBLE);
                setOnClick(holder.ivVoiceLeft,position);
                holder.ivVoiceLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        if (FriendIsStart == false && MyIsStart == false) {
                            FriendIsStart = true;
                            playFriendVoice(voicePath, holder.ivVoiceLeft);
                        } else if (FriendIsStart && MyIsStart == false && pos == position) {
                            FriendIsStart = false;
                            MediaPlayManager.pause(holder.ivVoiceLeft,handler);
                        } else if (FriendIsStart && MyIsStart == false && pos != position) {
                            MediaPlayManager.pause(holder.ivVoiceLeft,handler);
                            playFriendVoice(voicePath, holder.ivVoiceLeft);
                        } else if (FriendIsStart == false && MyIsStart) {
                            FriendIsStart = true;
                            MyIsStart = false;
                            MediaPlayManager.pause(holder.ivVoiceLeft,handler);
                            playFriendVoice(voicePath, holder.ivVoiceLeft);
                        }

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (FriendIsStart) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int resId = context.getResources().getIdentifier("v_anim" + count, "drawable",
                                                        context.getPackageName());
                                                count++;
                                                if (count == 4) {
                                                    count = 1;
                                                }
                                                holder.ivVoiceLeft.setImageResource(resId);
                                            }
                                        });
                                        Thread.sleep(500);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
            }
        }

        /*if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    listener.onItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    listener.onLongItemClick(holder.itemView,position);
                    return false;
                }
            });
        }*/

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    /**
     * 给文本消息、图片、或者语音消息设置监听
     * @param view
     * @param pos
     */
    public void setOnClick(View view,final int pos){
        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = pos;
                    listener.onItemClick(view, position);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = pos;
                    listener.onLongItemClick(v,position);
                    return false;
                }
            });
        }
    }

   /* private Handler handler = new Handler(context.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int resId = context.getResources().getIdentifier("v_anim" + count,"drawable",context.getPackageName());
            count++;
            if(count == 4){
                count = 1;
            }

            if(message.arg1 == MYPLAYING){

                }else if(message.arg1 == FRIENDPLAYING){

                }else {

                }

        }
    };*/

    public void playMyVoice(String voicePath, final ImageView imageView){
        MediaPlayManager.play
                (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        MyIsStart = false;
                        count = 1;
                        imageView.setImageResource(R.drawable.voice);
                    }
                });
    }

    public void playFriendVoice(String voicePath, final ImageView imageView){
        MediaPlayManager.play
                (context.getApplicationContext().getFilesDir().getPath()+ "/" + voicePath,new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        FriendIsStart = false;
                        count = 1;
                        imageView.setImageResource(R.drawable.voice);
                    }
                });
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
