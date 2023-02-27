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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.BadgeView;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

/**
 * 铺货
 */
public class LoadSaleActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private ListView lvOrder;//listview
    private TextView tvTitle,tvChoose;//head title  choose
    private RelativeLayout rlBack;//head back
    private ArrayList<Map<String, Object>> dataLists = new ArrayList<Map<String,Object>>(); //零售数据
    private String result; //json
    private MyHandler handler;//handler
    private LoadAdapter adapter;//adapter
    private View viewShop;//动画开始位置
    private RelativeLayout rlNo;//dataLists.size() = 0
    //imageLoader
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    //购物车图层
    private ImageView shopCart,gotoTop;//购物车 置顶
    private ViewGroup anim_mask_layout;//动画层
    private ImageView buyImg;// 这是在界面上跑的小图片
    private int buyNum = 0;//购买数量
    private BadgeView buyNumView;//显示购买数量的控件
    private Intent intent;//intent
    //lv footer
    private View footerView;
    private int lastVisibleItemPosition = 0;
    private Boolean flag = true;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_sale);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        //购物车数量风格
        shopCart = (ImageView) findViewById(R.id.shopping_img_cart);
        buyNumView = new BadgeView(this, shopCart);
        buyNumView.setTextColor(Color.WHITE);
        buyNumView.setBackgroundColor(Color.RED);
        buyNumView.setTextSize(12);
        //界面
        lvOrder = (ListView) findViewById(R.id.load_sale_lv);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvChoose = (TextView) findViewById(R.id.head_title_gone);
        imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chanpin) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chanpin) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chanpin) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
                .build(); // 构建完成
        adapter = new LoadAdapter();
        handler = new MyHandler();
        rlNo = (RelativeLayout) findViewById(R.id.retail_rl_detail);
        tvTitle.setText("生成铺货单");
        tvChoose.setText("筛选");
        tvChoose.setVisibility(View.VISIBLE);
        //置顶
        gotoTop = (ImageView) findViewById(R.id.go_to_top);
        gotoTop.setVisibility(View.GONE);
        //footerView
        footerView = View.inflate(LoadSaleActivity.this, R.layout.item_lv_footer, null);
        MyThread.getInstance(LoadSaleActivity.this).SaleTypeAllThread(handler,Constant.LOAD_SALE_PATH_A,"","","A");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //返回
        rlBack.setOnClickListener(this);
        //筛选
        tvChoose.setOnClickListener(this);
        //购物车
        shopCart.setOnClickListener(this);
        //置顶
        gotoTop.setOnClickListener(this);
        //listview滑动监听
        lvOrder.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < lastVisibleItemPosition) {
                    // 上滑
                    if (firstVisibleItem == 0){
                        gotoTop.setVisibility(View.GONE);
                    }
                } else if (firstVisibleItem > lastVisibleItemPosition) {
                        gotoTop.setVisibility(View.VISIBLE);
                }
                lastVisibleItemPosition = firstVisibleItem;//更新位置
               // Log.d("TAG",lastVisibleItemPosition+"");

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回
            case R.id.head_rl_title:
                intent = new Intent(LoadSaleActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
                break;
            //筛选
            case R.id.head_title_gone:
                intent = new Intent(LoadSaleActivity.this,RightActivity.class);
                intent.putExtra("choose","loadSale");
                startActivityForResult(intent,Constant.SALE_CHOOSE);
                break;
            //购物车
            case R.id.shopping_img_cart:
                if (buyNum == 0){
                    Toast.makeText(LoadSaleActivity.this,"请添加商品",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    intent = new Intent(LoadSaleActivity.this, ShopCarActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            //置顶
            case R.id.go_to_top:
                lvOrder.setSelection(0);
                lastVisibleItemPosition = 0;
                gotoTop.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.SALE_CHOOSE){
            if (resultCode ==RESULT_OK){
                String itemTypeId = data.getStringExtra("id");
                String itemTypeOwnId = data.getStringExtra("ownId");
                MyThread.getInstance(LoadSaleActivity.this).SaleTypeAllThread(handler,Constant.LOAD_SALE_PATH_A,itemTypeId,itemTypeOwnId,"D");
            }
        }
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //产品列表   flag标识是否第一次进来laoadSale
                case Constant.LOAD_SALE:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadSaleActivity.this).getNoRes(result)) {
                        Log.d("TAG", "sale result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(LoadSaleActivity.this).getLoadSale(result);
                        Log.d("TAG", dataLists.toString());
                        if (dataLists.size() > 0) {
                            if (flag) {
                                flag = false;
                                if (dataLists.size() > 6) {
                                    lvOrder.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                lvOrder.setAdapter(adapter);
                            } else {
                                lvOrder.removeFooterView(footerView);
                                if (dataLists.size() > 6) {
                                    lvOrder.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                            buyNum = Integer.parseInt(dataLists.get(0).get("itemCount").toString());
                            getBuyNumber();
                        } else {
                            lvOrder.removeFooterView(footerView);
                            adapter.notifyDataSetChanged();
                            rlNo.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadSaleActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(LoadSaleActivity.this).getDialog(LoadSaleActivity.this);
                            } else {
                                Log.d("TAG", "item->" + id);
                                MyThread.getInstance(LoadSaleActivity.this).ChooseShopCarThread(handler, "1", Constant.ADD_SALE_SHOP_CAR, id, "A");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //添加商品 ->shopcar
                case Constant.ADD_SALE_SHOP_CAR:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "add  result->" + result);
                    if (!ResultLog.getInstance(LoadSaleActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                buyNum = Integer.parseInt(object.getString("totline"));
                                Log.d("TAG", object.getString("totline"));
                                int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                                viewShop.getLocationInWindow(start_location);// 这是获取购物车图片在屏幕的X、Y坐标（这也是动画开始的坐标）
                                buyImg = new ImageView(LoadSaleActivity.this);// buyImg是动画的图片，我的是一个小球（R.drawable.sign）
                                buyImg.setImageResource(R.drawable.sign);// 设置buyImg的图片
                                setAnim(buyImg, start_location);// 开始执行动画
                            } else {
                                Toast.makeText(LoadSaleActivity.this, "商品已经添加", Toast.LENGTH_SHORT).show();
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
     * adapter
     */
    class LoadAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataLists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(LoadSaleActivity.this).inflate(R.layout.item_load_sale_lv, parent, false);
                viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.loading_item_bg);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.loading_item_iv_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.loading_item_tv_name);
                viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.loading_item_tv_info);
                viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.loading_item_tv_price);
                viewHolder.tvUnitName = (TextView) convertView.findViewById(R.id.loading_item_tv_unitName);
                viewHolder.ivShop = (ImageView) convertView.findViewById(R.id.loading_item_iv_add);
                viewHolder.llShop = (LinearLayout) convertView.findViewById(R.id.loading_ll_car_load);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String path = dataLists.get(position).get("url").toString();
            imageLoader.displayImage(path,viewHolder.ivIcon,options);
            viewHolder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
            viewHolder.tvName.setText((String)dataLists.get(position).get("name"));
            viewHolder.tvInfo.setText((String)dataLists.get(position).get("info"));
            viewHolder.tvPrice.setText((String)dataLists.get(position).get("price"));
            viewHolder.tvUnitName.setText((String)dataLists.get(position).get("unitName"));

            viewHolder.llShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = dataLists.get(position).get("id").toString();
                    MyThread.getInstance(LoadSaleActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(LoadSaleActivity.this).ChooseShopCarThread(handler,"1",Constant.ADD_SALE_SHOP_CAR,id,"A","LS001","","");
                    viewShop = v;
                }
            });
            return convertView;
        }
        class ViewHolder {
            ImageView ivBg;
            ImageView ivIcon;
            TextView tvName;
            TextView tvInfo;
            TextView tvPrice;
            TextView tvUnitName;
            ImageView ivShop;
            LinearLayout llShop;
        }
    }

    /**
     * 购买商品种类
     */
    private void getBuyNumber() {
        buyNumView.setText(buyNum + "");
        buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        buyNumView.show();
    }


    /**
     * 创建动画层
     * @return
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * layout
     * @param vg
     * @param view
     * @param location
     * @return
     */
    private View addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    /**
     * 动画
     * @param v
     * @param start_location
     */
    private void setAnim(final View v, int[] start_location) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v,
                start_location);
        int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
        shopCart.getLocationInWindow(end_location);// shopCart是那个购物车

        // 计算位移
        int endX = 0 - start_location[0] + 50;// 动画位移的X坐标
        int endY = end_location[1] - start_location[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                getBuyNumber();
            }
        });
    }

    /**
     * 返回键
     * @param keyCode keyCode
     * @param event KeyEvent
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent = new Intent(LoadSaleActivity.this, LoadingActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
