package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
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
import android.widget.Toast;
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
 * Created by Sxs
 */
public class SellActivity extends BaseActivity implements View.OnClickListener {
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();//商品数据源
    private TextView tvTotalNumber,tvTotalPrice;  //总计数量,总计价格
    private RelativeLayout rlCount;//结算按钮
    //listview
    private SaleSwipeView mOldSwipeView;
    private SaleSwipeListView lvProduct;
    private SellAdapter sellAdapter; //适配器 adapter
    //imageLoader
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private MyHandler handler; //处理消息handler
    private String result;//结果
    //head
    private TextView tvTitle;//head title
    private RelativeLayout rlBack, rlNoData; //head back , 无数据
    private Intent intent;
    private String customerName,customerAddress,phone; //获取用户 ,显示门店地址
    private View footerView;  //listview footer
    private Boolean flag = true;//标识
    //private String userName,userPwd,phoneCode;
    private String id,showNumber;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     * */
    private void initView() {
        //实例化
        lvProduct = (SaleSwipeListView) findViewById(R.id.sell_lv);
        tvTotalNumber = (TextView) findViewById(R.id.sell_tv_number);
        tvTotalPrice = (TextView) findViewById(R.id.sell_tv_price);
        rlCount = (RelativeLayout) findViewById(R.id.sell_rl_jiesuan);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("铺货");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlNoData = (RelativeLayout) findViewById(R.id.retail_rl_detail);
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
        //获取数据
        getIntentData();
        sellAdapter = new SellAdapter();
        handler = new MyHandler();
        //购物车数据 status D
        MyThread.getInstance(SellActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_D,"","D","","");
    }
    /**
     * 获取界面传递数据
     */
    private void getIntentData(){
        Intent intent = getIntent();
        customerName = intent.getStringExtra("name");
        customerAddress = intent.getStringExtra("address");
        phone = intent.getStringExtra("phone");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加监听事件
        rlCount.setOnClickListener(this);
        rlBack.setOnClickListener(this);
    }

