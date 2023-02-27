package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.ConnectAdapter;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
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
 * 交班数据 （订单量， 零售数量）
 */
public class ClearDataActivity extends BaseActivity implements View.OnClickListener {
    //head
    private TextView tvTitle;//title
    private RelativeLayout rlBack;//back
    private LinearLayout llBottom;
    private TextView tvOrderNo,tvOwnerOrderNo;
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();//数据集
    private ListView lvStore;//listview
    private MyHandler handler;//handler
    private String flag;//intent传递数据
    private String result;//json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_data);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        llBottom = (LinearLayout) findViewById(R.id.clear_data_bottom);
        tvOrderNo = (TextView) findViewById(R.id.server_order_tv_number);
        tvOwnerOrderNo = (TextView) findViewById(R.id.owner_order_tv_number);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        lvStore = (ListView) findViewById(R.id.clear_lv_data);
        llBottom.setVisibility(View.GONE);
        handler = new MyHandler();
        getIntentData();
    }

    /**
     * intent
     */
    private void getIntentData() {
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (("order").equals(flag)){
            tvTitle.setText("订单完成量");
            MyThread.getInstance(ClearDataActivity.this).QueryDataThread(handler,"","","","O", Constant.DATA_QUERY_ORDER);
            MyThread.getInstance(ClearDataActivity.this).QueryDataThread(handler,"","","","B", Constant.DATA_QUERY_ORDER_TYPE);
            return;
        }
        if (("sale").equals(flag)){
            tvTitle.setText("零售单汇总");
            MyThread.getInstance(ClearDataActivity.this).QueryDataThread(handler,"","","","R", Constant.DATA_QUERY_RETAIL);
            return;
        }
        if (("sell").equals(flag)){
            tvTitle.setText("零售产品汇总");
            MyThread.getInstance(ClearDataActivity.this).ConnectThread(handler,Constant.CONNECT_T,"T");
            return;
        }
        if (("res").equals(flag)){
            tvTitle.setText("剩余产品汇总");
            MyThread.getInstance(ClearDataActivity.this).ConnectThread(handler,Constant.CONNECT_R,"R");
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_rl_title:
                finish();
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
                case Constant.DATA_QUERY_ORDER_TYPE:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ClearDataActivity.this).getNoRes(result)) {
                        Log.d("TAG", "count->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("rows");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = array.getJSONObject(i);
                                String pltcount = jo.getString("pltcount");
                                String owncount = jo.getString("owncount");
                                tvOrderNo.setText(pltcount);
                                tvOwnerOrderNo.setText(owncount);
                                llBottom.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    //订单销售信息
                case Constant.DATA_QUERY_ORDER:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ClearDataActivity.this).getNoRes(result)) {
                        Log.d("TAG", "order store  result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(ClearDataActivity.this).OrderNoLog(result);
                        Log.d("TAG", " store store  data->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            lvStore.setAdapter(new FgDetailAdapter(ClearDataActivity.this, dataLists));
                        }
                        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(ClearDataActivity.this, DetailActivity.class);
                                intent.putExtra("orderNo", dataLists.get(position).get("number").toString());
                                intent.putExtra("orderType", "O");
                                intent.putExtra("orderFrom", dataLists.get(position).get("fromCode").toString());
                                intent.putExtra("intent", "sale");
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                //铺货销售信息
                case Constant.DATA_QUERY_RETAIL:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ClearDataActivity.this).getNoRes(result)) {
                        Log.d("TAG", "retail store  result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(ClearDataActivity.this).OrderNoLog(result);
                        Log.d("TAG", " store  retail  data->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            lvStore.setAdapter(new FgDetailAdapter(ClearDataActivity.this, dataLists));
                        }
                        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(ClearDataActivity.this, DetailActivity.class);
                                intent.putExtra("orderNo", dataLists.get(position).get("number").toString());
                                intent.putExtra("orderType", "R");
                                intent.putExtra("intent", "sale");
                                intent.putExtra("orderFrom", dataLists.get(position).get("fromCode").toString());
                                startActivity(intent);
                            }
                        });
                    }
                    break;
                //剩余商品合计
                case Constant.CONNECT_R:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "retail store  result->" + result);
                    if(!ResultLog.getInstance(ClearDataActivity.this).getNoRes(result)) {
                        dataLists.clear();
                        getJson(result);
                        if (dataLists.size() > 0) {
                            lvStore.setAdapter(new ConnectAdapter(ClearDataActivity.this, dataLists));
                        }
                    }
                    break;
                //零售产品汇总
                case Constant.CONNECT_T:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "retail store  result->" + result);
                    if(!ResultLog.getInstance(ClearDataActivity.this).getNoRes(result)) {
                        dataLists.clear();
                        getJson(result);
                        if (dataLists.size() > 0) {
                            lvStore.setAdapter(new ConnectAdapter(ClearDataActivity.this, dataLists));
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 解析json
     * @param data result
     */
    private void getJson(String data){
        try {
            JSONObject object = new JSONObject(data);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String itemCode = (String) jo.get("itemcode");
                String itemName = (String) jo.get("itemname");
                String itemSpec = (String) jo.get("itemspec");
                String itemWeight = (String) jo.get("itemweight");
                String itemUrl = (String) jo.get("itemurl");
                String unitCode = jo.getString("unitcode");
                String unitName = jo.getString("unitdesc");
                String sumQty = jo.getString("sumqty");
                int a = sumQty.indexOf(".");
                sumQty = sumQty.substring(0, a);
                int b = itemWeight.indexOf(".");
                itemWeight = itemWeight.substring(0, b);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("url", itemUrl);
                map.put("itemCode", itemCode);
                map.put("name", itemName);
                map.put("info", itemSpec);
                map.put("unitName", unitName);
                map.put("itemWeight", itemWeight);
                map.put("sumQty", sumQty);
                dataLists.add(map);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
