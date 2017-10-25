package com.example.ljh.wechat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/9/22.
 */

public class ListViewAdapter_Add extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context context;
    private List<AddressBean> datalist  = new ArrayList<AddressBean>();
    private String friend;//判断该用户是否已经是好友

    ListViewAdapter_Add(Context context, List<AddressBean> datalist,String friend){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        this.friend = friend;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view == null){
            view = layoutInflater.inflate(R.layout.item_add,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.ivHead);
            viewHolder.textView = (TextView) view.findViewById(R.id.tvUserName);
            viewHolder.button = (Button) view.findViewById(R.id.btAdd);
            if(friend.equals("false")) {
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AddFriendNoteActivity.class);
                        intent.putExtra("username", datalist.get(i).getName());
                        context.startActivity(intent);
                    }
                });
            }else {
                viewHolder.button.setText("发送消息");
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

            if(datalist.get(i).getName().equals("false")){   //判断是否搜索到用户
                viewHolder.textView.setText("该用户不存在");
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.button.setVisibility(View.GONE);
            }else {
                viewHolder.textView.setText(datalist.get(i).getName());
                if(datalist.get(i).getBitmap() != null){
                    viewHolder.imageView.setImageBitmap(datalist.get(i).getBitmap());   //显示头像
                }else {
                    viewHolder.imageView.setImageResource(R.drawable.address_normal);   //如果没有头像
                }
            }
        return view;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
        Button button;
    }
}
