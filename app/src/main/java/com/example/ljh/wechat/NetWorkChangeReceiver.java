package com.example.ljh.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by ljh on 2017/11/27.
 */

public class NetWorkChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()){

        }else{
            Toast.makeText(context,"当前网络连接不可用",Toast.LENGTH_LONG).show();
        }
    }

    /*public void onNetWorkChangeReceiver(Context context,Intent intent){

    }*/
}
