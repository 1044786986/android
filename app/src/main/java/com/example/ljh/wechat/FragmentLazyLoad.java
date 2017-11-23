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
 * Created by ljh on 2017/11/16.
 */

public abstract class FragmentLazyLoad extends Fragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter_share recyclerViewAdapter_share;

    private List<ShareBean> datalist;
    private  int startId = 0; //第一个帖子的流水号
    private  int endId = 0;   //最后一个帖子的流水号

    private boolean isViewCreate = false;
    private boolean isVisible = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.viewpager_share,null);

        datalist  = new ArrayList<ShareBean>();
        initView(view);
        isViewCreate = true;

        return view;
    }

    private void lazyLoad(){
        if(isViewCreate && isVisible){
            showProgressDialog();
            upDataPost();
            isViewCreate = false;
            isVisible = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            isVisible = true;
            lazyLoad();
        }else{
            isVisible = false;
        }
    }

    protected abstract void upDataPost();
    protected abstract void getPost();
    protected abstract void showProgressDialog();
    protected abstract void initId();


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

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("aa","--------------onDestroy");
        datalist.clear();
        datalist = null;
        startId = 0;
        endId = 0;
        progressDialog.dismiss();
    }

    class RecyclerViewAdapter_share extends RecyclerView.Adapter<RecyclerViewAdapter_share.ViewHolder> implements View.OnClickListener{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_share,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            Log.i("aaa","onBindViewHolder");
            Log.i("aaa","datalist.size = " + datalist.size());
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
                                    Intent intent = new Intent(getActivity(),PreviewActivity.class);
                                    intent.putExtra("bitmap",bitmap);
                                    startActivity(intent);
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
                                    Intent intent = new Intent(getActivity(),PreviewActivity.class);
                                    intent.putExtra("bitmap",bitmap2);
                                    startActivity(intent);
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
                                    Intent intent = new Intent(getActivity(),PreviewActivity.class);
                                    intent.putExtra("bitmap",bitmap3);
                                    startActivity(intent);
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
