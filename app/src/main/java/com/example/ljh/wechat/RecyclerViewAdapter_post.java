package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljh on 2017/11/19.
 */

public class RecyclerViewAdapter_post extends RecyclerView.Adapter<RecyclerViewAdapter_post.ViewHolder>{
    private LayoutInflater layoutInflater;
    private Context context;
    private List<PostBean> datalist;
    PreViewManager preViewManager;

    RecyclerViewAdapter_post(Context context, List<PostBean> datalist){
        this.layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        this.context = context;
        preViewManager = new PreViewManager();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_post,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvUsername.setText(datalist.get(position).getUsername());
        holder.tvFloor.setText("#" + (position+1));
        holder.tvDate.setText(datalist.get(position).getDate());
        holder.ivHead.setImageBitmap(preViewManager.StringToBitmap(datalist.get(position).getHeadString()));
        holder.tvContent.setText(datalist.get(position).getContent());
        final String imageString = datalist.get(position).getImageString();
        if(imageString.equals("")){
            holder.imageView.setVisibility(View.GONE);
        }else if(!imageString.equals("")){
            holder.imageView.setImageBitmap(preViewManager.StringToBitmap(imageString));
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PreviewActivity.class);
                    intent.putExtra("bitmap",preViewManager.StringToByte(imageString));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivHead;
        ImageView imageView;
        TextView tvUsername;
        TextView tvContent;
        TextView tvDate;
        TextView tvFloor;
        public ViewHolder(View itemView) {
            super(itemView);
            ivHead = (ImageView) itemView.findViewById(R.id.ivHead);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvFloor = (TextView) itemView.findViewById(R.id.tvFloor);
        }
    }
}
