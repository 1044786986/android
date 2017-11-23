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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by ljh on 2017/11/15.
 */

public class Fragment_python extends Fragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter_share recyclerViewAdapter_share;

    private List<ShareBean> datalist;
    private  int startId = 0; //第一个帖子的流水号
    private  int endId = 0;   //最后一个帖子的流水号

    private boolean isViewCreate;
    private boolean isVisible;

    static Fragment_python fragment_python;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.viewpager_share,null);
        fragment_python = this;
        datalist  = new ArrayList<ShareBean>();
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreate = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreate && isVisible) {
            showProgressDialog();
            upDataPost();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreate = false;
            isVisible = false;
        }
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
                        .add("type","getPost")
                        .add("typeString","python")
                        .add("UserType",FragmentShare.UserType)
                        .add("startId",endId+"")
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
                                JSONArray jsonArray = jsonObject.getJSONArray("post");
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

    public void upDataPost(){
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
                        .add("type","upDataPost")
                        .add("typeString","python")
                        .add("UserType",FragmentShare.UserType)
                        .add("startId",startId+"")
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
                        String result = response.body().string();

                        try {
                            if(result != "" && result.length() != 0) {    //还有更多帖子
                                datalist.clear();
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("post");
                                Gson gson = new Gson();
                                /*datalist = gson.fromJson(jsonArray.toString(), new TypeToken<List<ShareBean>>(){
                                }.getType());*/
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
                            }
                            Log.i("aaa","list.size = " + datalist.size());
                            Message message = handler.obtainMessage();
                            message.obj = "getPostTrue";
                            message.sendToTarget();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String string = msg.obj + "";
            if (string.equals("getPostFailure")) {
                Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_SHORT).show();
            } else if (string.equals("getPostTrue")){
                recyclerViewAdapter_share.notifyDataSetChanged();   //更新列表
                initId();                                          //更新第一个帖子和最后一个帖子的流水号
            }
            /**
             * 隐藏动画效果
             */
            if(swipeRefreshLayout.isShown()) { //当swipeRefreshLayout在转转转
                swipeRefreshLayout.setRefreshing(false);
            }
            dismissProgressDialog();
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在加载");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void initView(View view){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        recyclerViewAdapter_share = new RecyclerViewAdapter_share();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter_share);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                upDataPost();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        datalist.clear();
        datalist = null;
        startId = 0;
        endId = 0;
        fragment_python = null;
    }

    public void startIntentActivity(Bitmap bitmap){
        Intent intent = new Intent(getActivity(),PreviewActivity.class);
        intent.putExtra("bitmap",FragmentShare.BitmapTobyte(bitmap));
        startActivity(intent);
    }

    class RecyclerViewAdapter_share extends RecyclerView.Adapter<RecyclerViewAdapter_share.ViewHolder> implements View.OnClickListener{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_share,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

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
                            final Bitmap bitmap = StringToBitmap(list.get(i));
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
                            final Bitmap bitmap2 = StringToBitmap(list.get(i));
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
                            final Bitmap bitmap3 = StringToBitmap(list.get(i));
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
                    Intent intent = new Intent(getActivity(),PostActivity.class);
                    intent.putExtra("shareBean",datalist.get(position));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return datalist.size();
        }

        public Bitmap StringToBitmap(String string){
            Bitmap bitmap = null;
            if(string != null && string != "" && string.length() != 0){
                byte byte1[] = Base64.decode(string,Base64.DEFAULT);
                bitmap = new BitmapFactory().decodeByteArray(byte1,0,byte1.length);
            }
            return bitmap;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivContent1:
                    break;
                case R.id.ivContent2:
                    break;
            }
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
