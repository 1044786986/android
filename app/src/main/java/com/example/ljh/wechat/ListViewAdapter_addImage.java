package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by ljh on 2017/11/1.
 */

public class ListViewAdapter_addImage extends BaseAdapter{
    private static List<Bitmap>list;
    private Context context;
    private LayoutInflater layoutInflater;

    ListViewAdapter_addImage(Context context, List<Bitmap> list){
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_listview_addimage,null);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
            final Bitmap bitmap  = list.get(position);
            viewHolder.ivPhoto.setImageBitmap(bitmap);
            viewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PreviewActivity.class);
                    intent.putExtra("bitmap",FragmentShare.BitmapTobyte(bitmap));   //bitmap转为byte[]
                    context.startActivity(intent);
                }
            });

           viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   list.remove(position);
                   notifyDataSetChanged();
                   if(list.size() == 0 || list == null){
                       WriterActivity.hideListView();
                   }
               }
           });

        return convertView;
    }

    class ViewHolder{
        ImageView ivPhoto;
        ImageView ivDelete;
    }
}