    /**
     * button响应事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //结算按钮
            case R.id.sell_rl_jiesuan:
//                intent = new Intent(SellActivity.this, IndentActivity.class);
//                intent.putExtra("store",customerName);//tvStore.getText().toString()
//                intent.putExtra("address",customerAddress);//tvAddress.getText().toString()
//                intent.putExtra("phone",phone);
//                startActivity(intent);
                getActivity(IndentActivity.class);
                break;
            //返回
            case R.id.head_rl_title:
//                intent = new Intent(SellActivity.this, RetailActivity.class);
//                intent.putExtra("customerName",customerName);
//                intent.putExtra("address",customerAddress);
//                intent.putExtra("phone",phone);
//                startActivity(intent);
                getActivity(RetailActivity.class);
                finish();
                break;
        }
    }

    /**
     * 跳转
     * @param activity class
     */
    private void getActivity(Class activity){
        intent = new Intent(SellActivity.this, activity);
        intent.putExtra("customerName",customerName);
        intent.putExtra("address",customerAddress);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }
    /**
     * handler
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //产品列表
                case Constant.SALE_RETAIL_D:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " sell result->" + result);
                    if (!ResultLog.getInstance(SellActivity.this).getNoRes(result)) {
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(SellActivity.this).RetailDetailLog(result);
                        Log.d("TAG", "sell ->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            if (flag) {
                                flag = false;
                                if (dataLists.size() > 6) {
                                    footerView = View.inflate(SellActivity.this, R.layout.item_lv_footer, null);
                                    lvProduct.addFooterView(footerView, null, false);
                                }
                                rlNoData.setVisibility(View.GONE);
                                lvProduct.setAdapter(sellAdapter);
                            } else {
                                lvProduct.removeFooterView(footerView);
                                if (dataLists.size() > 6) {
                                    footerView = View.inflate(SellActivity.this, R.layout.item_lv_footer, null);
                                    lvProduct.addFooterView(footerView, null, false);
                                }
                                rlNoData.setVisibility(View.GONE);
                                sellAdapter.notifyDataSetChanged();
                            }
                            String qty = dataLists.get(0).get("sumQty").toString();
                            String amt = dataLists.get(0).get("sumAmt").toString();
                            tvTotalNumber.setText(qty);
                            tvTotalPrice.setText(amt);
                        } else {
                            lvProduct.removeFooterView(footerView);
                            sellAdapter.notifyDataSetChanged();
                            rlNoData.setVisibility(View.VISIBLE);
                            tvTotalNumber.setText("0");
                            tvTotalPrice.setText("0.00");
                        }
                    }
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(SellActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(SellActivity.this).getDialog(SellActivity.this);
                            } else {
                                if (index == 1) {
                                    MyThread.getInstance(SellActivity.this).RetailThread(handler, showNumber, Constant.SALE_RETAIL_U, id, "U", "","");
                                } else if (index == 2) {
                                    MyThread.getInstance(SellActivity.this).RetailThread(handler, showNumber, Constant.SALE_RETAIL_K, id, "K", "","");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //更新数量
                case Constant.SALE_RETAIL_U:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(SellActivity.this).getNoRes(result)) {
                        Log.d("TAG", "update  result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                MyThread.getInstance(SellActivity.this).RetailThread(handler, "1", Constant.SALE_RETAIL_D, "", "D", "","");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //删除
                case Constant.SALE_RETAIL_K:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(SellActivity.this).getNoRes(result)) {
                        Log.d("TAG", "delete  result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                MyThread.getInstance(SellActivity.this).RetailThread(handler, "1", Constant.SALE_RETAIL_D, "", "D", "","");
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
     * 页面数据listview适配器
     */
    class SellAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder viewHolder ;
            SaleSwipeView swipeView = (SaleSwipeView) convertView;
            final View view = LayoutInflater.from(SellActivity.this).inflate(R.layout.item_sell_lv, parent,false);
            swipeView = new SaleSwipeView(SellActivity.this);
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
            //imageLoader.displayImage(path,viewHolder.ivIcon);
            viewHolder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
            viewHolder.tvName.setText((String)dataLists.get(position).get("name"));
            viewHolder.tvInfo.setText((String)dataLists.get(position).get("info"));
            viewHolder.tvPrice.setText((String)dataLists.get(position).get("price"));
            viewHolder.tvShow.setText((String)dataLists.get(position).get("saleNum"));
            viewHolder.tvNumber.setText((String)dataLists.get(position).get("number"));
            viewHolder.tvUnitName.setText((String)dataLists.get(position).get("unitName"));
            viewHolder.leftView.setText("删除");
            viewHolder.checkBox.setVisibility(View.GONE);
            final String qty = dataLists.get(position).get("saleNum").toString();
            //数量加一
            viewHolder.btnJia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否为选中状态
                    //获取用户点击的数量
                    String show = viewHolder.tvShow.getText().toString();
                    String number = viewHolder.tvNumber.getText().toString();
                    int count = Integer.parseInt(number);
                    if (show == null || show.equals("")) {
                        show = "0";
                    }
                    int result = Integer.parseInt(show);
                    if (result < count) {
                        result++;
                        //设置用户显示的数量以及剩余的数量
                        viewHolder.tvShow.setText(String.valueOf(result));
                        //获取用户显示的数量
                        id = dataLists.get(position).get("id").toString();
                        showNumber = viewHolder.tvShow.getText().toString();
                        index = 1;
                        MyThread.getInstance(SellActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                        MyThread.getInstance(SellActivity.this).RetailThread(handler, showNumber, Constant.SALE_RETAIL_U, id, "U", "","LS001", "", "");
                    }else{
                        Toast.makeText(SellActivity.this,"不能超过库存量!",Toast.LENGTH_SHORT).show();
                    }
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
                        id = dataLists.get(position).get("id").toString();
                        viewHolder.tvShow.setText(String.valueOf(result));
                        showNumber = viewHolder.tvShow.getText().toString();
                        index = 1;
                        MyThread.getInstance(SellActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                        MyThread.getInstance(SellActivity.this).RetailThread(handler,showNumber, Constant.SALE_RETAIL_U,id,"U","","LS001","","");
                        return;
                    }
                    //最小值为1
                    if (result == 1) {
                        return;
                    }
                }
            });
            //输入数量
            viewHolder.tvShow.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String number = s.toString();
                    if (number == null || ("").equals(number)) {
                    } else {
                        int count = Integer.parseInt(number);
                        String totalNum = viewHolder.tvNumber.getText().toString();
                        int totalNo = Integer.parseInt(totalNum);
                        if (count > totalNo) {
                            Toast.makeText(SellActivity.this, "不能超过库存量!", Toast.LENGTH_SHORT).show();
                            viewHolder.tvShow.setText(qty);
                        }
                    }
                    CharSequence text = viewHolder.tvShow.getText();
                    if ( text instanceof Spannable) {
                        Spannable spanText = (Spannable) text;
                        //移动到字体后面
                        Selection.setSelection(spanText, text.length());
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
                            // 页面保存输入量
                            id = dataLists.get(position).get("id").toString();
                            Log.d("TAG","unfouce->"+count);
                            //更新销售临时表数据
                            index = 1;
                            MyThread.getInstance(SellActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                            MyThread.getInstance(SellActivity.this).RetailThread(handler,count, Constant.SALE_RETAIL_U,id,"U","","LS001","","");
                        }
                    }
                }
            });

            viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = dataLists.get(position).get("id").toString();
                    //String qty = viewHolder.tvShow.getText().toString();
                    showNumber = viewHolder.tvShow.getText().toString();
                    Log.d("TAG","shop->" + qty);
                    index = 2;
                    MyThread.getInstance(SellActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(SellActivity.this).RetailThread(handler,qty, Constant.SALE_RETAIL_K,id,"K","","LS001","","");
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
                tvUnitName = (TextView) convertView.findViewById(R.id.fg_sell_item_tvunitName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 返回键
     * @param keyCode keyCode
     * @param event event
     * @return boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent = new Intent(SellActivity.this, RetailActivity.class);
            intent.putExtra("customerName",customerName);
            intent.putExtra("address",customerAddress);
            intent.putExtra("phone",phone);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
