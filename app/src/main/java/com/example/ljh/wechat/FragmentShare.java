package com.example.ljh.wechat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.widget.HorizontalListView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ljh on 2017/9/18.
 */

public class FragmentShare extends Fragment implements View.OnClickListener{
    private HorizontalListView listView;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private CircleImageView ccivHead;
    private TextView tvUserName;
    private ImageView ivWritePost;
    private LinearLayout layout_all,layout_friend;

    private List<String> BitmapList = new ArrayList<String>();

    ListViewAdapter_share listViewAdapter_share;

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share,null);
        AddBitmapList();
        initView(view);
        return view;
    }

    public void AddBitmapList(){
        BitmapList.add("all");
        BitmapList.add("android");
        BitmapList.add("java");
        BitmapList.add("javascript");
        BitmapList.add("php");
        BitmapList.add("python");
        BitmapList.add("unity");
        BitmapList.add("other");
    }

    public void setNormalBackground(){
        layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_NormalBackground));
        layout_friend.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_NormalBackground));
    }

    public void initView(View view){
        listView = (HorizontalListView) view.findViewById(R.id.ListView_share);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_share);
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        ccivHead = (CircleImageView) view.findViewById(R.id.ccivHead);
        ivWritePost = (ImageView) view.findViewById(R.id.ivWritePost);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        layout_all = (LinearLayout) view.findViewById(R.id.layout_all);
        layout_friend = (LinearLayout) view.findViewById(R.id.layout_friend);
        layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));

        ivWritePost.setOnClickListener(this);
        layout_all.setOnClickListener(this);
        layout_friend.setOnClickListener(this);

        listViewAdapter_share = new ListViewAdapter_share(getActivity(),BitmapList);
        listView.setAdapter(listViewAdapter_share);

        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivWritePost:
                Intent intent = new Intent(getActivity(),WriterActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_all:
                setNormalBackground();
                layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));
                break;
            case R.id.layout_friend:
                setNormalBackground();
                layout_friend.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));
            break;
        }
    }
}
