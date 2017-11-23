package com.example.ljh.wechat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.HorizontalListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/10/30.
 */

public class WriterActivity extends AppCompatActivity implements View.OnClickListener {
    private static HorizontalListView horizontalListView;
    private AppCompatSpinner appCompatSpinner;
    private EditText etTitle,etContent; //标题、内容
    private RelativeLayout layout_back; //返回
    private TextView tvSendPost;    //发送
    private ProgressDialog progressDialog;
    private FloatingActionsMenu fab_munu;   //悬浮菜单
    private FloatingActionButton fab_carmera,fab_album; //悬浮按钮

    private String typeString = "all";  //默认语言类型，无分类
    private String type[] = {"all","android","java","javascript","php","python","unity","other"};//菜单导航栏

    private List<String> BitmapStringList;
    private static List<Bitmap> BitmapList;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOICE_PHOTO = 2;

    private Uri uri;

    ListViewAdapter_addImage adapter;
    CompressImageManager compressImageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer);
        getSupportActionBar().hide();

        BitmapList = new ArrayList<Bitmap>();
        BitmapStringList = new ArrayList<String>();
        compressImageManager = new CompressImageManager();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:  //返回
                WriterActivity.this.finish();
                break;
            case R.id.tvSendPost:   //发帖

                String title = etTitle.getText()+"";
                String content = etContent.getText()+"";
                if(title == null || title == ""){
                    Toast.makeText(this,"标题不能为空!",Toast.LENGTH_SHORT).show();
                }else if(content == null || content == ""){
                    Toast.makeText(this,"内从不能为空!",Toast.LENGTH_SHORT).show();
                }else{
                tvSendPost.setOnClickListener(null);
                showDialog();
                SendPost(title,content);
                }
                break;
            case R.id.fab_camera:   //打开相机
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
                }else if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                }
                else{
                    openCamera();
                }
                break;
            case R.id.fab_album:    //打开相册
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                }else{
                    openAlbum();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(this, R.string.permissionDenied , Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this, R.string.permissionDenied , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void SendPost(final String title,final String content){
        new Thread(){
            @Override
            public void run() {
                try {
                    Map<String,JSONArray> map = new HashMap<String,JSONArray>();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    if(BitmapList.size() >= 1) {
                        for (int i = 0; i < BitmapList.size(); i++) {
                            Bitmap bitmap = BitmapList.get(i);
                            byte byte1[] = BitmapToByte(bitmap);
                            String bitmapString = Base64.encodeToString(byte1, Base64.DEFAULT);
                            jsonObject = new JSONObject();
                            jsonObject.put("username",MainActivity.username);
                            jsonObject.put("type",typeString);
                            jsonObject.put("title",title);
                            jsonObject.put("content",content);
                            jsonObject.put("bitmapString", bitmapString);
                            jsonArray.put(jsonObject);
                        }
                    }else{
                        jsonObject.put("username",MainActivity.username);
                        jsonObject.put("type",typeString);
                        jsonObject.put("title",title);
                        jsonObject.put("content",content);
                        jsonObject.put("bitmapString","");
                        jsonArray.put(jsonObject);
                    }

                    map.put("list",jsonArray);
                    Log.i("aaa",jsonArray+"");
                    JSONObject jsonObject1 = new JSONObject(map);

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody requestBody1 = RequestBody.create(JSON,jsonObject1+"");

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .writeTimeout(5000, TimeUnit.SECONDS)
                            .readTimeout(5000,TimeUnit.SECONDS)
                            .connectTimeout(5000,TimeUnit.SECONDS)
                            .build();
            /*RequestBody requestBody = new FormBody.Builder()
                    .add("type","sendPost")
                    .add("username",MainActivity.username)
                    .add("typeString",typeString)
                    .add("title",title)
                    .add("content",content)
                    .add("list",jsonObject+"")
                    .build();*/
                    Request request = new Request.Builder()
                            .url(MainActivity.SetPostServlet)
                            .post(requestBody1)
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
                    tvSendPost.setOnClickListener(WriterActivity.this);
                    break;
                case "SendTrue":
                    dismissDialog();
                    WriterActivity.this.finish();
                    System.gc();
                    FragmentShare fragmentShare = new FragmentShare();
                    fragmentShare.getData();
                    break;
                case "TakePhotoTrue":
                    horizontalListView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                case "TakePhotoFalse":
                    Toast.makeText(WriterActivity.this,"图片可发送数量已达上限",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 打开相册
     */
    public void openAlbum(){
        ThemeConfig themeConfig = ThemeConfig.DARK;
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnablePreview(true)
                .setMutiSelectMaxSize(3)
                .setEnableRotate(true)
                .build();
        ImageLoader imageLoader = new ImageLoade();
        CoreConfig coreConfig = new CoreConfig.Builder(this,imageLoader,themeConfig)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
        GalleryFinal.openGalleryMuti(CHOICE_PHOTO,functionConfig,handlerResultCallback);

    }
    GalleryFinal.OnHandlerResultCallback handlerResultCallback = new GalleryFinal.OnHandlerResultCallback() {
        @Override
        public void onHandlerSuccess(int requestCode, List<PhotoInfo> resultList) {
            for (int i = 0; i < resultList.size(); i++) {
                if (BitmapList.size() == 3) {
                    Toast.makeText(WriterActivity.this, "图片可发送数量已达上限", Toast.LENGTH_SHORT).show();
                    break;
                }
                Bitmap bitmap = compressImageManager.CompressToAlbum(resultList.get(i).getPhotoPath());
                BitmapList.add(bitmap);
            }
            //showImage();
            horizontalListView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onHandlerFailure(int requestCode, String errorMsg) {
                Toast.makeText(WriterActivity.this,"古古滑滑觉得呢张图片唔得",Toast.LENGTH_SHORT).show();
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
            if(BitmapList.size() < 3){ //最多只能发3张图片
                Bitmap bitmap = compressImageManager.CompressToCamera(WriterActivity.this,uri);
                BitmapList.add(bitmap);         //保存到图片集合

                message.obj = "TakePhotoTrue";
                message.sendToTarget();
            }else {
                message.obj = "TakePhotoFalse";
                message.sendToTarget();
            }
        }
    }

    public Bitmap CompressToAlbum(String path){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    /**
     * 压缩图片
     */
    public Bitmap CompressToCamera(){
        BitmapFactory.Options options = new BitmapFactory.Options();//压缩图片
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri),null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

   public static void hideListView(){
       horizontalListView.setVisibility(View.GONE);
   }

    public void initView(){
        appCompatSpinner = (AppCompatSpinner) findViewById(R.id.spinner_type);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        tvSendPost = (TextView) findViewById(R.id.tvSendPost);
        horizontalListView = (HorizontalListView) findViewById(R.id.ListView_addImage);
        adapter = new ListViewAdapter_addImage(WriterActivity.this,BitmapList);
        horizontalListView.setAdapter(adapter);
        //layout_image = (RelativeLayout) findViewById(R.id.layout_image);
        layout_back = (RelativeLayout) findViewById(R.id.layout_back);
        fab_munu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fab_carmera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_album = (FloatingActionButton) findViewById(R.id.fab_album);


        layout_back.setOnClickListener(this);
        tvSendPost.setOnClickListener(this);
        fab_carmera.setOnClickListener(this);
        fab_album.setOnClickListener(this);
        /*ivDelete1.setOnClickListener(this);
        ivDelete2.setOnClickListener(this);
        ivDelete3.setOnClickListener(this);*/
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
        AccCompatSpinnerClick();
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
     * 监听AppCompatSpinner
     */
    public void AccCompatSpinnerClick(){
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
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismissDialog(){
        progressDialog.dismiss();
    }

}
