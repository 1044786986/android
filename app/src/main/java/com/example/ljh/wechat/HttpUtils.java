package com.example.ljh.wechat;

import android.os.Handler;
import android.util.Log;
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
 * Created by ljh on 2017/9/16.
 */

public class HttpUtils {

    /*static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.SECONDS)
            .connectTimeout(5000,TimeUnit.SECONDS)
            .writeTimeout(5000,TimeUnit.SECONDS).build();

    static String result = null;
    private static Handler handler = new Handler();


   /* HttpUtils(){

    }

    public static void Login(final String username, final String passowrd, final String url){
        new Thread(){
            @Override
            public void run() {

                 RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", passowrd)
                        .build();

                 final Request request = new Request.Builder()
                        .post(requestBody)
                        .url(url)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    String result;
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("tag","---------------onFailure");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        result = response.body().string();

                        response.body().close();
                    }
                });
            }
        }.start();

    }

    /*public String SendData(String encode) {

        final byte data[] = getRequestData(map,encode).toString().getBytes();

        new Thread() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("charset", "UTF-8");
                    httpURLConnection.setDoOutput(true);     //打开输出流，以便向服务器提交数据
                    httpURLConnection.setDoInput(true);
                    //设置请求体的类型是文本类型
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //设置请求体的长度
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(data);

                    int response = httpURLConnection.getResponseCode();
                    if(response == HttpURLConnection.HTTP_OK){
                        Log.i("tag","-------------成功");
                        InputStream inputStream = httpURLConnection.getInputStream();
                        result = ResponseResult(inputStream);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return result;
    }

    public  StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public static String ResponseResult(InputStream inputStream){
        String resultData;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        int len = 0;
        try {
            while( (len = inputStream.read(data) ) != -1 ){
                byteArrayOutputStream.write(data,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String (byteArrayOutputStream.toByteArray());
        return resultData;
    }*/

}
