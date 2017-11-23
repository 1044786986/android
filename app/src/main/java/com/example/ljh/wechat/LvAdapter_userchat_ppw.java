package com.example.ljh.wechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/11/14.
 */

public class LvAdapter_userchat_ppw extends BaseAdapter{
    private List<String> datalist;
    private LayoutInflater layoutInflater;
    private Context context;

    LvAdapter_userchat_ppw(Context context,List<String>datalist){
        layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_lv_ppw_userchat,null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
            viewHolder.textView.setText(datalist.get(position));

        return convertView;
    }


    class ViewHolder{
        TextView textView;
    }
}
