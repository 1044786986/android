package com.example.ljh.wechat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ljh on 2017/10/27.
 */

public class NotificationManager1 {
    static NotificationManager notificationManager;
    static NotificationCompat.Builder builder;
    static List<Integer> IdList = new ArrayList<Integer>();

    public static void sendNotification(String fromUser, String content, Context context,int id){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context.getPackageName(),"com.example.ljh.wechat.MainActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//设置启动模式
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        //PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_chat_bubble_outline_blue_24dp);  //小图标
        builder.setContentTitle(fromUser);                                  //标题
        builder.setContentText(content);                                    //内容
        builder.setContentIntent(pendingIntent);                            //意图
        builder.setAutoCancel(true);                                        //当用户单击面板就可以让通知将自动取消
        builder.setDeleteIntent(pendingIntent);
        builder.setDefaults(Notification.FLAG_SHOW_LIGHTS);                   //呼吸灯
        builder.setPriority(Notification.PRIORITY_HIGH);                    //优先级
        /**
         * 开启声音提醒后
         */
        if(MessageRemindActivity.voice){
            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sound));
        }
        /**
         * 开启震动提醒后
         */
        if(MessageRemindActivity.shock){
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        notificationManager.notify(id,builder.build());
        IdList.add(id);
    }

}
