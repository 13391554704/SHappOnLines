package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.IndentAdapter;
import com.sw.mobsale.online.adapter.LoadingAdapter;
import com.sw.mobsale.online.util.CaptureActivity;
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

/**
 * 商品明细 （订单  ，零售）
 */
public class CarLoadActivity extends BaseActivity {
    //商品数据源
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();
    //listview
    private ListView lvProduct;
    //界面 店名 地址 订单号
    private TextView tvCustomerName, tvAddress, tvOrderNum;
    private LinearLayout llAddress,llNumber,llTotal;
    //总计数量 总计价格
    private TextView tvTotalNumber,tvTotalPrice;
    //确定  无数据
    private RelativeLayout rlConfirm,rlNo;
    private TextView tvNumber;
    //head文件
    private TextView tvTitle;
    private RelativeLayout rlBack;
    //商铺名 地址 订单号 订单来源 总量 总价
    private String name,address,orderNum,orderId,orderFrom,qtySum,amt;
    //server返回结果
    private String result;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_load);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        lvProduct = (ListView) findViewById(R.id.car_load_lv);
        llAddress = (LinearLayout) findViewById(R.id.car_load_address_ll);
        llNumber = (LinearLayout) findViewById(R.id.car_load_number_ll);
        llTotal = (LinearLayout) findViewById(R.id.car_load_bottom_ll);
        tvCustomerName = (TextView) findViewById(R.id.car_load_name_tv);
        tvAddress = (TextView) findViewById(R.id.car_load_address_tv);
        tvOrderNum = (TextView) findViewById(R.id.car_load_order_tv);
        tvTotalNumber = (TextView) findViewById(R.id.car_load_tv_number);
        tvTotalPrice = (TextView) findViewById(R.id.car_load_tv_price);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlConfirm = (RelativeLayout) findViewById(R.id.detail_sell_ll_btn_count);
        tvNumber = (TextView) findViewById(R.id.detail_sell_ll_btn_number);
        handler = new MyHandler();
        rlNo = (RelativeLayout) findViewById(R.id.retail_rl_detail);
        getIntentData();
    }

    /**
     * intent数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        //零售汇总
        if("saleAll".equals(name)){
            llAddress.setVisibility(View.GONE);
            llNumber.setVisibility(View.GONE);
            llTotal.setVisibility(View.VISIBLE);
            tvCustomerName.setText("零售汇总");
            tvTitle.setText("零售汇总");
            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            MyThread.getInstance(CarLoadActivity.this).ChooseShopCarThread(handler,"",Constant.LOAD_SALE_ALL,"","T");
            return;
        }
        //挂单明细
        if (("unfinish").equals(name)){
            llAddress.setVisibility(View.GONE);
            llNumber.setVisibility(View.GONE);
            llTotal.setVisibility(View.VISIBLE);
            tvCustomerName.setText("挂单明细");
            tvTitle.setText("挂单明细");
            orderNum =  intent.getStringExtra("number");
            orderId =  intent.getStringExtra("id");
            orderFrom =  intent.getStringExtra("orderFrom");
            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            MyThread.getInstance(CarLoadActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_C,"","C",orderNum,orderId);
            return;
        }
        //扫码订单
        if ("codeOrder".equals(name)){
            orderNum = intent.getStringExtra("orderNo");
            orderId =  intent.getStringExtra("id");
            Log.d("TAG",orderNum);
            tvTitle.setText("扫码订单");
            tvOrderNum.setText(orderNum);
            tvNumber.setText("确定");
            MyThread.getInstance(CarLoadActivity.this).OrderDetailThread(handler,Constant.TWO_ORDER_DETAIL_PATH,orderNum,orderId,"");
            rlConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyThread.getInstance(CarLoadActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(CarLoadActivity.this).OrderThread(handler, Constant.ORDER_STATUS_G,"","G",orderNum);
                }
            });
            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CarLoadActivity.this,CaptureActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }else{
            //loading-> carload
            orderNum = intent.getStringExtra("orderNo");
            orderId =  intent.getStringExtra("orderId");
            orderFrom = intent.getStringExtra("orderFrom");
            address = intent.getStringExtra("address");
            amt = intent.getStringExtra("amt");
            tvAddress.setText(address);
            tvOrderNum.setText(orderNum);
            tvTotalPrice.setText(amt);
            String flag = intent.getStringExtra("flag");
            if(("loading").equals(flag)){
                tvTitle.setText("装车单");
                rlBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                MyThread.getInstance(CarLoadActivity.this).OrderDetailThread(handler,Constant.LOAD_ORDER_ROUTE_PATH_ONLINE,orderNum,orderId,orderFrom);
            }else{
                tvTitle.setText("订单明细");
                rlBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                MyThread.getInstance(CarLoadActivity.this).OrderDetailThread(handler,Constant.LOAD_ORDER_DETAIL_PATH,orderNum,orderId,orderFrom);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * handler
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.ORDER_DETAIL:
                    result = msg.getData().getString("result");
                    Log.d("TAG"," detail result->"+result);
                    if(!ResultLog.getInstance(CarLoadActivity.this).getNoRes(result)) {
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
                                } else {
                                    String itemCode = (String) jo.get("itemcode");
                                    String itemName = (String) jo.get("itemname");
                                    String itemSpec = (String) jo.get("itemspec");
                                    String unitName = (String) jo.get("unitdesc");
                                    String qty = (String) jo.get("qty");
                                    String itemPrice = (String) jo.get("price");
                                    //String orderFrom = (String) jo.get("datasource");
                                    int a = qty.indexOf(".");
                                    qty = qty.substring(0, a);
                                    int b = itemPrice.indexOf(".") + 3;
                                    itemPrice = itemPrice.substring(0, b);
                                    String url = jo.getString("itemurl");
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("bg", Constant.image1[i % 5]);
                                    map.put("url", url);
                                    map.put("itemCode", itemCode);
                                    map.put("name", itemName);
                                    map.put("info", itemSpec);
                                    map.put("unitName", unitName);
                                    map.put("number", qty);
                                    map.put("price", itemPrice);
                                    dataLists.add(map);
                                    qtySum = jo.getString("qtysum");
                                    int c = qtySum.indexOf(".");
                                    qtySum = qtySum.substring(0, c);
                                    name = jo.getString("buyername");
                                    address = jo.getString("receiveaddress");
                                    amt = jo.getString("ordersumamt");
                                    int d = amt.indexOf(".") + 3;
                                    amt = amt.substring(0, d);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tvCustomerName.setText(name);
                        tvAddress.setText(address);
                        tvTotalPrice.setText(amt);
                        tvTotalNumber.setText(qtySum);
                        Log.d("TAG", dataLists.toString());
                        lvProduct.setAdapter(new LoadingAdapter(CarLoadActivity.this, dataLists));
                    }
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(CarLoadActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(CarLoadActivity.this).getDialog(CarLoadActivity.this);
                            } else {
                                MyThread.getInstance(CarLoadActivity.this).OrderThread(handler, Constant.ORDER_STATUS_G, "", "G", orderNum, "", "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //订单获取
                case Constant.ORDER_STATUS_G:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "code result->" + result);
                    if(!ResultLog.getInstance(CarLoadActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String state = object.getString("errorMessage");
                            if ("OK".equals(state)) {
                                Intent intent = new Intent(CarLoadActivity.this, CaptureActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                break;
                // 铺货明细
                case Constant.LOAD_SALE_ALL:
                    result = msg.getData().getString("result");
                    Log.d("TAG","result->" +result);
                    if(!ResultLog.getInstance(CarLoadActivity.this).getNoRes(result)) {
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(CarLoadActivity.this).OrderDetailLog(result);
                        Log.d("TAG", "sale all ->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            rlNo.setVisibility(View.GONE);
                            lvProduct.setAdapter(new LoadingAdapter(CarLoadActivity.this, dataLists));
                            String qty = dataLists.get(0).get("sumQty").toString();
                            String amt = dataLists.get(0).get("sumAmt").toString();
                            tvTotalNumber.setText(qty);
                            tvTotalPrice.setText(amt);
                        } else {
                            rlNo.setVisibility(View.VISIBLE);
                            tvTotalNumber.setText("0");
                            tvTotalPrice.setText("0.00");
                        }
                    }
                    break;
                //挂单明细
                case Constant.SALE_RETAIL_C:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(CarLoadActivity.this).getNoRes(result)) {
                        Log.d("TAG", " unfinish result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(CarLoadActivity.this).RetailDetailLog(result);
                        Log.d("TAG", "unfinish data ->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            lvProduct.setAdapter(new IndentAdapter(CarLoadActivity.this, dataLists));
                            String qty = dataLists.get(0).get("sumQty").toString();
                            String totalPrice = dataLists.get(0).get("sumAmt").toString();
                            tvTotalNumber.setText(qty);
                            tvTotalPrice.setText(totalPrice);
                        } else {
                            Toast.makeText(CarLoadActivity.this, "没有商品信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
