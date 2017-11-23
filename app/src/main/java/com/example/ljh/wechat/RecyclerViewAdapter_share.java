package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.widget.HorizontalListView;

/**
 * Created by ljh on 2017/10/29.
 */

public class RecyclerViewAdapter_share extends RecyclerView.Adapter<RecyclerViewAdapter_share.ViewHolder> implements View.OnClickListener{
    private LayoutInflater layoutInflater;
    private static List<ShareBean> datalist;
    private Context context;

    RecyclerViewAdapter_share(Context context,List<ShareBean> datalist){
        layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_share,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
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

                Bitmap bitmap = null;
                switch (i+1){
                    case 1:
                        bitmap = StringToBitmap(list.get(i));
                        holder.ivContent1.setVisibility(View.VISIBLE);
                        holder.ivContent1.setImageBitmap(bitmap);
                        holder.ivContent1.setOnClickListener(this);
                        break;
                    case 2:
                        bitmap = StringToBitmap(list.get(i));
                        holder.ivContent2.setVisibility(View.VISIBLE);
                        holder.ivContent2.setImageBitmap(bitmap);
                        holder.ivContent2.setOnClickListener(this);
                        break;
                    case 3:
                        bitmap = StringToBitmap(list.get(i));
                        holder.ivContent3.setVisibility(View.VISIBLE);
                        holder.ivContent3.setImageBitmap(bitmap);
                        holder.ivContent3.setOnClickListener(this);
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
            /*Fragment_all fragment_all = new Fragment_all();
            fragment_all.getPost();*/
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
