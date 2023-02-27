package com.sw.mobsale.online.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyDialog;
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


public class ConnectActivity extends BaseActivity implements View.OnClickListener{
    //界面 车号  出发时间  交班时间  商品总数 剩余商品 销售数量，金额
    private TextView tvSeller,tvUser,tvPhone,tvWorker,tvCarNumber, tvStartTime,tvOrderNo,tvOrderSaleNo,tvSaleNo,tvResNumber,tvSellNumber, tvSellPrice;
    private GridView lvModel;//支付方式
    private LinearLayout llOrderNo,llSaleNo,llSellNo,llResNo;//明细 销售订单 销售商品 剩余商品 销售数量
    //支付方式数据源
    private List<Map<String, Object>> modelLists = new ArrayList<Map<String, Object>>();
    //head
    private TextView tvTitle;
    private RelativeLayout rlBack;
    private Button btnConfirm;//交班按钮
    //handler
    private MyHandler handler;
    //后台返回json
    private String result;
    private String userName,userPwd,phoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvSeller = (TextView) findViewById(R.id.connect_tv_seller_name);
        tvUser = (TextView) findViewById(R.id.connect_tv_user_name);
        tvPhone = (TextView) findViewById(R.id.connect_tv_user_phone);
        tvWorker = (TextView) findViewById(R.id.connect_tv_worker);
        tvCarNumber = (TextView) findViewById(R.id.connect_tv_car_number);
        tvStartTime = (TextView) findViewById(R.id.connect_tv_start_time);
        tvOrderSaleNo = (TextView) findViewById(R.id.connect_tv_sale_order_number);
        tvOrderNo = (TextView) findViewById(R.id.connect_tv_order_number);
        tvSaleNo = (TextView) findViewById(R.id.connect_tv_sale_number);
        tvResNumber = (TextView) findViewById(R.id.connect_tv_res_pro_number);
        tvSellNumber = (TextView) findViewById(R.id.connect_tv_sell_pro_number);
        tvSellPrice = (TextView) findViewById(R.id.connect_tv_sell_pro_amt);
        llOrderNo = (LinearLayout) findViewById(R.id.connect_ll_order_number);
        llSaleNo = (LinearLayout) findViewById(R.id.connect_ll_sale_number);
        llSellNo = (LinearLayout) findViewById(R.id.connect_ll_sell_pro_number);
        llResNo = (LinearLayout) findViewById(R.id.connect_ll_res_pro_number);
        lvModel = (GridView) findViewById(R.id.connect_lv);
        btnConfirm = (Button) findViewById(R.id.connect_btn_confirm);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvTitle.setText("交班");
        handler = new MyHandler();
        getSpf();
        //用户信息
        MyThread.getInstance(ConnectActivity.this).ConnectThread(handler,Constant.CONNECT_A,"A");
        //销售金额 分类
        MyThread.getInstance(ConnectActivity.this).ConnectThread(handler,Constant.CONNECT_D,"D");
    }

    /**
     * 获取用户名 密码 设备号
     */
    public void getSpf(){
        SharedPreferences sdf = getSharedPreferences("user", Context.MODE_PRIVATE);
        userName = sdf.getString("userCode","");
        userPwd = sdf.getString("password","");
        phoneCode =sdf.getString("phoneCode","");
    }

    @Override
    protected void onResume() {
        super.onResume();
        llOrderNo.setOnClickListener(this);
        llSaleNo.setOnClickListener(this);
        llSellNo.setOnClickListener(this);
        llResNo.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //订单数量
            case R.id.connect_ll_order_number:
                openActivity("order");
                break;
            //铺货单数量
            case R.id.connect_ll_sale_number:
                openActivity("sale");
                break;
            //销售商品数量
            case R.id.connect_ll_sell_pro_number:
                openActivity("sell");
                break;
            //剩余商品数量
            case R.id.connect_ll_res_pro_number:
                openActivity("res");
                break;
            //交班按钮
            case R.id.connect_btn_confirm:
                MyThread.getInstance(ConnectActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
                break;
            //返回
            case R.id.head_rl_title:
                finish();
                break;
        }
    }

    /**
     * 跳转->ClearDataActivity
     * @param flag 标识
     */
    public void openActivity(String flag){
        Intent intent = new Intent(ConnectActivity.this,ClearDataActivity.class);
        intent.putExtra("flag",flag);
        startActivity(intent);
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
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * handler
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //交班头信息
                case Constant.CONNECT_A:
                   result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ConnectActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("rows");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = array.getJSONObject(i);
                                String sellercode = jo.getString("sellercode");
                                String sellername = jo.getString("sellername");
                                String usercode = jo.getString("usercode");
                                String terminalname = jo.getString("terminalname");
                                String terminalmobile = jo.getString("terminalmobile");
                                String drivername = jo.getString("drivername");
                                String saleamt = jo.getString("saleamt");
                                String ordernum = jo.getString("ordernum");
                                String carnum = jo.getString("carnum");
                                String orderallnum = jo.getString("orderallnum");
                                String retailnum = jo.getString("retailnum");
                                String retailitemnum = jo.getString("retailitemnum");
                                String allitemnum = jo.getString("allitemnum");
                                String flightsseq = jo.getString("flightsno");
                                if (!("0").equals(allitemnum)) {
                                    int a = allitemnum.indexOf(".");
                                    allitemnum = allitemnum.substring(0, a);
                                }
                                if (!("0").equals(retailitemnum)) {
                                    int b = retailitemnum.indexOf(".");
                                    retailitemnum = retailitemnum.substring(0, b);
                                }
                                tvSeller.setText(sellername);
                                tvUser.setText(terminalname);
                                tvPhone.setText(terminalmobile);
                                tvWorker.setText(drivername);
                                tvCarNumber.setText(carnum);
                                tvOrderSaleNo.setText(ordernum);
                                tvOrderNo.setText(orderallnum);
                                tvSaleNo.setText(retailnum);
                                tvResNumber.setText(allitemnum);
                                tvSellNumber.setText(retailitemnum);
                                tvSellPrice.setText(saleamt);
                                tvStartTime.setText(flightsseq);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //交班支付信息
                case  Constant.CONNECT_D:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ConnectActivity.this).getNoRes(result)) {
                        modelLists = ResultLog.getInstance(ConnectActivity.this).ModelLog(result);
                        lvModel.setAdapter(new modelAdapter());
                    }
                    break;
                //在线
                case Constant.FORCE_DOWN:
                    String result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ConnectActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(ConnectActivity.this).getDialog(ConnectActivity.this);
                            } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectActivity.this);
