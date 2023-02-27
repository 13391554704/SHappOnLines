package com.sw.mobsale.online.fragment;


import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.ui.MyPagerAdapter;

import java.util.ArrayList;

/**
 * 订单Fragment
 */
public class DetailFragment extends Fragment implements View.OnClickListener {
    //订单标签
    private TextView tvUnfinish, tvFinish;
    private View viewUnfinish, viewFinish;
    //fragment
//    private UnFinishFragment unFinishFragment;
//    private FinishFragment finishFragment;
//    //数据
//    private ArrayList<Fragment> fragmentList;
    //viewpager
    private ViewPager viewPager;
    //adapter
    private MyPagerAdapter myPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        initView(view);
        initViewPager();
        return view;
    }

    /**
     * 初始化
     * @param view view
     */
    private void initView(View view) {
        tvUnfinish = (TextView) view.findViewById(R.id.detail_tv_unfinish);
        tvFinish = (TextView) view.findViewById(R.id.detail_tv_finish);
        viewUnfinish = view.findViewById(R.id.detail_view_gone1);
        viewFinish = view.findViewById(R.id.detail_view_gone2);
        viewPager = (ViewPager) view.findViewById(R.id.detail_fl_content);
        //数据 fragment
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        //fragment
        UnFinishFragment unFinishFragment = new UnFinishFragment();
        FinishFragment finishFragment = new FinishFragment();
        fragmentList.add(unFinishFragment);
        fragmentList.add(finishFragment);
        myPagerAdapter = new MyPagerAdapter(getFragmentManager(), fragmentList);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setHintColor();
                switch (i) {
                    case 0:
                        tvUnfinish.setTextColor(Color.parseColor("#e84232"));
                        viewUnfinish.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tvFinish.setTextColor(Color.parseColor("#e84232"));
                        viewFinish.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(0);
        tvUnfinish.setTextColor(Color.parseColor("#e84232"));
        viewUnfinish.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化标签
     */
    private void setHintColor() {
        tvUnfinish.setTextColor(Color.parseColor("#454545"));
        viewUnfinish.setVisibility(View.INVISIBLE);
        tvFinish.setTextColor(Color.parseColor("#454545"));
        viewFinish.setVisibility(View.INVISIBLE);
    }


    /**
     * 点击事件
     */
    public void initViewPager() {
        tvUnfinish.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
    }


    /**
     * 点击监听函数
     * @param v v
     */
    @Override
    public void onClick(View v) {
        setHintColor();
        switch (v.getId()) {
            case R.id.detail_tv_unfinish:
                viewPager.setCurrentItem(0);
                tvUnfinish.setTextColor(Color.parseColor("#e84232"));
                viewUnfinish.setVisibility(View.VISIBLE);
                break;
            case R.id.detail_tv_finish:
                viewPager.setCurrentItem(1);
                tvFinish.setTextColor(Color.parseColor("#e84232"));
                viewFinish.setVisibility(View.VISIBLE);
                break;
        }
    }
//
//    /**
//     * 更新
//     */
//    public void update(){
//        viewPager.setCurrentItem(1,true);
//        myPagerAdapter.updateTwo(1);
//        viewPager.setCurrentItem(0,true);
//        myPagerAdapter.updateTwo(0);
//    }

}
