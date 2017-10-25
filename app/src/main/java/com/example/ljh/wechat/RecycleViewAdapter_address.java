package com.example.ljh.wechat;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2017/9/20.
 */

public class RecycleViewAdapter_address extends RecyclerView.Adapter<RecycleViewAdapter_address.ViewHolder>{
    private List<AddressBean> datalist = new ArrayList<AddressBean>();
    private LayoutInflater layoutInflater;
    private RecycleView_Address_ItemViewListener ItemSelectListener = null;
    private Context context;

    RecycleViewAdapter_address(Context context, List<AddressBean> datalist){
        this.layoutInflater = LayoutInflater.from(context);
        this.datalist = datalist;
        this.context = context;
    }

    public interface RecycleView_Address_ItemViewListener{
            void onItemClick(View view,int position);
    }

    public void setItemSelectListener(RecycleView_Address_ItemViewListener listener){
            this.ItemSelectListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_address,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textView.setText(datalist.get(position).getName());
        holder.imageView.setImageBitmap(datalist.get(position).getBitmap());

        if(ItemSelectListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    ItemSelectListener.onItemClick(view,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivHead);
            textView = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

}
