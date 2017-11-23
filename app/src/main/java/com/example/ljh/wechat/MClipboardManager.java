package com.example.ljh.wechat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by ljh on 2017/11/15.
 */

public class MClipboardManager{
    private ClipboardManager clipboardManager;
    private ClipData clipData;

    MClipboardManager(Context context){
        clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    }

    public void copyContent(String content){
        clipData = ClipData.newPlainText("text",content);
        clipboardManager.setPrimaryClip(clipData);
    }

    public String getContent(){
        String content = null;
        clipData = clipboardManager.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        content = item.getText()+"";
        return content;
    }

    public boolean isContent(){
        boolean flag = false;
        clipData = clipboardManager.getPrimaryClip();
        if(clipData != null){
            flag = true;
        }
        return flag;
    }
}
