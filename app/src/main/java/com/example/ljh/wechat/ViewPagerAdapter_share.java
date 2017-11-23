package com.example.ljh.wechat;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ljh on 2017/11/15.
 */

public class ViewPagerAdapter_share extends PagerAdapter{
    private List<View> viewList;
    private List<View> tabList;
    private List<String> tabStringList;
    private Context context;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter_share adapter_share;

    ViewPagerAdapter_share(Context context, List<View> viewList,List<View> tabList,List<String>tabStringList){
        this.viewList = viewList;
        this.tabList = tabList;
        this.tabStringList = tabStringList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabStringList.get(position);
    }
}
