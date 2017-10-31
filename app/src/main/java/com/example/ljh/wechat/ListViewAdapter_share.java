package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ljh on 2017/10/29.
 */

public class ListViewAdapter_share extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private List<String> datalist;
    private Context context;

    ListViewAdapter_share(Context context, List<String> datalist){
        this.context = context;
        this.datalist = datalist;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_listview_share,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int resId = context.getResources().getIdentifier(datalist.get(position),"drawable",context.getPackageName());
        viewHolder.imageView.setImageResource(resId);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,datalist.get(position)+position,Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
    }
}