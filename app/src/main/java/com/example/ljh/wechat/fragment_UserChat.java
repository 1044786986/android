package com.example.ljh.wechat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by ljh on 2017/10/4.
 */

public class fragment_UserChat extends Fragment implements View.OnClickListener{
    private static RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView ivVoice,ivSendImage,ivMessage;  //语音按键，显示语音状态，发送图片,用户信息
    private ImageView ivAlbum,ivCamera; //打开相册,打开相机
    private EditText etMessage; //输入文字
    private Button btVoicing;
    private TextView tvUsername,tvCancel; //当前聊天用户
    private static TextView tvBack; //返回键
    private LinearLayout linearLayout_UserChat,linearLayout_SendImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    private Uri uri;
    private static String toUser;  //好友名字
    private int Voice_State = 0; //非语音模式状态
    private static int n,m;
    private float startY; //记录手指落下的Y轴位置
    private static float time = 0f; //录音时间
    private boolean isVocing = false; //判断是否正在录音

    private static List<Chat_LogBean> datalist;    //历史聊天记录
    private static List<Chat_LogBean> datalist2;

    private SQLiteDatabase sqLiteDatabase;
    private static Handler handler = new Handler(Looper.getMainLooper());

    static final int Take_Photo = 2;
    static final int STATE_NORMAL = 1; //普通状态
    static boolean STATE_RECORDING = false;   //正在录音
    static final int STATE_WANT_CANCEL = 3; //取消录音
    static String path;
    /*private Socket socket;
    private OutputStream outputStream;
    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;*/

    static FragmentChat fragmentChat;
    MediaRecorderManager mediaRecorderManager;
    DialogManager dialogManager;
    ImageLoader imageLoader;
    static RecycleViewAdapter_UserChat recycleViewAdapter_userChat;

    AudioFinishRecorderListener audioFinishRecorderListener;