//                            builder.setTitle("提示").setIcon(R.drawable.warn).setMessage("确定交班吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    MyThread.getInstance(ConnectActivity.this).ConnectThread(handler,Constant.CONNECT_E,"E");
//                                }
//                            }).setNegativeButton("取消", null).create();
//                            builder.show();
                                final MyDialog confirmDialog = new MyDialog(ConnectActivity.this, "提示", "确定交班吗?", "确认", "取消", false, false);
                                confirmDialog.setCancelable(false);
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm(String result) {

                                    }

                                    @Override
                                    public void doConfirm() {
                                        MyThread.getInstance(ConnectActivity.this).ConnectThread(handler, Constant.CONNECT_E, "E");
                                    }

                                    @Override
                                    public void doCancel() {
                                        confirmDialog.dismiss();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //交班确认信息
                case  Constant.CONNECT_E:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ConnectActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            Log.d("TAG", message);
                            if (message.equals("ok")) {
                                Toast.makeText(ConnectActivity.this, "完成交班!", Toast.LENGTH_SHORT).show();
                                MyThread.getInstance(ConnectActivity.this).BackThread(handler, userName, userPwd, phoneCode);
                            } else {
                                Toast.makeText(ConnectActivity.this, "请核对数据后在进行交班!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //退出信息
                case Constant.USER_BACK:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ConnectActivity.this).getNoRes(result)) {
                        ResultLog.getInstance(ConnectActivity.this).DownLog(result, ConnectActivity.this);
                    }
                    break;
            }

        }
    }

    /**
     * adapter gridview
     */
    class modelAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return modelLists.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView =  LayoutInflater.from(ConnectActivity.this).inflate(R.layout.more_data,parent,false);
                viewHolder.tvModel = (TextView) convertView.findViewById(R.id.more_Data_model);
                viewHolder.tvMoney = (TextView) convertView.findViewById(R.id.more_Data_model_money);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvModel.setText((String)modelLists.get(position).get("model"));
            viewHolder.tvMoney.setText((String)modelLists.get(position).get("money"));

            return convertView;
        }
        class ViewHolder{
            TextView tvModel;
            TextView tvMoney;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
