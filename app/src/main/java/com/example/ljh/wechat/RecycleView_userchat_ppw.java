package com.example.ljh.wechat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ljh on 2017/11/14.
 */

public class RecycleView_userchat_ppw extends RecyclerView.Adapter<RecycleView_userchat_ppw.ViewHolder>{
    private LayoutInflater layoutInflater;
    private List<String> datalist;
    private OnItemClickListener listener = null;

    RecycleView_userchat_ppw(Context context, List<String>datalist){
        this.layoutInflater  = LayoutInflater.from(context);
        this.datalist = datalist;
    }

    public interface OnItemClickListener{
        void OnItemClick(View view,int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= layoutInflater.inflate(R.layout.item_lv_ppw_userchat,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(datalist.get(position));
        if(listener != null){
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
