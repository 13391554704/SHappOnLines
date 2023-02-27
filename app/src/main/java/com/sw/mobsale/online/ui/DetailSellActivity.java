package com.sw.mobsale.online.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailSellActivity extends BaseActivity implements View.OnClickListener{
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>(); //商品数据源
    private ListView lvProduct;  //listview
    private TextView tvCustomerName, tvAddress, tvOrderNum,tvPhone,tvPay; //买家名，地址，电话，订单号，支付方式
    private LinearLayout llAddress,llPhone,llMoney;//地址行 用于地图导航  电话行 用于拨号   结算行  用于付款
    private Button btnFinish;//完成订单 已付款
    private TextView tvTotalNumber,tvTotalPrice; //总计数量 总计价格
    private RelativeLayout rlCount;//结算按钮 未付款
    private MyHandler handler; //处理消息
    private ImageView ivTop;//置顶图标
    private TextView tvTitle,tvRefuse; //head文件 标题
    private RelativeLayout rlBack; //head文件 返回
    private DisplayImageOptions options;   //ImageLoader options
    private ImageLoader imageLoader;//ImageLoader  imageLoader
    private Intent intent;//intent
    private String pay,address,orderNum,orderId,phone,orderType,orderFrom;//支付方式  地址 订单号  手机号  订单
    private double Latitude,Longitude; // 经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sell);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvCustomerName = (TextView) findViewById(R.id.detail_sell_tv_store);
        tvAddress = (TextView) findViewById(R.id.detail_sell_address_tv);
        tvOrderNum = (TextView) findViewById(R.id.detail_sell_number_tv);
        tvPhone = (TextView) findViewById(R.id.detail_sell_phone_tv);
        tvPay = (TextView) findViewById(R.id.detail_sell_pay_tv);
        llAddress = (LinearLayout) findViewById(R.id.detail_sell_address_ll);
        llPhone = (LinearLayout) findViewById(R.id.detail_sell_phone_ll);
        lvProduct = (ListView) findViewById(R.id.detail_sell_lv);
        //总数量 总价格
        tvTotalNumber = (TextView) findViewById(R.id.detail_sell_ll_tv_number);
        tvTotalPrice = (TextView) findViewById(R.id.detail_sell_tv_price);
        rlCount = (RelativeLayout) findViewById(R.id.detail_sell_ll_btn_count);
        ivTop = (ImageView) findViewById(R.id.detail_sell_iv_gototop);
        llMoney = (LinearLayout) findViewById(R.id.detail_sell_ll_money);
        btnFinish = (Button) findViewById(R.id.detail_sell_btn);
        //head
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("订单配送");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvRefuse = (TextView) findViewById(R.id.head_title_gone);
        tvRefuse.setText("拒收");
        tvRefuse.setVisibility(View.VISIBLE);
        handler = new MyHandler();
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
        getIntentDate();
        MyThread.getInstance(DetailSellActivity.this).OrderDetailThread(handler,Constant.LOAD_ORDER_ROUTE_PATH_ONLINE,orderNum,orderId,orderFrom);
    }

    /**
     * 获取界面传递数据
     */
    private void getIntentDate() {
        Intent intent = getIntent();
        orderNum= intent.getStringExtra("orderNo");
        orderId = intent.getStringExtra("orderId");
        orderType= intent.getStringExtra("orderType");
        orderFrom= intent.getStringExtra("orderFrom");
        tvOrderNum.setText(orderNum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加监听事件
        rlCount.setOnClickListener(this);
        tvRefuse.setOnClickListener(this);
        ivTop.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        llAddress.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
    }

    /**
     * 将位置信息转化为经纬度信息
     * @param address
     * @return
     */
    public void getLat(String address){
        GeoCoder geoCoder = GeoCoder.newInstance();
        int i = address.indexOf("市") + 1;
        String city = address.substring(0,i);
        GeoCodeOption mGeoCodeOption = new GeoCodeOption();
        mGeoCodeOption.address(address);
        mGeoCodeOption.city(city);
        Log.d("TAG","city->"+city);
        Log.d("TAG","address->"+address);
        geoCoder.geocode(mGeoCodeOption);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                    Latitude =39.915;
                    Longitude = 116.404;
                }else {
                    Latitude = geoCodeResult.getLocation().latitude;
                    Longitude = geoCodeResult.getLocation().longitude;
                    Log.d("TAG", "LON->" + Longitude + " " + Longitude);
                }
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //进入导航
            case R.id.detail_sell_address_ll:
                Log.d("TAG","LAN->"+ Longitude + Longitude);
                Uri uri  = Uri.parse("geo:"+Latitude+","+Longitude+"?q="+address+"");
                Intent i1 = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i1);
                break;
            //联系电话
            case R.id.detail_sell_phone_ll:
                Log.d("TAG",phone);
                if (!TextUtils.isEmpty(phone))
                    intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + phone));
                else
                    intent = new Intent(Intent.ACTION_CALL_BUTTON);
                startActivity(intent);
                break;
            //已付款
            case R.id.detail_sell_btn:
                SharedPreferences sdf = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userName = sdf.getString("userCode","");
                String userPwd = sdf.getString("password","");
                String phoneCode =sdf.getString("phoneCode","");
                MyThread.getInstance(DetailSellActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                MyThread.getInstance(DetailSellActivity.this).PayThread(handler, orderNum, "0", orderType, "S");
                break;
            //未付款
            case R.id.detail_sell_ll_btn_count:
                intent= new Intent(DetailSellActivity.this, PayModelActivity.class);
                intent.putExtra("orderNo", orderNum);
                intent.putExtra("orderType", orderType);
                intent.putExtra("orderFrom", orderFrom);
                intent.putExtra("totalPrice", tvTotalPrice.getText().toString());
                intent.putExtra("phone",phone);
                startActivity(intent);
                finish();
                break;
            //置顶
            case R.id.detail_sell_iv_gototop:
                if (!lvProduct.isStackFromBottom()) {
                    lvProduct.setStackFromBottom(true);
                }
                lvProduct.setStackFromBottom(false);
                break;
            //返回
            case R.id.head_rl_title:
//                intent = new Intent(DetailSellActivity.this,MainActivity.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.head_title_gone:
                Toast.makeText(DetailSellActivity.this,"拒收!",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * handler
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //商品列表
                case Constant.ORDER_DETAIL:
                    String result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(DetailSellActivity.this).getNoRes(result)) {
                        Log.d("TAG", "detail result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("rows");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = array.getJSONObject(i);
                                if (jo.get("itemcode").equals("") || "null".equals(jo.get("itemcode").toString())) {
                                    continue;
                                }
                                if (jo.get("itemname").equals("") || "null".equals(jo.get("itemname").toString())) {
                                    continue;
                                }
                                if (jo.get("itemspec").equals("") || "null".equals(jo.get("itemspec").toString())) {
                                    continue;
                                }
                                if (jo.get("qty").equals("") || "null".equals(jo.get("qty").toString())) {
                                    continue;
                                }
                                if (jo.get("price").equals("") || "null".equals(jo.get("price").toString())) {
                                    continue;
                                }
                                if (jo.get("unitdesc").equals("") || "null".equals(jo.get("unitdesc").toString())) {
                                    continue;
                                }
                                if (jo.get("qtysum").equals("") || "null".equals(jo.get("qtysum").toString())) {
                                    continue;
                                }
                                if (jo.get("itemurl").equals("") || "null".equals(jo.get("itemurl").toString())) {
                                    continue;
                                }
                                String itemCode = (String) jo.get("itemcode");
                                String itemName = (String) jo.get("itemname");
                                String itemSpec = (String) jo.get("itemspec");
                                String unitName = (String) jo.get("unitdesc");
                                String qty = (String) jo.get("qty");
                                String itemPrice = (String) jo.get("price");
                                int a = qty.indexOf(".");
                                qty = qty.substring(0, a);
                                int b = itemPrice.indexOf(".") + 3;
                                itemPrice = itemPrice.substring(0, b);
                                String url = jo.getString("itemurl");
                                String qtySum = jo.getString("qtysum");
                                int c = qtySum.indexOf(".");
                                qtySum = qtySum.substring(0, c);
                                String amt = jo.getString("ordersumamt");
                                int d = amt.indexOf(".") + 3;
                                amt = amt.substring(0, d);
                                String customerName = jo.getString("buyername");
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("bg", Constant.image1[i % 5]);
                                map.put("url", url);
                                map.put("itemCode", itemCode);
                                map.put("name", itemName);
                                map.put("info", itemSpec);
                                map.put("unitName", unitName);
                                map.put("number", qty);
                                map.put("price", itemPrice);
                                map.put("customerName", customerName);
                                map.put("qtySum", qtySum);
                                map.put("amt", amt);
                                dataLists.add(map);
                                address = jo.getString("receiveaddress");
                                phone = jo.getString("mobileno");
                                pay = jo.getString("iscashondelivery");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvCustomerName.setText(dataLists.get(0).get("customerName").toString());
                        tvTotalPrice.setText(dataLists.get(0).get("amt").toString());
                        tvTotalNumber.setText(dataLists.get(0).get("qtySum").toString());
                        tvAddress.setText(address);
                        tvPhone.setText(phone);
                        if (("N").equals(pay)) {
                            tvPay.setText("已付款");
                            btnFinish.setVisibility(View.VISIBLE);
                            llMoney.setVisibility(View.GONE);
                        } else {
                            tvPay.setText("货到付款");
                            llMoney.setVisibility(View.VISIBLE);
                            btnFinish.setVisibility(View.GONE);
                        }
                        Log.d("TAG", dataLists.toString());
                        //绑定适配器
                        DetailSellAdapter sellAdapter = new DetailSellAdapter();
                        lvProduct.setAdapter(sellAdapter);
                        getLat(address);
                    }
                    break;
                //确认在线
                case Constant.FORCE_DOWN:
                    String down = msg.getData().getString("result");
                    if (down == null || ("").equals(down)){
                        return;
                    }
                    try {
                        Log.d("TAG","force down->"+ down);
                        JSONObject object = new JSONObject(down);
                        String message = object.getString("errorMessage");
                        if ("err".equals(message)){
                            ResultLog.getInstance(DetailSellActivity.this).getDialog(DetailSellActivity.this);
                        }else{
                            MyThread.getInstance(DetailSellActivity.this).PayThread(handler,Constant.FINISH_PAY,orderNum,orderFrom,"0", orderType, "S","");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //已付款
                case Constant.FINISH_PAY:
                    String finish = msg.getData().getString("result");
                    if (finish == null || ("").equals(finish)) {
                      return;
                    }
                    try {
                        Log.d("TAG", "pay finish result->" + finish);
                        JSONObject object = new JSONObject(finish);
                        String state = object.getString("errorMessage");
                        if ("ok".equals(state)) {
                            intent = new Intent(DetailSellActivity.this, DetailActivity.class);
                            intent.putExtra("orderNo", orderNum);
                            intent.putExtra("orderType", orderType);
                            intent.putExtra("orderFrom", orderFrom);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DetailSellActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 页面数据listview适配器
     */
    class DetailSellAdapter extends BaseAdapter {

        //构造方法
        public DetailSellAdapter() {

        }

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
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(DetailSellActivity.this).inflate(R.layout.detail_lv_item, parent, false);
                viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.fg_sell_item_bg);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.fg_sell_item_iv);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.fg_sell_item_tvname);
                viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.fg_sell_item_tvinfo);
                viewHolder.tvShow = (TextView) convertView.findViewById(R.id.fg_sell_item_tv_show);
                viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.fg_sell_item_tvprice);
                viewHolder.tvUnitName = (TextView) convertView.findViewById(R.id.fg_sell_item_tvunitName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
            final String path = dataLists.get(position).get("url").toString();
            imageLoader.displayImage(path,viewHolder.ivIcon,options);
            viewHolder.tvName.setText((String) dataLists.get(position).get("name"));
            viewHolder.tvInfo.setText((String) dataLists.get(position).get("info"));
            viewHolder.tvShow.setText((String) dataLists.get(position).get("number"));
            viewHolder.tvPrice.setText((String) dataLists.get(position).get("price"));
            viewHolder.tvUnitName.setText((String) dataLists.get(position).get("unitName"));
            return convertView;
        }
        public class ViewHolder {
            public ImageView ivBg;
            public ImageView ivIcon;
            public TextView tvName;
            public TextView tvInfo;
            public TextView tvPrice;
            public TextView tvShow;
            public TextView tvUnitName;
        }
    }

    /**
     * 返回键
     * @param keyCode keycode标识
     * @param event KeyEvent
     * @return 返回true false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(DetailSellActivity.this,MainActivity.class);
//            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
