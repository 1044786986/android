package com.example.ljh.wechat;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ljh on 2017/10/18.
 */

public class MediaRecorderManager {
    private static MediaRecorder mediaRecorder;
    //private static String path = "/data/data/com.example.ljh.wechat";
    private static String path = "";
    public static String CurrentFilePath;
    private boolean isStart = false;
    private static MediaRecorderManager mInstance = null;
    private static int level = 1;
    static String fileName;
    private Context context;

    MediaRecorderManagerListener mediaRecorderManagerListener = null;

    MediaRecorderManager(){}

     MediaRecorderManager(Context context){
        //path = context.getApplicationContext().getFilesDir().getPath();
        path = fragment_UserChat.path;
         this.context = context;
    }

    public MediaRecorderManager getInstance(){
        if(mInstance == null){
            synchronized(MediaRecorderManager.class){
                if(mInstance == null){
                    mInstance = new MediaRecorderManager(context);
                }
            }
        }
        return mInstance;
    }

    public interface MediaRecorderManagerListener{
        void wellPrepared();
    }

    public void setMediaRecorderManagerListener(MediaRecorderManagerListener mediaRecorderManagerListener){
        this.mediaRecorderManagerListener = mediaRecorderManagerListener;
    }

    /**
     * 准备录音
     */
    public void prepare(){

            try {
                fileName = getDate();
                //File file = new File(path + "/" + fileName);
                File file = new File(path);
                if(file.isDirectory()){
                    file.mkdir();
                }
                if(!file.exists()) {
                    file.createNewFile();
                }
                File file1 = new File(file,fileName);
                CurrentFilePath =  file1.getAbsolutePath();
                mediaRecorder = new MediaRecorder();

                mediaRecorder.setOutputFile(CurrentFilePath);//设置输出文件
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//设置音频格式
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
                mediaRecorder.setMaxDuration(120000);
                mediaRecorder.prepare();//准备录音
                mediaRecorder.start(); //开始录音
                isStart = true;

            } catch (IOException e) {
                e.printStackTrace();
        }
    }

    /***
     *获取音量等级
     */
    public int getLevel(int maxlevel){

        if (isStart) {
            try {
                return maxlevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    /**
     *释放资源
     */
    public void release(){
        Log.i("tag","-----------release");
        if(mediaRecorder != null){
            //mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder = null;
            //CurrentFilePath = null;
        }
    }

    /**
     * 取消录音
     */
    public void cancel(){
        Log.i("tag","------------CurrentFilePath=" + CurrentFilePath);
        File file = new File(CurrentFilePath);
        if(file.exists()){
            file.delete();
        }
        release();
    }

    public String getDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = simpleDateFormat.format(date) + ".amr";
        return dateString;
    }


}
