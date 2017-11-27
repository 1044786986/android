package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/11/20.
 */

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.ViewHolder>{
    private final int TYPE_NORMAL = 1;
    private final int TYPE_HEADER = 0;
    private View mHeadView;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<PostBean> datalist = new ArrayList<PostBean>();

    private PreViewManager preViewManager;
    private PostActivity postActivity;

    HeaderAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        preViewManager = new PreViewManager();
        postActivity = new PostActivity();
    }

    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if(mHeadView != null && viewType == TYPE_HEADER){
            viewHolder = new ViewHolder(mHeadView);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_HEADER){
            return;
        }
        int pos = getPosition(holder);
        holder.tvUsername.setText(datalist.get(pos).getUsername());
        holder.tvFloor.setText("#" + pos);
        holder.tvDate.setText(datalist.get(pos).getDate());
        holder.ivHead.setImageBitmap(preViewManager.StringToBitmap(datalist.get(pos).getHeadString()));
        holder.tvContent.setText(datalist.get(pos).getContent());
        final String imageString = datalist.get(pos).getImageString();
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
        if(mHeadView != null){
            return datalist.size()+1;
        }else{
            return datalist.size();
        }
    }

    public void getData(int positon){
        if(positon == datalist.size()-1){
            postActivity.getData();
        }
    }

    public void setHeadView(View headView){
        this.mHeadView = headView;
        notifyItemInserted(0);
    }

    public View getHeadView(){
        return mHeadView;
    }

    public void addData(List<PostBean> list){
        datalist.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if(mHeadView == null){
            return TYPE_NORMAL;
        }
        else if(position == 0){
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    public int getPosition(ViewHolder viewHolder){
        int position= viewHolder.getLayoutPosition();
        if(mHeadView == null){
            return position;
        }else{
            return position - 1;
        }
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
