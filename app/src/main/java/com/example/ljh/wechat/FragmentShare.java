package com.example.ljh.wechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ljh on 2017/9/18.
 */

public class FragmentShare extends Fragment implements View.OnClickListener{

    private NavigationView navigationView;
    private CircleImageView ccivHead;
    private TextView tvUserName,tvAll,tvFriend;
    private ImageView ivWritePost;
    private LinearLayout layout_all,layout_friend;  //所有人的帖子，好友的帖子
    private LinearLayout layout; //记录layout_all还是layout_friend
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //private List<String> tabStringList;//语言类型
    static List<ShareBean> datalist;        //帖子数据
    //private List<Fragment> fragmentList;    //语言类型fragment
    private String tabStringList[] = {"all","android","java","javascript","php","python","unity","other"};
    private Fragment fragmentList[] = {new Fragment_all(),new Fragment_android(),new Fragment_java(),
            new Fragment_javascript(),new Fragment_php(),new Fragment_python(),new Fragment_unity(),new Fragment_other()};

    static int startId = 0; //第一个帖子的流水号
    static int endId = 0;   //最后一个帖子的流水号
    int pos = 0;         //记录当前的fragment是第几个
    static String typeString = "all";
    static String UserType = "all";

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share,container,false);
        datalist  = new ArrayList<ShareBean>();
        //fragmentList = new ArrayList<Fragment>();
        //tabStringList = new ArrayList<String>();
        //AddBitmapList();
        initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivWritePost:  //跳转编写帖子页面
                Intent intent = new Intent(getActivity(),WriterActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_all:
                if(!layout.equals(layout_all)){
                    layout = layout_all;
                    setNormalBackground();
                    layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));
                    tvAll.setTextColor(Color.rgb(0,188,212));
                    tvFriend.setTextColor(Color.rgb(158,158,158));

                    UserType = "all";
                    getData();
                }
                break;
            case R.id.layout_friend:
                if(!layout.equals(layout_friend)){
                    layout = layout_friend;
                    setNormalBackground();
                    layout_friend.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));
                    tvFriend.setTextColor(Color.rgb(0,188,212));
                    tvAll.setTextColor(Color.rgb(158,158,158));

                    UserType = "friend";
                    getData();
                }
                break;
        }
    }

    public void getData(){
        switch (pos){
            case 0:
                Fragment_all.fragment_all.showProgressDialog(); //显示进度条
                Fragment_all.fragment_all.upDataPost();         //获取数据
                break;
            case 1:
                Fragment_android.fragment_android.showProgressDialog();
                Fragment_android.fragment_android.upDataPost();
                break;
            case 2:
                Fragment_java.fragment_java.showProgressDialog();
                Fragment_java.fragment_java.upDataPost();
                break;
            case 3:
                Fragment_javascript.fragment_javascript.showProgressDialog();
                Fragment_javascript.fragment_javascript.upDataPost();
                break;
            case 4:
                Fragment_php.fragment_php.showProgressDialog();
                Fragment_php.fragment_php.upDataPost();
                break;
            case 5:
                Fragment_python.fragment_python.showProgressDialog();
                Fragment_python.fragment_python.upDataPost();
                break;
            case 6:
                Fragment_unity.fragment_unity.showProgressDialog();
                Fragment_unity.fragment_unity.upDataPost();
                break;
            case 7:
                Fragment_other.fragment_other.showProgressDialog();
                Fragment_other.fragment_other.upDataPost();
                break;


        }
    }

    public static byte[] BitmapTobyte(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 初始化数据源
     */
    public static void clearData(){
        //datalist.clear();
        startId = 0;
        endId = 0;
    }

    public void setNormalBackground(){
        layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_NormalBackground));
        layout_friend.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_NormalBackground));
    }


    public void initTabLayout(View view){
        viewPager = (ViewPager) view.findViewById(R.id.ViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.TabLayout);
    }

    public void initView(View view){
        initTabLayout(view);
        ViewPagerAdapter_share viewPagerAdapter = new ViewPagerAdapter_share(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.DrawerLayout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_myself:
                        Intent intent = new Intent(getActivity(),MyPostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_boutique:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        View view1 = navigationView.getHeaderView(0);
        ccivHead = (CircleImageView) view1.findViewById(R.id.ccivHead);
        ccivHead.setImageBitmap(FragmentMy.getMyHead());
        tvUserName = (TextView) view1.findViewById(R.id.tvUserName);
        tvUserName.setText(MainActivity.username);

        ivWritePost = (ImageView) view.findViewById(R.id.ivWritePost);
        tvAll = (TextView) view.findViewById(R.id.tvAll);
        tvFriend = (TextView) view.findViewById(R.id.tvFriend);

        /**
         * 选择查看所有人的帖子还是好友的帖子
         */
        layout_all = (LinearLayout) view.findViewById(R.id.layout_all);
        layout_friend = (LinearLayout) view.findViewById(R.id.layout_friend);
        layout_all.setBackground(getActivity().getResources().getDrawable(R.color.layout_all_SelectBackground));
        layout = layout_all;

        /**
         * 写帖子,查看所有人和好友帖子
         */
        ivWritePost.setOnClickListener(this);
        layout_all.setOnClickListener(this);
        layout_friend.setOnClickListener(this);

    }

    class ViewPagerAdapter_share extends FragmentStatePagerAdapter {

        public ViewPagerAdapter_share(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position != 0){
                pos = position - 1;
            }else{
                pos = position;
            }
            //return fragmentList.get(position);
            return fragmentList[position];
        }

        @Override
        public int getCount() {
            return fragmentList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabStringList[position];
        }

       /* @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }*/
    }
}
