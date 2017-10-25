package com.example.ljh.wechat;

import android.content.Context;
import android.media.AudioRecord;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ljh on 2017/10/20.
 */

public class DialogManager {
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private LayoutInflater layoutInflater;
    private Context context;
    private ImageView ivMic,ivVoiceLevel;
    private TextView tvTip;

    DialogManager(Context context){
        this.context = context;
    }

    public void showRecordingDialog(){
            builder = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.item_dialog,null);

            ivMic = (ImageView) view.findViewById(R.id.ivMic);
            ivVoiceLevel = (ImageView) view.findViewById(R.id.ivVoiceLevel);
            tvTip = (TextView) view.findViewById(R.id.tvTip);

            builder.setView(view);
            builder.create();
            alertDialog = builder.show();
    }

    /**
     * 恢复原始状态
     */
    public void stateNormal(){
        if(alertDialog != null){
            ivMic.setVisibility(View.VISIBLE);
            ivVoiceLevel.setVisibility(View.VISIBLE);
            ivMic.setImageResource(R.drawable.recorder);
            ivVoiceLevel.setImageResource(R.drawable.v1);
            tvTip.setText("手指上滑,取消发送");
        }
    }

    /**
     * 手指上滑
     */
    public void wantCancel(){
        if(alertDialog != null){
            tvTip.setText("松开手指,取消发送");
            ivMic.setVisibility(View.INVISIBLE);
            ivVoiceLevel.setImageResource(R.drawable.cancel);
        }
    }

    /**
     * 说话时间过短
     */
    public void voiceToShort(){
        if(alertDialog != null){
            ivMic.setVisibility(View.INVISIBLE);
            ivVoiceLevel.setImageResource(R.drawable.voice_to_short);
            tvTip.setText("说话时间过短");
        }
    }

    /**
     * 隐藏dialog
     */
    public void dismissDialog(){
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    /**
     * 更新音量大小
     */
    public void updateVoiceLevel(int level){
        int resId = context.getResources().getIdentifier("v"+level,"drawable",context.getPackageName());
        ivVoiceLevel.setImageResource(resId);
    }
}
