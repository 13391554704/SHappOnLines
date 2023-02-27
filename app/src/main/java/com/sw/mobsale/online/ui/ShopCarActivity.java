package com.sw.mobsale.online.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.OnSlideListener;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.SaleSwipeListView;
import com.sw.mobsale.online.util.SaleSwipeView;
import com.sw.mobsale.online.util.ScreenManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 铺货单
 */
public class ShopCarActivity extends BaseActivity {
    //界面
    private SaleSwipeView mOldSwipeView;
    private SaleSwipeListView lvPro;//lv
    private RelativeLayout btnSubmit;//buton 生成铺货单
    private TextView tvTitle,tvNumber,tvPrice;// head title  商品数量  商品价格
    private RelativeLayout rlBack;//head back
    private List<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();//数据
    private String result;//json
    //imageLoader
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private RelativeLayout rlNo;//提示空
    private MyHandler handler;  //handler
    private int upOrDe = 0;//upOrDe 标识 up更新 de 删除
    private View footerView;   //listview footer
    private LoadAdapter adapter;//adapter
    private boolean index = true;//标识是否是第一次展示商品列表
    private String id,showNumber;
    //标识 更新 =1  删除=2 生成=3
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        lvPro = (SaleSwipeListView) findViewById(R.id.shop_car_lv);
        btnSubmit = (RelativeLayout) findViewById(R.id.shop_car_btn_submit);
        tvNumber = (TextView) findViewById(R.id.shop_car_tv_number);
        tvPrice = (TextView) findViewById(R.id.shop_car_tv_price);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvTitle.setText("生成铺货单");
        rlNo = (RelativeLayout) findViewById(R.id.retail_rl_detail);
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
        handler = new MyHandler();
        adapter = new LoadAdapter();
        //footerView
        footerView = View.inflate(ShopCarActivity.this, R.layout.item_lv_footer, null);
        //购物车数据 status D
        MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,"",Constant.SALE_SHOP_CAR,"","D");
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 3;
                MyThread.getInstance(ShopCarActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                upOrDe = 1;
//                MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,"",Constant.ADD_SALE_SHOP_CAR,"","S","LS001","","");
            }
        });
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopCarActivity.this, LoadSaleActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
            final ViewHolder viewHolder ;
            SaleSwipeView swipeView = (SaleSwipeView) convertView;
            View view = LayoutInflater.from(ShopCarActivity.this).inflate(R.layout.item_sell_lv, parent,false);
            swipeView = new SaleSwipeView(ShopCarActivity.this);
            swipeView.setContentItemView(view);
            viewHolder = new ViewHolder(swipeView);
            swipeView.setOnSlideListener(new OnSlideListener() {

                @Override
                public void onSlide(View view, int status) {

                    if (mOldSwipeView != null && mOldSwipeView != view) {
                        mOldSwipeView.shrink();
                    }

                    if (status == SLIDE_STATUS_ON) {
                        mOldSwipeView = (SaleSwipeView) view;
                    }
                }
            });
            swipeView.setTag(viewHolder);
            if (SaleSwipeListView.mSwipeView != null) {
                SaleSwipeListView.mSwipeView.shrink();
            }
            final String path = dataLists.get(position).get("url").toString();
            imageLoader.displayImage(path,viewHolder.ivIcon,options);
            viewHolder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
            viewHolder.tvName.setText((String)dataLists.get(position).get("name"));
            viewHolder.tvInfo.setText((String)dataLists.get(position).get("info"));
            viewHolder.tvPrice.setText((String)dataLists.get(position).get("price"));
            viewHolder.tvShow.setText((String)dataLists.get(position).get("number"));
            viewHolder.tvUnitName.setText((String)dataLists.get(position).get("unitName"));
            viewHolder.leftView.setText("删除");
            viewHolder.tvNumber.setVisibility(View.GONE);
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.tvRes.setVisibility(View.GONE);

            //数量加一
            viewHolder.btnJia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取用户点击的数量
                    String show = viewHolder.tvShow.getText().toString();
                    if (show == null || show.equals("")) {
                        show = "0";
                    }
                    int result = Integer.parseInt(show);
                    result++;
                    //设置用户显示的数量以及剩余的数量
                    viewHolder.tvShow.setText(String.valueOf(result));
                    //获取用户显示的数量
                     id = dataLists.get(position).get("id").toString();
                    showNumber = viewHolder.tvShow.getText().toString();
                    status = 1;
                    MyThread.getInstance(ShopCarActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                        MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,showNumber,Constant.ADD_SALE_SHOP_CAR,id,"U","LS001","","");
                }

            });
            //数量减一
            viewHolder.btnJian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String show = viewHolder.tvShow.getText().toString();
                    if (show == null || show.equals("")) {
                       return;
                    }
                    int result = Integer.parseInt(show);
                    if (result > 1) {
                        //剩余数量减一，显示数量加一
                        result--;
                        viewHolder.tvShow.setText(String.valueOf(result));
                        id = dataLists.get(position).get("id").toString();
                        showNumber = viewHolder.tvShow.getText().toString();
                        status = 1;
                        MyThread.getInstance(ShopCarActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                        MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,showNumber,Constant.ADD_SALE_SHOP_CAR,id,"U","LS001","","");
                        return;
                    }
                    //数量为1提示为最低数量
                    if (result == 1) {
                        return;
                    }
                }

            });

            //根据光标监听Edittext数值变化
            viewHolder.tvShow.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                String before;
                String count;
                @Override
                public void onFocusChange(View v,boolean hasFocus) {
                    if (hasFocus){
                        before = viewHolder.tvShow.getText().toString();
                        Log.d("TAG","fouce->"+before);
                    }else{
                        count = viewHolder.tvShow.getText().toString();
                        if(count ==null||("").equals(count)){
                            count ="1";
                            viewHolder.tvShow.setText("1");
                        }
                        if (count.equals(before)){
                            return;
                        }else{
                            id = dataLists.get(position).get("id").toString();
                            showNumber = viewHolder.tvShow.getText().toString();
                            status = 1;
                            MyThread.getInstance(ShopCarActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                            MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,count,Constant.ADD_SALE_SHOP_CAR,id,"U","LS001","","");
                            Log.d("TAG","unfouce->"+count);
                        }
                    }
                }
            });

            viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = dataLists.get(position).get("id").toString();
                    showNumber = viewHolder.tvShow.getText().toString();
                    status = 2;
                    Log.d("TAG","shop->" + showNumber);
                    MyThread.getInstance(ShopCarActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler,qty,Constant.ADD_SALE_SHOP_CAR,id,"K","LS001","","");
                }
            });

            return swipeView;
        }
        class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            TextView tvInfo;
            TextView tvPrice;
            TextView tvUnitName;
            TextView tvNumber;
            TextView tvRes;
            TextView leftView;
            ImageView ivBg;
            CheckBox checkBox;
            Button btnJian;
            Button btnJia;
            EditText tvShow;
            ViewHolder(View convertView){
                leftView = (TextView) convertView.findViewById(R.id.tv_left);
                ivBg = (ImageView) convertView.findViewById(R.id.fg_sell_item_bg);
                ivIcon = (ImageView) convertView.findViewById(R.id.fg_sell_item_iv);
                tvName = (TextView) convertView.findViewById(R.id.fg_sell_item_tvname);
                tvInfo = (TextView) convertView.findViewById(R.id.fg_sell_item_tvinfo);
                btnJian = (Button) convertView.findViewById(R.id.fg_sell_item_btn_jian);
                btnJia = (Button) convertView.findViewById(R.id.fg_sell_item_btn_jia);
                tvShow = (EditText) convertView.findViewById(R.id.fg_sell_item_tv_show);
                tvPrice = (TextView) convertView.findViewById(R.id.fg_sell_item_tvprice);
                checkBox = (CheckBox) convertView.findViewById(R.id.fg_sell_item_checkbox);
                tvNumber = (TextView) convertView.findViewById(R.id.fg_sell_item_tvnumber);
                tvRes = (TextView) convertView.findViewById(R.id.fg_sell_item_tv_res);
                tvUnitName = (TextView) convertView.findViewById(R.id.fg_sell_item_tvunitName);
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
                //商品列表
                case Constant.SALE_SHOP_CAR:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(ShopCarActivity.this).getNoRes(result)) {
                        Log.d("TAG", "shop_car result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(ShopCarActivity.this).getLoadSale(result);
                        if (dataLists.size() > 0) {
                            if (index) {
                                index = false;
                                if (dataLists.size() > 6) {
                                    lvPro.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                lvPro.setAdapter(adapter);
                            } else {
                                lvPro.removeFooterView(footerView);
                                if (dataLists.size() > 6) {
                                    lvPro.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                            String qty = dataLists.get(0).get("sumQty").toString();
                            String amt = dataLists.get(0).get("sumAmt").toString();
                            tvNumber.setText(qty);
                            tvPrice.setText(amt);
                        } else {
                            tvNumber.setText("0");
                            tvPrice.setText("0.00");
                            lvPro.removeFooterView(footerView);
                            adapter.notifyDataSetChanged();
                            rlNo.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(ShopCarActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(ShopCarActivity.this).getDialog(ShopCarActivity.this);
                            } else {
                                if (status == 1) {
                                    MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler, showNumber, Constant.ADD_SALE_SHOP_CAR, id, "U");
                                } else if (status == 2) {
                                    MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler, showNumber, Constant.ADD_SALE_SHOP_CAR, id, "K");
                                } else if (status == 3) {
                                    upOrDe = 1;
                                    MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler, "", Constant.ADD_SALE_SHOP_CAR, "", "S");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //生成零售单  更新 删除
                case Constant.ADD_SALE_SHOP_CAR:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(ShopCarActivity.this).getNoRes(result)) {
                        Log.d("TAG", "update/delete  result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                if (upOrDe == 0) {
                                    MyThread.getInstance(ShopCarActivity.this).ChooseShopCarThread(handler, "", Constant.SALE_SHOP_CAR, "", "D");
                                } else if (upOrDe == 1) {
                                    Intent intent = new Intent(ShopCarActivity.this, LoadingActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
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
     * 返回键
     * @param keyCode keyCode
     * @param event event
     * @return  true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ShopCarActivity.this, LoadSaleActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
