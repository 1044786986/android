package com.example.ljh.wechat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljh on 2017/11/8.
 */

public class RecyclerViewAdapter_shareTab extends RecyclerView.Adapter<RecyclerViewAdapter_shareTab.ViewHolder>{
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String>list;
    private boolean firstLoad = true;
    private TextView textViews[] = new TextView[1];

    FragmentShare fragmentShare;

    RecyclerViewAdapter_shareTab(Context context, List<String> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        fragmentShare = new FragmentShare();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_listview_share,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(list.get(position));
        //holder.textView.setTextColor(Color.rgb(139,139,139));
        if(firstLoad){  //判断是否是第一次加载
            holder.textView.setTextColor(Color.rgb(0,188,212));
            textViews[0] = holder.textView;
            firstLoad = false;
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!textViews[0].equals(holder.textView)){
                    //FragmentShare.clearData();  //清除数据
                    FragmentShare.typeString = list.get(position);  //更变类型
                    //fragmentShare.upDataPost();    //获取数据
                    /**
                     * 更改选中颜色
                     */
                    textViews[0].setTextColor(Color.rgb(139,139,139));
                    holder.textView.setTextColor(Color.rgb(0,188,212));
                    textViews[0] = holder.textView;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
