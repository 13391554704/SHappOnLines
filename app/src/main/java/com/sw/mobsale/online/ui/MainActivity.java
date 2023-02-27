package com.sw.mobsale.online.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.fragment.DetailFragment;
import com.sw.mobsale.online.fragment.MainFragment;
import com.sw.mobsale.online.fragment.MoreFragment;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SystemBarTintManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * 主页面
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ViewPager mPager; //viewpager
    private MyPagerAdapter myPagerAdapter;  //pageradapter
    //fragment集合
    private ArrayList<Fragment> fragmentList;
    private DetailFragment detailFragment;
    private MainFragment mainFragment ;
    private MoreFragment moreFragment;
    //标签
    private ImageView ivDetail, ivMain, ivMore;
    private TextView tvDetail,tvMain,tvMore;
    private RelativeLayout rlDetail,rlMain,rlMore;
    private long exitTime = 0; //离开时间
    private String userName,userPwd,phoneCode;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
        Log.d("TAG","main--> onCreate()");
        //通知栏颜色
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(Color.parseColor("#e84232"));
    }

    /**
     * 初始化标签名
     */
    public void initView() {
        rlDetail = (RelativeLayout) findViewById(R.id.main_rl_detail);
        rlMain = (RelativeLayout) findViewById(R.id.main_rl_sell);
        rlMore = (RelativeLayout) findViewById(R.id.main_rl_more);
        ivDetail = (ImageView) findViewById(R.id.main_iv_detail);
        ivMain = (ImageView) findViewById(R.id.main_iv_sell);
        ivMore = (ImageView) findViewById(R.id.main_iv_more);
        tvDetail = (TextView) findViewById(R.id.main_tv_detail);
        tvMain = (TextView) findViewById(R.id.main_tv_sell);
        tvMore = (TextView) findViewById(R.id.main_tv_more);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        mainFragment = new MainFragment();
        detailFragment = new DetailFragment();
        moreFragment = new MoreFragment();
        fragmentList.add(mainFragment);
        fragmentList.add(detailFragment);
        fragmentList.add(moreFragment);
        //PagerAdapter
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        mPager.setAdapter(myPagerAdapter);
        mPager.setOffscreenPageLimit(2);
        //起始为当前页
        mPager.setCurrentItem(0);
        ivMain.setImageResource(R.drawable.main_unpro);
        tvMain.setTextColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //viewpager滑动监听
        handler = new MyHandler();
        SharedPreferences sdf = getSharedPreferences("user", Context.MODE_PRIVATE);
        userName = sdf.getString("userCode","");
        userPwd = sdf.getString("password","");
        phoneCode =sdf.getString("phoneCode","");
        MyThread.getInstance(MainActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setHintColor();
                switch (i) {
                    case 0:
                        ivMain.setImageResource(R.drawable.main_unpro);
                        tvMain.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 1:
                        ivDetail.setImageResource(R.drawable.main_unmobile);
                        tvDetail.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 2:
                        ivMore.setImageResource(R.drawable.main_unmy);
                        tvMore.setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //viewpager点击监听
        rlDetail.setOnClickListener(this);
        rlMain.setOnClickListener(this);
        rlMore.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        setHintColor();
        switch (v.getId()) {
            case R.id.main_rl_sell:
                mPager.setCurrentItem(0);
                ivMain.setImageResource(R.drawable.main_unpro);
                tvMain.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.main_rl_detail:
                mPager.setCurrentItem(1);
                ivDetail.setImageResource(R.drawable.main_unmobile);
                tvDetail.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.main_rl_more:
                mPager.setCurrentItem(2);
                ivMore.setImageResource(R.drawable.main_unmy);
                tvMore.setTextColor(Color.parseColor("#ffffff"));
                break;
        }
    }

    /**
     * 初始化标签
     */
    public void setHintColor() {
        ivDetail.setImageResource(R.drawable.main_mobile);
        ivMain.setImageResource(R.drawable.main_pro);
        ivMore.setImageResource(R.drawable.main_my);
        tvDetail.setTextColor(Color.parseColor("#ff6f62"));
        tvMain.setTextColor(Color.parseColor("#ff6f62"));
        tvMore.setTextColor(Color.parseColor("#ff6f62"));
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //强制退出
                case Constant.FORCE_DOWN:
                    String result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(MainActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(MainActivity.this).getDialog(MainActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 连续点击两次退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * singleTask 模式下重新进入时调用次方法  onNewIntent->onReStart->onStart->onResume
     * @param intent
     */
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        //实时更新fragment数据
//        mPager.setCurrentItem(1,true);
//        myPagerAdapter.update(1);
//    }

    /**
     * 自定义字体
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG","main--> onDestroy()");
    }
}