    public fragment_UserChat(String toUser){
        this.toUser = toUser;
        //path = Environment.getExternalStorageDirectory().getPath();
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userchat,null);
        path = getActivity().getApplication().getFilesDir().getPath();
        fragmentChat = new FragmentChat();
        mediaRecorderManager = new MediaRecorderManager(getActivity());
        dialogManager = new DialogManager(getActivity());
        datalist = fragmentChat.getDatalist(toUser);
        datalist2 = new ArrayList<Chat_LogBean>();
        n = datalist.size()-20;
        m = datalist.size();
        setDatalist();
        initView(view);
        voicing();
        //initSocket();
        //getPermission();
        sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);
        return view;
    }

    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener audioFinishRecorderListener){
        this.audioFinishRecorderListener = audioFinishRecorderListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvBack:
                getActivity().finish();
                sqLiteDatabase.close();
                break;
            /**
             * 切换输入和语音模式
             */
            case R.id.ivVoice:
                if(Voice_State == 0){
                    Voice_State = 1;
                    ivVoice.setImageResource(R.drawable.voice);
                    btVoicing.setVisibility(View.VISIBLE);
                    etMessage.setVisibility(View.GONE);
                    hideInputMethod(view);  //隐藏键盘
                }else{
                    Voice_State = 0;
                    ivVoice.setImageResource(R.drawable.search_ic_mic_black_24dp);
                    btVoicing.setVisibility(View.GONE);
                    etMessage.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.ivSendImage:
                hideInputMethod(view);
                linearLayout_SendImage.setVisibility(View.VISIBLE);
                break;
            case R.id.ivAlbum:
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
                break;
            case R.id.ivCamera:
                if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},2);
                }else{
                    openCamera();
                }
                break;
            case R.id.ivMessage:
                getPermission();
                Toast.makeText(getActivity(),"aa",Toast.LENGTH_SHORT).show();
                break;
            case R.id.linearLayout_UserChat:
                hideInputMethod(view);
                break;
        }
    }

    public void voicing(){
        btVoicing.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                    /*dialogManager.showRecordingDialog(); //显示对话框
                    isVocing = true;                    //正在录音状态
                    //mediaRecorderManager.prepare();     //开启录音*/
                    //getVoiceLevel();                    //获取音量大小
                return false;
            }
        });

        btVoicing.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btVoicing.setText("按住说话");
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 3);
                }
                else {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mediaRecorderManager.prepare();
                            dialogManager.showRecordingDialog(); //显示对话框
                            isVocing = true;                    //正在录音状态
                            STATE_RECORDING = true;
                            getVoiceLevel();                    //获取音量大小
                            startY = motionEvent.getY();
                            btVoicing.setText("松开结束");
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if ((-(motionEvent.getY()) - startY) > 30) {
                                dialogManager.wantCancel();
                                STATE_RECORDING = false;
                            } else {
                                dialogManager.stateNormal();
                                STATE_RECORDING = true;
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            if (STATE_RECORDING == false && isVocing) {
                                Log.i("tag","*--------------取消发送");
                                //取消发送
                                mediaRecorderManager.cancel();
                                dialogManager.dismissDialog();
                            }
                              else if(time <= 1f){
                                time = 0f;
                                mediaRecorderManager.cancel();
                                dialogManager.voiceToShort();
                                setBtVoicingOfNull();   //取消监听器
                            }
                            else if(STATE_RECORDING && isVocing){
                                Log.i("tag","---------成功录音");
                                mediaRecorderManager.release();
                                if(audioFinishRecorderListener != null){
                                    audioFinishRecorderListener.onFinish(time,MediaRecorderManager.CurrentFilePath);
                                }
                                sendVoiceMessage(MediaRecorderManager.fileName);
                                dialogManager.dismissDialog();
                                //insertIntoSqlite(MainActivity.username,toUser,null,null,getDate(),);
                            }
                            isVocing = false;
                            STATE_RECORDING = false;
                            time = 0f;
                            //dialogManager.dismissDialog();  //隐藏对话框
                            break;
                        }
                    }
                    return false;
                }
        });

    }

    public void getVoiceLevel(){
        new Thread(){
            @Override
            public void run() {
                try {
                    while (isVocing){
                        Log.i("tag","-------------time=" + time);
                        Thread.sleep(100);
                        final int level = mediaRecorderManager.getLevel(7);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(STATE_RECORDING){
                                    time = time + 0.1f;
                                    dialogManager.updateVoiceLevel(level);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void setBtVoicingOfNull(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    /*btVoicing.setOnTouchListener(null);
                    btVoicing.setOnLongClickListener(null);*/
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //voicing();
                            dialogManager.dismissDialog();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 我发送给朋友的记录
     */
    public void insertIntoSqlite(String fromUser,String toUser,String message,byte image[],String date,String fileName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("fromUser",fromUser);
        contentValues.put("toUser",toUser);
        contentValues.put("text",message);
        contentValues.put("image",image);
        contentValues.put("date",date);
        contentValues.put("voicePath",fileName);
        sqLiteDatabase.insert("chat_log",null,contentValues);

    }


    /**
     * recycleview分页
     */
    public void setDatalist(){
        if(m >= 20){
            for(int i = m-1; i > n;i--){
                datalist2.add(0,datalist.get(i));
            }
            m = m - 20;
            n = n - 20;
        }else{
            datalist2 = datalist;
        }
    }

    /**
     * 打开相册
     */
    public void openAlbum(){
        /*ThemeConfig themeConfig = new ThemeConfig.Builder()
                .setTitleBarBgColor(R.color.TitleBarBgColor)
                .setTitleBarTextColor(R.color.TitleBarTextColor)
                .build();    //配置主题*/
        ThemeConfig themeConfig = ThemeConfig.DARK;
        FunctionConfig functionConfig = new FunctionConfig.Builder()    //配置功能
                .setEnablePreview(true) //启用预览
                .setMutiSelectMaxSize(9)  //配置最大数量
                .setEnableRotate(true)  //开启选择功能
                .build();
        ImageLoader imageLoader = new ImageLoade(); //配置ImageLoder

        CoreConfig coreConfig = new CoreConfig.Builder(getActivity(),imageLoader,themeConfig)   //设置核心配置信息
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
        GalleryFinal.openGalleryMuti(1,functionConfig,handlerResultCallback);
        //imageLoader.clearMemoryCache();
    }

    GalleryFinal.OnHandlerResultCallback handlerResultCallback = new GalleryFinal.OnHandlerResultCallback() {
        @Override
        public void onHandlerSuccess(int requestCode, List<PhotoInfo> resultList) {
                sendImageMessage(resultList);
        }

        @Override
        public void onHandlerFailure(int requestCode, String errorMsg) {

        }
    };

    public byte[]image(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     *打开相机
     */
    public void openCamera(){
        try {
            File file = new File(getActivity().getExternalCacheDir(),"image.jpg");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();

            if(Build.VERSION.SDK_INT >= 23){
                uri = FileProvider.getUriForFile(getActivity(),"com.example.ljh.wechat.fileprovider",file);
            }else{
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            startActivityForResult(intent,Take_Photo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(getActivity(), "you denide thi permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                bitmap = CompressImage(bitmap);
                sendCameraImageMessage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnClick(){
        recycleViewAdapter_userChat.setRecycleViewItemClickListener(
                new RecycleViewAdapter_UserChat.RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view1, int position) {
                hideInputMethod(view);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        });

        mediaRecorderManager.setMediaRecorderManagerListener(new MediaRecorderManager.MediaRecorderManagerListener() {
            @Override
            public void wellPrepared() {

            }
        });
    }

    /**
     * 隐藏键盘
     */
    public void hideInputMethod(View view){
        InputMethodManager inputMethodManager
                = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

        if(linearLayout_SendImage.isShown()){
            linearLayout_SendImage.setVisibility(View.GONE);
        }
    }

    /**
     * 发送文字消息给好友
     */
    public void SendTextMessage(final String message){
        new Thread(){
            @Override
            public void run() {
                try {
                    try {
                        Socket socket = new Socket("192.168.191.1",8888);
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","text");
                        jsonObject.put("message",message);
                        jsonObject.put("toUser",toUser);
                        jsonObject.put("fromUser",MainActivity.username);
                        String date = getDate();
                        jsonObject.put("date",date);
                        printWriter.print(jsonObject);
                        printWriter.flush();
                        printWriter.close();
                        outputStream.close();
                        outputStreamWriter.close();
                        socket.close();
                        insertIntoSqlite(MainActivity.username,toUser,message,null,date,null);//保存聊天记录到sqlite
                        fragmentChat.addDatalist(MainActivity.username,toUser,message,null,date,null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     *发送图片消息给好友
     */
    public void sendImageMessage(final List<PhotoInfo> resultList){
        new Thread(){
            @Override
            public void run() {
                try {
                    for(int i=0;i < resultList.size();i++){
                        Socket socket = new Socket("192.168.191.1",8888);
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);

                        String photoInfo = resultList.get(i).getPhotoPath();
                        Bitmap bitmap = CompressImage(BitmapFactory.decodeFile(photoInfo));//获取并压缩图片
                        byte image[] = image(bitmap);
                        String imageString = Base64.encodeToString(image,Base64.DEFAULT);
                        //list.add(new SendImageMessageBean(imageString));
                        insertIntoSqlite(MainActivity.username,toUser,null,image,getDate(),null);
                        fragmentChat.addDatalist(MainActivity.username,toUser,null,image,getDate(),null);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","sendImageMessage");
                        jsonObject.put("fromUser",MainActivity.username);
                        jsonObject.put("toUser",toUser);
                        jsonObject.put("image",imageString);
                        printWriter.println(jsonObject);
                        printWriter.flush();
                        printWriter.close();
                        outputStream.close();
                        outputStreamWriter.close();
                        socket.close();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void sendCameraImageMessage(final Bitmap bitmap){
        new Thread(){
            @Override
            public void run() {
                try {
                        Socket socket = new Socket("192.168.191.1",8888);
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                        byte image[] = image(bitmap);
                        String imageString = Base64.encodeToString(image,Base64.DEFAULT);
                        insertIntoSqlite(MainActivity.username,toUser,null,image,getDate(),null);
                        fragmentChat.addDatalist(MainActivity.username,toUser,null,image,getDate(),null);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","sendImageMessage");
                        jsonObject.put("fromUser",MainActivity.username);
                        jsonObject.put("toUser",toUser);
                        jsonObject.put("image",imageString);
                        printWriter.println(jsonObject);
                        printWriter.flush();
                        printWriter.close();
                        outputStream.close();
                        outputStreamWriter.close();
                        socket.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送语音消息
     */
    public void sendVoiceMessage(final String fileName){
        new Thread(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("192.168.191.1",8888);
                    OutputStream outputStream = socket.getOutputStream();
                    //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                    PrintWriter printWriter = new PrintWriter(outputStream);

                    File file = new File(getActivity().getApplication().getFilesDir().getPath() + "/" + fileName);
                    FileInputStream fileInputStream = new FileInputStream(file);

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    byte byte1[] = new byte[2048];
                    int len = 0;
                    while ((len = dataInputStream.read(byte1)) != -1){
                            byteArrayOutputStream.write(byte1,0,len);
                    }
                    byte voiceByte[] = byteArrayOutputStream.toByteArray();
                    String voiceString = Base64.encodeToString(voiceByte,0,voiceByte.length,Base64.DEFAULT);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type","sendVoiceMessage");
                    jsonObject.put("fromUser",MainActivity.username);
                    jsonObject.put("toUser",toUser);
                    jsonObject.put("voice",voiceString);
                    printWriter.println(jsonObject);
                    printWriter.flush();
                    printWriter.close();
                    outputStream.close();
                    //outputStreamWriter.close();
                    socket.close();

                    insertIntoSqlite(MainActivity.username,toUser,null,null,getDate(),fileName);
                    fragmentChat.addDatalist(MainActivity.username,toUser,null,null,getDate(),fileName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 对图片进行压缩
     */
    public Bitmap CompressImage(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f,0.5f);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return bitmap;
    }

    /**
     * 获取日期
     */
    public String getDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    static void updateAdapter(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.ChatCount != 0){
                    tvBack.setText("返回" + "(" + MainActivity.ChatCount + ")");
                }else{
                    tvBack.setText("返回");
                }
                if(datalist == null || datalist.size() == 0){
                    datalist = fragmentChat.getDatalist(toUser);
                }
                recycleViewAdapter_userChat.notifyDataSetChanged();
                recyclerView.scrollToPosition(recycleViewAdapter_userChat.getItemCount()-1);//定位到底部
            }
        });
    }


    public void initSocket(){
        /*new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.191.1",8888);
                    outputStream = socket.getOutputStream();
                    outputStreamWriter = new OutputStreamWriter(outputStream);
                    printWriter = new PrintWriter(outputStreamWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*try {
            outputStream.close();
            outputStreamWriter.close();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void getPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions
                    (getActivity(),new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},1);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    public void initView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recycleViewAdapter_userChat = new RecycleViewAdapter_UserChat(getActivity(),datalist);
        recyclerView.setAdapter(recycleViewAdapter_userChat);
        recyclerView.scrollToPosition(recycleViewAdapter_userChat.getItemCount()-1);
        setOnClick();

        ivVoice = (ImageView) view.findViewById(R.id.ivVoice);
        btVoicing = (Button) view.findViewById(R.id.btVoicing);
        ivSendImage = (ImageView) view.findViewById(R.id.ivSendImage);
        ivMessage = (ImageView) view.findViewById(R.id.ivMessage);
        ivAlbum = (ImageView) view.findViewById(R.id.ivAlbum);
        ivCamera = (ImageView) view.findViewById(R.id.ivCamera);

        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvBack = (TextView) view.findViewById(R.id.tvBack);
        tvUsername = (TextView) view.findViewById(R.id.tvUserName);
        tvUsername.setText(toUser);

        ivVoice.setOnClickListener(this);
        btVoicing.setOnClickListener(this);
        ivSendImage.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        ivAlbum.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        tvBack.setOnClickListener(this);

        linearLayout_SendImage = (LinearLayout) view.findViewById(R.id.linearLayout_SendImage);
        linearLayout_UserChat = (LinearLayout) view.findViewById(R.id.linearLayout_UserChat);
        linearLayout_UserChat.setOnClickListener(this);

        etMessage = (EditText) view.findViewById(R.id.etMessage);
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEND){
                    String message = etMessage.getText()+"";
                    etMessage.setText("");
                    SendTextMessage(message);
                    return true;
                }
                return false;
            }
        });
        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout_SendImage.isShown()){
                    linearLayout_SendImage.setVisibility(View.GONE);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDatalist();
                recycleViewAdapter_userChat.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
