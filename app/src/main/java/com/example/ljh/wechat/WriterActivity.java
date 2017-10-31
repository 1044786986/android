package com.example.ljh.wechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/10/30.
 */

public class WriterActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatSpinner appCompatSpinner;
    private EditText etTitle,etContent;
    private RelativeLayout layout_back;
    private LinearLayout layout_addImage;
    private TextView tvSendPost;
    private ProgressDialog progressDialog;

    private String typeString = "default";
    private String type[] = {"default","android","java","javascript","php","python","unity","other"};

    private List<String> BitmapStringList = new ArrayList<String>();
    private List<Bitmap> BitmapList = new ArrayList<Bitmap>();

    private static final int TAKE_PHOTO = 1;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        getSupportActionBar().hide();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:
                WriterActivity.this.finish();
                break;
            case R.id.tvSendPost:
                showDialog();
                SendPost();
                break;
        }
    }

    public void SendPost(){
        String title = etTitle.getText()+"";
        String content = etContent.getText()+"";
        if(title == null || title == ""){
            Toast.makeText(this,"标题不能为空!",Toast.LENGTH_SHORT).show();
        }else if(content == null || content == ""){
            Toast.makeText(this,"内从不能为空!",Toast.LENGTH_SHORT).show();
        }else{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .writeTimeout(5000, TimeUnit.SECONDS)
                    .readTimeout(5000,TimeUnit.SECONDS)
                    .connectTimeout(5000,TimeUnit.SECONDS)
                    .build();
            RequestBody requestBody = new FormBody.Builder()
                    .add("type","sendPost")
                    .add("username",MainActivity.username)
                    .add("typeString",typeString)
                    .add("title",title)
                    .add("content",content)
                    .add("list","")
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.SearchFriendsServlet)
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = handler.obtainMessage();
                    message.obj = "SendFalse";
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message message = handler.obtainMessage();
                    message.obj = "SendTrue";
                    message.sendToTarget();
                }
            });
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.obj+"";
            switch (result){
                case "SendFalse":
                    Toast.makeText(WriterActivity.this,"网络连接出现问题,发送失败",Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    break;
                case "SendTrue":
                    dismissDialog();
                    WriterActivity.this.finish();
                    break;
                case "TakePhotoTrue":
                    break;
                case "TakePhotoFalse":
                    Toast.makeText(WriterActivity.this,"图片可发送数量已达上限",Toast.LENGTH_SHORT).show();
                    break;
            }
           /* if(result.equals("SendFalse")){
                Toast.makeText(WriterActivity.this,"网络连接出现问题,发送失败",Toast.LENGTH_SHORT).show();
                dismissDialog();
            }else if(result.equals("SendTrue")){
                dismissDialog();
                WriterActivity.this.finish();
            }
            else if(result.equals("TakePhotoTrue")){

            }else if(result.equals("TakePhotoFalse")){
                Toast.makeText(WriterActivity.this,"图片可发送数量已达上限",Toast.LENGTH_SHORT).show();
            }*/
        }
    };

    /**
     * 打开相机
     */
    public void openCamera(){
        try {

            File file = new File(this.getExternalCacheDir(),"image.png");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            if(Build.VERSION.SDK_INT >= 23){
                uri = FileProvider.getUriForFile(this,"com.example.ljh.wechat.fileprovider",file);
            }else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            startActivityForResult(intent,TAKE_PHOTO);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == this.RESULT_OK){
            Message message  = handler.obtainMessage();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));//生成Bitmap
                if(BitmapList.size() <= 3 && BitmapStringList.size() <= 3){ //最多只能发3张图片
                    bitmap = CompressImageManager.CompressImage(bitmap);    //压缩图片
                    BitmapList.add(bitmap);         //保存到图片集合

                    byte byte1[] = BitmapToByte(bitmap);    //bitmap转byte类型
                    String bitmapString = Base64.encodeToString(byte1,Base64.DEFAULT);  //byte转为String类型
                    BitmapStringList.add(bitmapString);
                    message.obj = "TakePhotoTrue";
                    message.sendToTarget();
                }else {
                    message.obj = "TakePhotoFalse";
                    message.sendToTarget();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void initView(){
        appCompatSpinner = (AppCompatSpinner) findViewById(R.id.spinner_type);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        tvSendPost = (TextView) findViewById(R.id.tvSendPost);
        layout_back = (RelativeLayout) findViewById(R.id.layout_back);
        layout_addImage = (LinearLayout) findViewById(R.id.layout_addImage);
        layout_back.setOnClickListener(this);
        tvSendPost.setOnClickListener(this);
        /**
         * 监听文本长度
         */
        etTitleClick();
        etConentClick();

        /**
         * AppCompatSpinner设置适配器和监听
         */
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,type);
        appCompatSpinner.setAdapter(arrayAdapter);
        appCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeString = type[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 监听标题长度
     */
    public void etTitleClick(){
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etTitle.getText().length() == 30){
                    Toast.makeText(WriterActivity.this,"标题长度不能超过30个字符",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    /**
     * 监听内容长度
     */
    public void etConentClick(){
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etContent.getText().length() == 500){
                    Toast.makeText(WriterActivity.this,"内容长度不能超过500个字符",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     *将图片转为byte
     */
    public byte[] BitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismissDialog(){
        progressDialog.dismiss();
    }

}
