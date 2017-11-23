package com.example.ljh.wechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cn.finalteam.galleryfinal.widget.HorizontalListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2017/11/23.
 */

public class MyPostActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog progressDialog;

    private RecyclerViewAdapter_myPost adapter;
    private PreViewManager preViewManager;
    private  List<ShareBean> datalist;
    private  int startId = 0; //第一个帖子的流水号
    private  int endId = 0;   //最后一个帖子的流水号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);
        getSupportActionBar().hide();

        datalist = new ArrayList<ShareBean>();
        preViewManager = new PreViewManager();
        initView();
        showProgressDialog();
        getPost();
    }

    /**
     * 获取帖子
     */
    public void getPost(){
        ExecutorService executorService = ThreadManager.startThread();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(5000, TimeUnit.SECONDS)
                        .writeTimeout(5000,TimeUnit.SECONDS)
                        .connectTimeout(5000,TimeUnit.SECONDS)
                        .build();
                RequestBody requestBody = new FormBody.Builder()
                        .add("type","getMyPost")
                        .add("p_id",endId+"")
                        .add("username",MainActivity.username)
                        .build();
                Request request = new Request.Builder()
                        .post(requestBody)
                        .url(MainActivity.GetPostServlet)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = handler.obtainMessage();
                        message.obj = "getPostFailure";
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String result = response.body().string();
                            Log.i("aaa","result = " + result);
                            if(!result.equals("{\"post\":[]}")) {    //还有更多帖子
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                Gson gson = new Gson();
                                List<ShareBean> list = new ArrayList<ShareBean>();
                                list = gson.fromJson(jsonArray.toString(), new TypeToken<List<ShareBean>>() {
                                }.getType());
                                for (int i = 0; i < list.size(); i++) {
                                    ShareBean shareBean = new ShareBean();
                                    shareBean.setId(list.get(i).getId());
                                    shareBean.setUsername(list.get(i).getUsername());
                                    shareBean.setHeadString(list.get(i).getHeadString());
                                    shareBean.setType(list.get(i).getType());
                                    shareBean.setTitle(list.get(i).getTitle());
                                    shareBean.setContent(list.get(i).getContent());
                                    shareBean.setComment(list.get(i).getComment());
                                    shareBean.setDate(list.get(i).getDate());
                                    shareBean.setNice(list.get(i).getNice());
                                    shareBean.setNonice(list.get(i).getNonice());
                                    shareBean.setList(list.get(i).getList());
                                    datalist.add(shareBean);
                                }
                                Log.i("aaa","list.size = " + datalist.size());
                                Message message = handler.obtainMessage();
                                message.obj = "getPostTrue";
                                message.sendToTarget();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String string = msg.obj + "";
            if (string.equals("getPostFailure")) {
                Toast.makeText(MyPostActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();

            } else if (string.equals("getPostTrue")) {
                adapter.notifyDataSetChanged();   //更新列表
                initId();                                          //更新第一个帖子和最后一个帖子的流水号
                if(progressDialog != null && progressDialog.isShowing()){
                    dismissProgressDialog();
                }
            }
        }
    };

        /**
         * //更新第一个帖子和最后一个帖子的流水号
         */
        public void initId(){
            if(datalist != null && datalist.size() !=0){
                startId = Integer.parseInt(datalist.get(0).getId());
                endId = Integer.parseInt(datalist.get(datalist.size()-1).getId());
            }
        }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void initView(){
        adapter = new RecyclerViewAdapter_myPost();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_myPost);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datalist.clear();
        adapter = null;
        preViewManager = null;
        startId = 0;
        endId = 0;
    }

    class RecyclerViewAdapter_myPost extends RecyclerView.Adapter<RecyclerViewAdapter_myPost.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyPostActivity.this).inflate(R.layout.item_share,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tvUserName.setText(datalist.get(position).getUsername());
            holder.tvDate.setText(datalist.get(position).getDate());
            holder.tvTitle.setText(datalist.get(position).getTitle());
            holder.tvContent.setText(datalist.get(position).getContent());
            holder.tvComment.setText(datalist.get(position).getComment());
            holder.tvNice.setText(datalist.get(position).getNice());

            String headString = datalist.get(position).getHeadString();
            if(headString != null && headString != ""){
                byte head[] = Base64.decode(headString,Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
                holder.ivHead.setImageBitmap(bitmap);
            }else{
                holder.ivHead.setImageResource(R.mipmap.ic_person_black_24dp);
            }

            List<String> list = new ArrayList<String>();
            list = datalist.get(position).getList();
            /**
             * 文章里有图片
             */

            if(list != null && list.size() > 0){
                for (int i =0;i<list.size();i++){

                    //Bitmap bitmap = null;
                    switch (i+1){
                        case 1:
                            final Bitmap bitmap = preViewManager.StringToBitmap(list.get(i));
                            holder.ivContent1.setVisibility(View.VISIBLE);
                            holder.ivContent1.setImageBitmap(bitmap);
                            holder.ivContent1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startIntentActivity(bitmap);

                                }
                            });
                            break;
                        case 2:
                            final Bitmap bitmap2 = preViewManager.StringToBitmap(list.get(i));
                            holder.ivContent2.setVisibility(View.VISIBLE);
                            holder.ivContent2.setImageBitmap(bitmap2);
                            holder.ivContent2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startIntentActivity(bitmap2);
                                }
                            });
                            break;
                        case 3:
                            final Bitmap bitmap3 = preViewManager.StringToBitmap(list.get(i));
                            holder.ivContent3.setVisibility(View.VISIBLE);
                            holder.ivContent3.setImageBitmap(bitmap3);
                            holder.ivContent3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startIntentActivity(bitmap3);
                                }
                            });
                            break;
                    }
                }

                /**
                 * //文章不包含图片
                 */
            }else{
                holder.ivContent1.setVisibility(View.GONE);
                holder.ivContent2.setVisibility(View.GONE);
                holder.ivContent3.setVisibility(View.GONE);
            }

            /**
             * 当绑定最后一项的时候，申请加载更多数据
             */
            if (position == datalist.size()-1){
                getPost();
            }

            /**
             * 点击帖子进入详细内容页面
             */
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyPostActivity.this,PostActivity.class);
                    intent.putExtra("shareBean",datalist.get(position));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        public void startIntentActivity(Bitmap bitmap){
            Intent intent = new Intent(MyPostActivity.this,PreviewActivity.class);
            intent.putExtra("bitmap",FragmentShare.BitmapTobyte(bitmap));
            startActivity(intent);
        }


        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView ivHead;
            ImageView ivContent1,ivContent2,ivContent3;
            TextView tvUserName,tvDate;
            TextView tvTitle,tvContent;
            TextView tvComment,tvNice;
            HorizontalListView horizontalListView;

            public ViewHolder(View itemView) {
                super(itemView);

                ivHead = (ImageView) itemView.findViewById(R.id.ivHead);
                ivContent1 = (ImageView) itemView.findViewById(R.id.ivContent1);
                ivContent2 = (ImageView) itemView.findViewById(R.id.ivContent2);
                ivContent3 = (ImageView) itemView.findViewById(R.id.ivContent3);
                tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvComment = (TextView) itemView.findViewById(R.id.tvComment);
                tvNice = (TextView) itemView.findViewById(R.id.tvNice);
                horizontalListView = (HorizontalListView) itemView.findViewById(R.id.horizontalListView);
            }
        }
    }
}
