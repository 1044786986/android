package com.example.ljh.wechat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by ljh on 2017/11/22.
 */

public abstract class PermissionActivity extends AppCompatActivity{
    final int OPEN_ALBUM = 0;

    public void openAlbumPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},OPEN_ALBUM);
        }
        else{
            openAlbum();
        }
    }

    public void openCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},OPEN_ALBUM);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},OPEN_ALBUM);
        }else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case OPEN_ALBUM:
                if(grantResults.length <= 0 || grantResults[0] == -1){
                    showTipDialog();
                }
                break;
        }
    }

    public void showTipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setMessage("当前应用缺少必要权限，请单击【确定】按钮前往设置中心进行权限授权")
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                startSetting();
             }
         });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void startSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    protected abstract void openCamera();
    protected abstract void openAlbum();
}
