package com.example.ljh.wechat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/9/25.
 */

public class ModifyHeadActivity extends AppCompatActivity {
    private ImageView ivHead;

    private Intent intent;
    private Uri uri;
    static final int TAKE_PHOTO = 1;
    static final int CHOOSE_PHOTO = 2;

    private SQLiteDatabase sqLiteDatabase;
    private String url = "http://192.168.191.1:8080/wechat/SearchFriendsServlet";

    private FragmentMy fragmentMy;
    private Handler handler = new Handler();
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyhead);
        initView();
        fragmentMy = new FragmentMy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"拍照");
        menu.add(1,2,1,"从手机相册中选择");
        menu.add(1,3,1,"保存图片");

        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(ContextCompat.checkSelfPermission(ModifyHeadActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            ModifyHeadActivity.this, new String[]{Manifest.permission.CAMERA},2);
                }else{
                    openCamera();
                }
                break;
            case 2:
                if(ContextCompat.checkSelfPermission(ModifyHeadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            ModifyHeadActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
                break;
            case 3:
                break;
        }
        if(item.getItemId() == android.R.id.home){
            ModifyHeadActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 打开相册
     */
    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    /**
     *拍照
     */
    public void openCamera(){
        try {
            File file = new File(getExternalCacheDir(),"image.jpg");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();

            if(Build.VERSION.SDK_INT >= 23){
                uri = FileProvider.getUriForFile(ModifyHeadActivity.this,"com.example.ljh.wechat.fileprovider",file);
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
        switch (requestCode){
            case TAKE_PHOTO:    //拍照
                if(resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        bitmap = compressImage(bitmap);
                        ivHead.setImageBitmap(bitmap);
                        insertToSqlite(image(bitmap));  //保存到sqlite数据库
                        insertToOracle(image(bitmap));  //保存到服务器数据库

                        fragmentMy.updateHead();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:  //在相册中选择
                if(resultCode == RESULT_OK){
                    handleImage(data);
                    fragmentMy.updateHead();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                   Toast.makeText(ModifyHeadActivity.this, "you denide thi permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(ModifyHeadActivity.this, "you denide thi permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void handleImage(Intent data){
        String imagePath = null;
        Uri uri = data.getData();

        if(DocumentsContract.isDocumentUri(this,uri)){  //如果是document类型的uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];    //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){  //如果是content类型的uri,用普通方式处理
                imagePath = getImagePath(uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){ //如果是file类型的Uri,直接获取图片路径
                imagePath = uri.getPath();
            }
            showImage(imagePath);//显示图片
        }
    }

    /**
     *获取图片路径
     */
    public String getImagePath(Uri uri,String selection){
        String path = null;
        //通过Uri和Selection来获取图片的真实路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     *  显示图片
     */
    public void showImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap = compressImage(bitmap);
            ivHead.setImageBitmap(bitmap);
            insertToSqlite(image(bitmap));  //保存到sqlite数据库
            insertToOracle(image(bitmap));  //保存到服务器数据库
        }else {
            Toast.makeText(ModifyHeadActivity.this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    public void insertToSqlite(byte[] image){
        sqLiteDatabase = openOrCreateDatabase("ljh.db",0,null);
        Cursor cursor = sqLiteDatabase.query("user_head",new String[]{"username"},"username=?",new String[]{MainActivity.username},null,null,null);
        if(cursor.getCount() > 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("head",image);
            sqLiteDatabase.update("user_head",contentValues,"username=?",new String[]{MainActivity.username});
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("username",MainActivity.username);
            contentValues.put("head",image);
            sqLiteDatabase.insert("user_head",null,contentValues);
        }
        sqLiteDatabase.close();
    }

    public void insertToOracle(final byte[] image){
        new Thread(){
            @Override
            public void run() {
                String string = Base64.encodeToString(image,Base64.DEFAULT);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .connectTimeout(5000,TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("type","head")
                        .add("username",MainActivity.username)
                        .add("head",string)
                        .build();
                Request request = new Request.Builder()
                        .post(requestBody)
                        .url(url)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("tag","-------------------更换头像成功");

                    }
                });
            }
        }.start();

  }

  public Bitmap compressImage(Bitmap bitmap){
      Matrix matrix = new Matrix();
      matrix.setScale(0.1f,0.1f);
      bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
      return bitmap;
  }

    /**
     * 将图片转换为字节
     */
    public byte[] image(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void initView(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.chat_normal);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.actionbar_head));

        ivHead = (ImageView) findViewById(R.id.ivHead);
        Intent intent = getIntent();
        byte image[] = intent.getByteArrayExtra("Bitmap");
        if(image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if (bitmap != null) {
                ivHead.setImageBitmap(bitmap);
            } else {
                ivHead.setImageResource(R.drawable.address_normal);
            }
        }
    }

}
