package com.example.ljh.wechat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    private static RecyclerView recyclerView,recyclerView_ppw;
    private LinearLayoutManager linearLayoutManager;
    private ImageView ivVoice,ivSendImage,ivMessage;  //语音按键，显示语音状态，发送图片,用户信息
    private ImageView ivAlbum,ivCamera; //打开相册,打开相机
    private EditText etMessage; //输入文字
    private Button btVoicing;
    private TextView tvUsername,tvCancel; //当前聊天用户
    private static TextView tvBack; //返回键
    private LinearLayout linearLayout_UserChat,linearLayout_SendImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow_etMessage;
    private View view;
    private View popupWindowView;

    private int _id = 0;
    private Uri uri;
    private static String toUser;  //好友名字
    private int Voice_State = 0; //非语音模式状态
    private float startY; //记录手指落下的Y轴位置
    private static float time = 0f; //录音时间
    private boolean isVocing = false; //判断是否正在录音

    static List<Chat_LogBean> datalist;    //历史聊天记录
    private List<String> ppwList;  //PopuWindow数据源
    private final String etMessagePPw[] = {"全部复制","选择","粘贴"};

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
    MediaRecorderManager mediaRecorderManager;  //播放音乐管理
    DialogManager dialogManager;   //弹窗管理
    ImageLoader imageLoader;    //图片加载
    static RecycleViewAdapter_UserChat recycleViewAdapter_userChat; //聊天记录适配器
    RecycleView_userchat_ppw recycleViewadapter_ppw;    //长按操作选择适配器
    CompressImageManager compressImageManager;  //压缩管理
    MClipboardManager mClipboardManager; //复制粘贴管理

    public fragment_UserChat(String toUser){
        this.toUser = toUser;
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userchat,null);
        path = getActivity().getApplication().getFilesDir().getPath();

        fragmentChat = new FragmentChat();
        mClipboardManager = new MClipboardManager(getActivity());
        mediaRecorderManager = new MediaRecorderManager(getActivity());
        dialogManager = new DialogManager(getActivity());
        compressImageManager = new CompressImageManager();
        sqLiteDatabase = getActivity().openOrCreateDatabase("ljh.db",0,null);

        datalist = new ArrayList<Chat_LogBean>();
        ppwList = new ArrayList<String>();

        addppwList();
        initView(view);
        initPopupWindow();
        voicing();

        getChatLog();   //获取聊天记录
        if(datalist.size() != 0 && datalist != null) {
            _id = datalist.get(0).getId();  //当前好友第一条聊天记录的id
            int a = _id;
        }
        //initSocket();
        //getPermission();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvBack:
                getActivity().finish();
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
                UserChatActivity.userChatActivity.showFragment(UserChatActivity.USERINFO);
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

                                sendVoiceMessage(MediaRecorderManager.fileName);
                                dialogManager.dismissDialog();
                            }
                            isVocing = false;
                            STATE_RECORDING = false;
                            time = 0f;
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
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
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
                    Toast.makeText(getActivity(), R.string.permissionDenied , Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(getActivity(), R.string.permissionDenied , Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
                Bitmap bitmap = compressImageManager.CompressToCamera(getActivity(),uri);
                sendCameraImageMessage(bitmap);
        }
    }

    /**
     * 每一条信息设置监听
     */
    public void setOnClick(){
        recycleViewAdapter_userChat.setRecycleViewItemClickListener(
                new RecycleViewAdapter_UserChat.RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view1, int position) {
                hideInputMethod(view);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.i("aa","onLongItemClick");
                showPopupWindow(view,position);
            }
        });

        mediaRecorderManager.setMediaRecorderManagerListener(new MediaRecorderManager.MediaRecorderManagerListener() {
            @Override
            public void wellPrepared() {

            }
        });
    }

    /**
     *显示popupWindow
     * view 长按的view
     * positon 聊天记录在datalist的位置
     */
    public void showPopupWindow(View view, final int position){
        popupWindow.showAsDropDown(view,100,-100);
        recycleViewadapter_ppw.setOnItemClickListener(new RecycleView_userchat_ppw.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int pos) {
                switch (pos){
                    case 0:             //转发
                        break;
                    case 1:             //复制
                        mClipboardManager.copyContent(datalist.get(position).getText());
                        break;
                    case 2:             //更多
                        break;
                    case 3:             //删除
                        deleteChatLog(position);
                        fragmentChat.deleteChatLog(toUser,position);
                        recycleViewAdapter_userChat.notifyDataSetChanged();
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    /**
     *
     */
    public void showPPwEtMessage(){

    }

    /**
     * 初始化popupWindow
     */
    public void initPopupWindow(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        popupWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.popuwindow_userchat,null);
        recyclerView_ppw = (RecyclerView) popupWindowView.findViewById(R.id.ListView_ppw_userchat);
        recycleViewadapter_ppw = new RecycleView_userchat_ppw(getActivity(),ppwList);
        recyclerView_ppw.setLayoutManager(linearLayoutManager);
        recyclerView_ppw.setAdapter(recycleViewadapter_ppw);

        popupWindow = new PopupWindow(popupWindowView, 400,
                500,true);
        popupWindow.setContentView(popupWindowView);
    }


    /**
     * 发送文字消息给好友
     */
    public void SendTextMessage(final String message) {
        if (message == null || message == "") {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "不能发送空白消息", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            insertIntoSqlite(MainActivity.username, toUser, message, null, getDate(), null);//保存聊天记录到sqlite
                            fragmentChat.addDatalist(MainActivity.username, toUser, message, null, getDate(), null);

                            Socket socket = new Socket(MainActivity.Ip, 8888);
                            OutputStream outputStream = socket.getOutputStream();
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "text");
                            jsonObject.put("message", message);
                            jsonObject.put("toUser", toUser);
                            jsonObject.put("fromUser", MainActivity.username);
                            printWriter.print(jsonObject);
                            printWriter.flush();
                            printWriter.close();
                            outputStream.close();
                            outputStreamWriter.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     *发送图片消息给好友
     */
    public void sendImageMessage(final List<PhotoInfo> resultList) {
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < resultList.size(); i++) {
                        String photoInfo = resultList.get(i).getPhotoPath();
                        Bitmap bitmap = compressImageManager.CompressToAlbum(photoInfo); //获取并压缩图片
                        byte image[] = image(bitmap);
                        String imageString = Base64.encodeToString(image, Base64.DEFAULT);

                        insertIntoSqlite(MainActivity.username, toUser, null, image, getDate(), null);
                        fragmentChat.addDatalist(MainActivity.username, toUser, null, image, getDate(), null);

                        Socket socket = new Socket(MainActivity.Ip, 8888);
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type", "sendImageMessage");
                        jsonObject.put("fromUser", MainActivity.username);
                        jsonObject.put("toUser", toUser);
                        jsonObject.put("image", imageString);
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
                        byte image[] = image(bitmap);
                        String imageString = Base64.encodeToString(image,Base64.DEFAULT);
                        insertIntoSqlite(MainActivity.username,toUser,null,image,getDate(),null);
                        fragmentChat.addDatalist(MainActivity.username,toUser,null,image,getDate(),null);

                        Socket socket = new Socket(MainActivity.Ip,8888);
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);
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
                    insertIntoSqlite(MainActivity.username,toUser,null,null,getDate(),fileName);
                    fragmentChat.addDatalist(MainActivity.username,toUser,null,null,getDate(),fileName);

                    Socket socket = new Socket(MainActivity.Ip,8888);
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
     * 加载聊天记录
     */
    public void getChatLog() {
        /**
         * 我发给好友的记录
         */
        List<Chat_LogBean> LogList = new ArrayList<Chat_LogBean>();
        Cursor cursor1 =
                sqLiteDatabase.query("chat_log", new String[]{"_id", "fromUser", "toUser", "text", "image", "date", "voicePath"},
                        "fromUser=? and toUser=?", new String[]{MainActivity.username, toUser}, null, null, "date DESC", "7");
        while (cursor1.moveToNext()) {
            int _id = cursor1.getInt(cursor1.getColumnIndex("_id"));
            String fromUser = cursor1.getString(cursor1.getColumnIndex("fromUser"));
            String toUser = cursor1.getString(cursor1.getColumnIndex("toUser"));
            String text = cursor1.getString(cursor1.getColumnIndex("text"));
            byte image[] = cursor1.getBlob(cursor1.getColumnIndex("image"));
            String date = cursor1.getString(cursor1.getColumnIndex("date"));
            String voicePath = cursor1.getString(cursor1.getColumnIndex("voicePath"));
            LogList.add(new Chat_LogBean(_id, fromUser, toUser, text, image, date, voicePath));
        }
        /**
         * 好友发给我的记录
         */
        Cursor cursor2 =
                sqLiteDatabase.query("chat_log", new String[]{"_id", "fromUser", "toUser", "text", "image", "date", "voicePath"},
                        "fromUser=? and toUser=?", new String[]{toUser, MainActivity.username}, null, null, "date DESC", "7");

        while (cursor2.moveToNext()) {
            int _id = cursor2.getInt(cursor2.getColumnIndex("_id"));
            String fromUser = cursor2.getString(cursor2.getColumnIndex("fromUser"));
            String toUser = cursor2.getString(cursor2.getColumnIndex("toUser"));
            String text = cursor2.getString(cursor2.getColumnIndex("text"));
            byte image[] = cursor2.getBlob(cursor2.getColumnIndex("image"));
            String date = cursor2.getString(cursor2.getColumnIndex("date"));
            String voicePath = cursor2.getString(cursor2.getColumnIndex("voicePath"));
            LogList.add(new Chat_LogBean(_id, fromUser, toUser, text, image, date, voicePath));
        }

        if (LogList.size() != 0) {
            Collections.sort(LogList, new ChatLogCompartor());  //按日期进行排序
            for (int i = LogList.size()-1; i >= 0; i--) {
                datalist.add(0, LogList.get(i));
            }
            LogList.clear();
            updateAdapter();

        }
    }

    /**
     * 下滑加载聊天记录
     */
    public void loadChatLog(){
        /**
         * 我发给好友的记录
         */
        List<Chat_LogBean>LogList = new ArrayList<Chat_LogBean>();
        Cursor cursor1 =
                sqLiteDatabase.query("chat_log",new String[]{"_id","fromUser","toUser","text","image","date","voicePath"},
                        "_id<? and fromUser=? and toUser=?",new String[]{_id+"",MainActivity.username,toUser},null,null,"date DESC","10");
        while (cursor1.moveToNext()) {
            int _id = cursor1.getInt(cursor1.getColumnIndex("_id"));
            String fromUser = cursor1.getString(cursor1.getColumnIndex("fromUser"));
            String toUser = cursor1.getString(cursor1.getColumnIndex("toUser"));
            String text = cursor1.getString(cursor1.getColumnIndex("text"));
            byte image[] = cursor1.getBlob(cursor1.getColumnIndex("image"));
            String date = cursor1.getString(cursor1.getColumnIndex("date"));
            String voicePath = cursor1.getString(cursor1.getColumnIndex("voicePath"));
            LogList.add(new Chat_LogBean(_id,fromUser, toUser, text,image, date,voicePath));
        }
        /**
         * 好友发给我的记录
         */
        Cursor cursor2 =
                sqLiteDatabase.query("chat_log",new String[]{"_id","fromUser","toUser","text","image","date","voicePath"},
                        "_id<? and fromUser=? and toUser=?",new String[]{_id+"",toUser,MainActivity.username},null,null,"date DESC","10");

        while (cursor2.moveToNext()) {
            int _id = cursor2.getInt(cursor2.getColumnIndex("_id"));
            String fromUser = cursor2.getString(cursor2.getColumnIndex("fromUser"));
            String toUser = cursor2.getString(cursor2.getColumnIndex("toUser"));
            String text = cursor2.getString(cursor2.getColumnIndex("text"));
            byte image[] = cursor2.getBlob(cursor2.getColumnIndex("image"));
            String date = cursor2.getString(cursor2.getColumnIndex("date"));
            String voicePath = cursor2.getString(cursor2.getColumnIndex("voicePath"));
            LogList.add(new Chat_LogBean(_id,fromUser, toUser, text,image, date,voicePath));
        }

        if(LogList.size() != 0){
            Collections.sort(LogList, new ChatLogCompartor());  //按日期进行排序
            for(int i = LogList.size()-1;i >= 0;i--){
                datalist.add(0,LogList.get(i));
            }
            _id = datalist.get(0).getId();      //重置第一条记录的id
            LogList.clear();
            recycleViewAdapter_userChat.notifyDataSetChanged();
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"已经没有更多啦",Toast.LENGTH_SHORT).show();
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 删除一条聊天记录
     */
    public void deleteChatLog(int position){
        String fromUser = datalist.get(position).getFromUser();
        String toUser = datalist.get(position).getToUser();
        String date = datalist.get(position).getDate();
        sqLiteDatabase.delete("chat_log","fromUser=? and toUser=? and date=?",new String[]{fromUser,toUser,date});
        datalist.remove(position);
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

    /**
     * PopuWindow增加数据
     */
    public void addppwList(){
        ppwList.clear();
        ppwList.add("复制");
        ppwList.add("转发");
        ppwList.add("更多");
        ppwList.add("删除");
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

    static void updateAdapter(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.ChatCount != 0){
                    tvBack.setText("返回" + "(" + MainActivity.ChatCount + ")");
                }else{
                    tvBack.setText("返回");
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
        if(sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }
        datalist.clear();
        /*try {
            outputStream.close();
            outputStreamWriter.close();
            printWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        etMessage.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etMessage.setSingleLine(false);
        etMessage.setHorizontallyScrolling(false);
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
        etMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //popupWindow
                return false;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChatLog();
            }
        });
    }


}
