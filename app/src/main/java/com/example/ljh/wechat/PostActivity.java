package com.example.ljh.wechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/11/11.
 */

public class PostActivity extends PermissionActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private RecyclerView recyclerView_image;
    private LinearLayoutManager linearLayoutManager,linearLayoutManager2;
    private TextView tvContent,tvUsername,tvDate,tvThumbUp,tvThumbDown,tvDelete;//头部的textView
    private TextView tvBack;
    private ImageView ivHead,ivThumbUp,ivThumbDown;                    //头部的Imageview
    private ImageView ivAlbum,ivCamera,ivAddImage,ivDelete;  //相册、相机、添加图片的布局按钮
    private ImageView imageView;                    //显示要发送的图片
    private EditText etMessage;                     //编辑发送的文字
    private Button btSend;                          //发送按钮
    private LinearLayout layout_addImage;           //相册和相机的布局
    private ProgressDialog progressDialog;

    private Uri uri;
    private int comment_id = 0;
    private final int TAKE_PHOTO = 0;
    private final int CHOICE_PHOTO = 1;
    private Bitmap bitmap = null;   //要发送的图片

    List<PostBean> datalist;
    List<String> bitmapList;

    HeaderAdapter headerAdapter;    //头部适配器
    ShareBean shareBean;            //帖子对象
    PreViewManager preViewManager;  //类型转换管理
    CompressImageManager compressImageManager;//压缩管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        getSupportActionBar().hide();
        datalist = new ArrayList<PostBean>();
        bitmapList = new ArrayList<String>();
        preViewManager = new PreViewManager();
        compressImageManager = new CompressImageManager();
        getShareBean();
        initView();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivThumbUp:
                Thumbs("nice");
                break;
            case R.id.ivThumbDown:
                Thumbs("noNice");
                break;
            case R.id.tvDelete:
                break;
            case R.id.tvBack:
                finish();
                break;
            case R.id.btSend:
                String text = etMessage.getText()+"";
                replyModerator(text,preViewManager.BitmapToString(bitmap));
                break;
            case R.id.etMessage:
                hideCameraAndAlbum();
                break;
            case R.id.ivAlbum:
                openAlbumPermission();
                break;
            case R.id.ivCamera:
                openCameraPermission();
                break;
            case R.id.ivDelete:
                bitmap = null;
                imageView.setImageBitmap(null);
                hideImageAndDelete();
                showCameraAndAlbum();
                break;
            case R.id.ivAddImage:
                hideInputMethod(v);
                if(bitmap == null){         //只能发送一张图片
                    showCameraAndAlbum();   //当没有图片的时候才显示可选择的相册
                }
                break;
        }
    }

    /**
     * 获取该帖子的对象
     */
    public void getShareBean(){
        Intent intent = getIntent();
        shareBean = (ShareBean) intent.getSerializableExtra("shareBean");
        bitmapList = shareBean.getList();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.obj+"";
            //获取数据出错
            if(result.equals("getDataFalse")){
                Toast.makeText(PostActivity.this,"网络连接错误", Toast.LENGTH_SHORT).show();
            }
            //获取到数据
            else if(result.equals("getDataTrue")){
                headerAdapter.addData(datalist);
            }
            //点赞失败
            else if(result.equals("ThumbsFalse")){
                Toast.makeText(PostActivity.this,"你已经评价过该帖子啦",Toast.LENGTH_SHORT).show();
            }
            //更新赞的数量
            else if(result.equals("nice")){     //点赞
                int i = Integer.parseInt(shareBean.getNice()) + 1;
                tvThumbUp.setText(i+"");
            }
            //更新踩的数量
            else if(result.equals("noNice")){   //踩
                int i = Integer.parseInt(shareBean.getNonice()) + 1;
                tvThumbUp.setText(i+"");
            }

            else if(result.equals("getPhotoTrue")){
                hideCameraAndAlbum();
                imageView.setImageBitmap(bitmap);
                showImageAndDelete();
            }
            //回复成功
            else if(result.equals("replyModeratorTrue")){
                hideDialog();
                etMessage.setText("");
                if(bitmap != null){
                    hideImageAndDelete();
                    imageView.setImageBitmap(null);
                    bitmap = null;
                }
                Toast.makeText(PostActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                datalist.clear();
                comment_id = 0;
                getData();
            }
            else if(result.equals("showDialog")){
                showDialog();
            }
        }
    };

    /**
     * 回复楼主
     */
    public void replyModerator(final String string, final String imageString){
        Message message = handler.obtainMessage();
        message.obj = "showDialog";
        message.sendToTarget();

        ExecutorService executorService = ThreadManager.startThread();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .writeTimeout(3000,TimeUnit.SECONDS)
                        .readTimeout(3000,TimeUnit.SECONDS)
                        .connectTimeout(3000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("type","replyModerator")
                        .add("username",MainActivity.username)
                        .add("p_id",shareBean.getId())
                        .add("text",string)
                        .add("imageString",imageString)
                        .build();
                Request request = new Request.Builder()
                        .post(requestBody)
                        .url(MainActivity.GetPostServlet)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = handler.obtainMessage();
                        message.obj = "getDataFalse";
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("aa","回复成功");
                        Message message = handler.obtainMessage();
                        message.obj = "replyModeratorTrue";
                        message.sendToTarget();
                    }
                });
            }
        });
    }
    /**
     * 获取帖子的详细内容
     */
    public void getData(){
        ExecutorService executorService = ThreadManager.startThread();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .writeTimeout(3000, TimeUnit.SECONDS)
                        .readTimeout(3000,TimeUnit.SECONDS)
                        .connectTimeout(3000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("id",shareBean.getId()+"")
                        .add("comment_id",comment_id+"")
                        .add("type","getPostContent")
                        .build();
                Request request = new Request.Builder()
                        .url(MainActivity.GetPostServlet)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = handler.obtainMessage();
                        message.obj = "getDataFalse";
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        if(!result.equals("{\"data\":[]}")) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    String comment_id = jsonObject.getString("comment_id");
                                    String username = jsonObject.getString("username");
                                    String headString = jsonObject.getString("headString");
                                    String content = jsonObject.getString("content");
                                    String imageString = jsonObject.getString("imageString");
                                    String date = jsonObject.getString("date");
                                    datalist.add(new PostBean(comment_id, username, headString, content, imageString, date));
                                }
                                comment_id = Integer.parseInt(datalist.get(datalist.size() - 1).getComment_id()); //分页最后一条评论的id
                                Message message = handler.obtainMessage();
                                message.obj = "getDataTrue";
                                message.sendToTarget();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 点赞功能
     */
    public void Thumbs(final String isNice){
        ExecutorService executorService = ThreadManager.startThread();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .writeTimeout(3000,TimeUnit.SECONDS)
                        .readTimeout(3000,TimeUnit.SECONDS)
                        .connectTimeout(3000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("type","Thumbs")
                        .add("username",MainActivity.username)
                        .add("p_id",shareBean.getId())
                        .add("isNice",isNice)
                        .build();
                final Request request = new Request.Builder()
                        .post(requestBody)
                        .url(MainActivity.GetPostServlet)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = handler.obtainMessage();
                        message.obj = "getDataFalse";
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            result = jsonObject.getString("result");
                            Message message = handler.obtainMessage();
                            if(result.equals("true")){          //评价成功
                                message.obj = isNice;
                            }else if(result.equals("false")){   //评价失败
                                message.obj = "ThumbsFalse";
                            }
                            message.sendToTarget();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 打开相机
     */
    public void openCamera(){
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),getFileName());
            file.createNewFile();
            if(Build.VERSION.SDK_INT >= 23){
                uri = FileProvider.getUriForFile(this,"com.example.ljh.wechat.fileprovider",file);
            }else{
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
        if(resultCode == RESULT_OK){
            bitmap = null;
            bitmap = compressImageManager.CompressToCamera(this,uri);
            Message message = handler.obtainMessage();
            message.obj = "getPhotoTrue";
            message.sendToTarget();
        }
    }

    /**
     * 打开相册
     */
    public void openAlbum(){
        ThemeConfig themeConfig = ThemeConfig.DARK;
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnablePreview(true)
                .setMutiSelectMaxSize(1)
                .setEnableRotate(true)
                .setEnableCamera(true)
                .build();
        ImageLoader imageLoader = new ImageLoade();
        CoreConfig coreConfig = new CoreConfig.Builder(this,imageLoader,themeConfig)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
        GalleryFinal.openGallerySingle(CHOICE_PHOTO,functionConfig,onHandlerResultCallback);
    }

    GalleryFinal.OnHandlerResultCallback onHandlerResultCallback = new GalleryFinal.OnHandlerResultCallback() {
        @Override
        public void onHandlerSuccess(int requestCode, List<PhotoInfo> resultList) {
            bitmap = null;
            bitmap = compressImageManager.CompressToAlbum(resultList.get(0).getPhotoPath());
            Message message = handler.obtainMessage();
            message.obj = "getPhotoTrue";
            message.sendToTarget();
        }

        @Override
        public void onHandlerFailure(int requestCode, String errorMsg) {

        }
    };

    public String getFileName(){
        return UUID.randomUUID().toString()+ ".png";
    }

    /**
     * 判断当前用户是否有权限删除该帖子
     */
    public void isDeletePermission(View view){
        if(shareBean.getUsername().equals(MainActivity.username)){
            tvDelete = (TextView) view.findViewById(R.id.tvDelete);
            tvDelete.setText("删除");
            tvDelete.setOnClickListener(this);
            tvDelete.setVisibility(View.VISIBLE);
        }
    }

    public void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * 显示图片和删除按钮
     */
    public void showImageAndDelete(){
        imageView.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏图片和删除按钮
     */
    public void hideImageAndDelete(){
        imageView.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
    }

    /**
     * 显示相机和相册
     */
    public void showCameraAndAlbum() {
        ivAlbum.setVisibility(View.VISIBLE);
        ivCamera.setVisibility(View.VISIBLE);
    }
    /**
     * 隐藏相机和相册
     */
    public void hideCameraAndAlbum(){
        ivAlbum.setVisibility(View.GONE);
        ivCamera.setVisibility(View.GONE);

    }

    /**
     * 隐藏键盘
     */
    public void hideInputMethod(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    /**
     *  获取日期
     */
    public String getDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

    public void initView(){
        View headView = LayoutInflater.from(this).inflate(R.layout.headview_community,null);
        tvContent = (TextView) headView.findViewById(R.id.tvContent);
        tvUsername = (TextView) headView.findViewById(R.id.tvUserName);
        tvDate = (TextView) headView.findViewById(R.id.tvDate);
        tvThumbUp = (TextView) headView.findViewById(R.id.tvThumbUp);
        tvThumbDown = (TextView) headView.findViewById(R.id.tvThumbDown);
        ivHead = (ImageView) headView.findViewById(R.id.ivHead);
        ivThumbUp = (ImageView) headView.findViewById(R.id.ivThumbUp);
        ivThumbDown = (ImageView) headView.findViewById(R.id.ivThumbDown);
        tvContent.setText(shareBean.getContent());
        tvUsername.setText(shareBean.getUsername());
        tvDate.setText(shareBean.getDate());
        tvThumbDown.setText(shareBean.getNonice());
        tvThumbUp.setText(shareBean.getNice());
        ivHead.setImageBitmap(preViewManager.StringToBitmap(shareBean.getHeadString()));
        ivThumbUp.setOnClickListener(this);
        ivThumbDown.setOnClickListener(this);
        isDeletePermission(headView);
        recyclerView_image = (RecyclerView) headView.findViewById(R.id.recyclerView_image);
        linearLayoutManager2 = new LinearLayoutManager(this);
        recyclerView_image.setLayoutManager(linearLayoutManager2);
        recyclerView_image.setAdapter(new HeaderViewAdapter());


        linearLayoutManager = new LinearLayoutManager(this);
        headerAdapter = new HeaderAdapter(this);
        headerAdapter.setHeadView(headView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_community);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(headerAdapter);

        ivAlbum = (ImageView) findViewById(R.id.ivAlbum);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        imageView = (ImageView) findViewById(R.id.imageView);
        ivAlbum.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);

        tvBack = (TextView) findViewById(R.id.tvBack);
        btSend = (Button) findViewById(R.id.btSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvBack.setOnClickListener(this);
        btSend.setOnClickListener(this);
        etMessage.setOnClickListener(this);
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    String text = etMessage.getText()+"";
                    if(bitmap != null){
                        replyModerator(text,preViewManager.BitmapToString(bitmap));
                    }else{
                        replyModerator(text,"");
                    }

                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shareBean = null;
        datalist.clear();
        bitmapList.clear();
        preViewManager = null;
        headerAdapter = null;
        comment_id = 0;
        bitmap = null;
        finish();
    }

    class HeaderViewAdapter extends RecyclerView.Adapter<HeaderViewAdapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PostActivity.this).inflate(R.layout.item_imageview,null);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.imageView.setImageBitmap(preViewManager.StringToBitmap(bitmapList.get(position)));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostActivity.this,PreviewActivity.class);
                    intent.putExtra("bitmap",preViewManager.StringToByte(bitmapList.get(position)));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bitmapList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }
    }

}
