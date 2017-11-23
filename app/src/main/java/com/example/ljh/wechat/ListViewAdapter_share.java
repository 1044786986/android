package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/10/29.
 */

public class ListViewAdapter_share extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private List<String> datalist;
    private List<TextView> tvList;
    private Context context;

    private boolean firstLoad = true;

    FragmentShare fragmentShare;

    ListViewAdapter_share(Context context, List<String> datalist){
        this.context = context;
        this.datalist = datalist;
        this.layoutInflater = LayoutInflater.from(context);
        fragmentShare = new FragmentShare();
        tvList = new ArrayList<TextView>();
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
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_listview_share,null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);

            tvList.add(viewHolder.textView);
            if(firstLoad){
                viewHolder.textView.setTextColor(Color.rgb(0,188,212));
                firstLoad = false;
            }
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(datalist.get(position));
        //viewHolder.textView.setTextColor(Color.rgb(139,139,139));

        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notifyDataSetChanged();
                setTextNormalColor();
                Toast.makeText(context,datalist.get(position)+position,Toast.LENGTH_SHORT).show();
                FragmentShare.clearData();
                FragmentShare.typeString = datalist.get(position);
                //fragmentShare.getPost();
                viewHolder.textView.setTextColor(Color.rgb(0,188,212));
            }
        });
        return convertView;
    }

    public void setTextNormalColor(){
        for (int i=0;i<tvList.size();i++){
            tvList.get(i).setTextColor(Color.rgb(139,139,139));
        }
    }

    class ViewHolder{
        TextView textView;
    }
}
