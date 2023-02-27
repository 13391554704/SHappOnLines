package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StoreManagerActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private TextView tvTitle,tvNowDate,tvOrder,tvRetail,tvAll;
    private View viewOrder,viewRetail,viewAll;
    private ListView lv;
    //数据源
    private List<Map<String,Object>> dataLists  = new ArrayList<Map<String,Object>>();
    //head 返回
    private RelativeLayout rlBack;
    // 销售类型
    private String orderType;
    //handler
    private MyHandler handler;
    //json
    private String result;
    //adapter
    private FgDetailAdapter adapter;
    //数据为空 显示
    private RelativeLayout rlNo;
    private TextView tvNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_manager);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("销售单据");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvNowDate = (TextView) findViewById(R.id.store_manager_now_date);
        tvAll  = (TextView) findViewById(R.id.store_manager_all);
        tvOrder = (TextView) findViewById(R.id.store_manager_order);
        tvRetail = (TextView) findViewById(R.id.store_manager_retail);
        viewAll = findViewById(R.id.store_manager_gone1);
        viewOrder = findViewById(R.id.store_manager_gone2);
        viewRetail = findViewById(R.id.store_manager_gone3);
        rlNo = (RelativeLayout) findViewById(R.id.retail_rl_detail);
        tvNo = (TextView) findViewById(R.id.detail_rl_content_tv);
        tvNo.setText("无销售信息");
        lv = (ListView) findViewById(R.id.clear_lv_data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(new Date());
        tvNowDate.setText(date);
        handler = new MyHandler();
        orderType = "A";
        MyThread.getInstance(StoreManagerActivity.this).QueryDataThread(handler,"","","","A",Constant.DATA_QUERY_ALL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StoreManagerActivity.this, DetailActivity.class);
                intent.putExtra("orderNo", dataLists.get(position).get("number").toString());
                if (("A").equals(orderType)) {
                    intent.putExtra("orderType",dataLists.get(position).get("saleType").toString());
                }else{
                    intent.putExtra("orderType",orderType);
                }
                intent.putExtra("intent","sale");
                intent.putExtra("orderFrom",dataLists.get(position).get("fromCode").toString());
                startActivity(intent);
            }
        });
        rlBack.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvOrder.setOnClickListener(this);
        tvRetail.setOnClickListener(this);
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
             //销售总信息
             case Constant.DATA_QUERY_ALL:
                 result = msg.getData().getString("result");
                 Log.d("TAG", " store result data->" + result);
                 getData(result,"无销售信息");
                 break;
             //订单销售信息
             case Constant.DATA_QUERY_ORDER:
                 result = msg.getData().getString("result");
                 Log.d("TAG", "order store  result->" + result);
                 getData(result,"无配送业务");
                 break;
             //铺货销售信息
             case Constant.DATA_QUERY_RETAIL:
                 result = msg.getData().getString("result");
                 Log.d("TAG", "retail store  result->" + result);
                 getData(result,"无零售业务");
                 break;
            }
        }
    }


    /**
     * json解析返回数据
     * @param result json
     * @param data 提示
     */
    private void getData(String result,String data){
        if (!ResultLog.getInstance(StoreManagerActivity.this).getNoRes(result)) {
            dataLists.clear();
            if (orderType.equals("A")) {
                dataLists = ResultLog.getInstance(StoreManagerActivity.this).SaleAllLog(result);
            } else {
                dataLists = ResultLog.getInstance(StoreManagerActivity.this).OrderNoLog(result);
            }
            Log.d("TAG", " store store  data->" + dataLists.toString());
            if (dataLists.size() > 0) {
                rlNo.setVisibility(View.GONE);
                adapter = new FgDetailAdapter(StoreManagerActivity.this, dataLists);
                lv.setAdapter(adapter);
            } else {
                tvNo.setText(data);
                rlNo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        hidden();
        switch (v.getId()){
            case R.id.store_manager_all:
//                tvOrder.setTextColor(Color.parseColor("#454545"));
//                viewOrder.setVisibility(View.INVISIBLE);
//                tvRetail.setTextColor(Color.parseColor("#454545"));
//                viewRetail.setVisibility(View.INVISIBLE);
                tvAll.setTextColor(Color.parseColor("#e84232"));
                viewAll.setVisibility(View.VISIBLE);
                orderType = "A";
                MyThread.getInstance(StoreManagerActivity.this).QueryDataThread(handler,"","","","A",Constant.DATA_QUERY_ALL);
                break;
            case R.id.store_manager_order:
                tvOrder.setTextColor(Color.parseColor("#e84232"));
                viewOrder.setVisibility(View.VISIBLE);
//                tvRetail.setTextColor(Color.parseColor("#454545"));
//                viewRetail.setVisibility(View.INVISIBLE);
//                tvAll.setTextColor(Color.parseColor("#454545"));
//                viewAll.setVisibility(View.INVISIBLE);
                orderType = "O";
                MyThread.getInstance(StoreManagerActivity.this).QueryDataThread(handler,"","","",orderType, Constant.DATA_QUERY_ORDER);
                break;
            case R.id.store_manager_retail:
//                tvOrder.setTextColor(Color.parseColor("#454545"));
//                viewOrder.setVisibility(View.INVISIBLE);
                tvRetail.setTextColor(Color.parseColor("#e84232"));
                viewRetail.setVisibility(View.VISIBLE);
//                tvAll.setTextColor(Color.parseColor("#454545"));
//                viewAll.setVisibility(View.INVISIBLE);
                orderType = "R";
                MyThread.getInstance(StoreManagerActivity.this).QueryDataThread(handler,"","","",orderType, Constant.DATA_QUERY_RETAIL);
                break;
            case R.id.head_rl_title:
                //Intent intent = new Intent(StoreManagerActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 初始标签
     */
    private void hidden(){
        tvOrder.setTextColor(Color.parseColor("#454545"));
        viewOrder.setVisibility(View.INVISIBLE);
        tvRetail.setTextColor(Color.parseColor("#454545"));
        viewRetail.setVisibility(View.INVISIBLE);
        tvAll.setTextColor(Color.parseColor("#454545"));
        viewAll.setVisibility(View.INVISIBLE);
    }
    /**
     * 返回键
     * @param keyCode keyCode
     * @param event event
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Intent intent = new Intent(StoreManagerActivity.this, MainActivity.class);
            //startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
