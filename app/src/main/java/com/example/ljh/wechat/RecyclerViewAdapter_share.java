package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljh on 2017/10/29.
 */

public class RecyclerViewAdapter_share extends RecyclerView.Adapter<RecyclerViewAdapter_share.ViewHolder>{
    private LayoutInflater layoutInflater;
    private List<ShareBean> datalist;

    RecyclerViewAdapter_share(Context context,List<ShareBean> datalist){
        layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_share2,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvUserName.setText(datalist.get(position).getUsername());
        holder.tvDate.setText(datalist.get(position).getDate());
        holder.tvTitle.setText(datalist.get(position).getTitle());
        holder.tvContent.setText(datalist.get(position).getContent());
        holder.tvComment.setText(datalist.get(position).getComment());
        holder.tvNice.setText(datalist.get(position).getNice());

        String headString = datalist.get(position).getHeadString();
        if(headString != null){
            byte head[] = Base64.decode(headString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(head,0,head.length);
            holder.ivHead.setImageBitmap(bitmap);
        }else{
            holder.ivHead.setImageResource(R.mipmap.ic_person_black_24dp);
        }


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHead;
        ImageView ivContent1,ivContent2,ivContent3;
        TextView tvUserName,tvDate;
        TextView tvTitle,tvContent;
        TextView tvComment,tvNice;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
