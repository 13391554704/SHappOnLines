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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
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

/**
 * 支付
 */
public class PayModelActivity extends BaseActivity implements View.OnClickListener {
    //head title 返回
    private TextView tvTitle;
    private TextView tvAll,tvRes,tvShow;
    private LinearLayout llShow;
    private RelativeLayout rlBack;
    //金额 ，返回主页
    private TextView tvMoney,btnBack;
    //二维码图片
    private ImageView ivModel;
    private GridView glView;
    //确认付款
    private Button btnConfirm ;
    private List<Map<String,String>> payModel = new ArrayList<Map<String, String>>();
    //订单号,销售类型  O-订单  R-铺货  金额 服务器返回结果
    private String orderNum,orderType,orderFrom,money,result,all;
    private String cardId = "";
    private String res="0";
    //handler 处理消息
    private MyHandler handler;
    //imageLoader
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    // 支付方式 code id URL
    private String paymentCode,paymentId,qrcodeUrl,paymodelFlag;
    //标识  哪个界面响应
    private String unSale;
    // adapter
    private PayModelAdapter adapter;
    //intent
    private Intent intent;
    private int downIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_model);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("确认付款");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvMoney = (TextView) findViewById(R.id.pay_model_tv_money);
        ivModel = (ImageView) findViewById(R.id.pay_model_iv);
        btnConfirm = (Button) findViewById(R.id.pay_model_btn_confirm);
        btnBack = (TextView) findViewById(R.id.pay_model_btn_back);
        glView = (GridView) findViewById(R.id.pay_model_gl);
        tvAll =(TextView) findViewById(R.id.pay_model_show_tv_all);
        tvRes = (TextView) findViewById(R.id.pay_model_show_tv_res);
        llShow = (LinearLayout) findViewById(R.id.pay_model_ll_show);
        tvShow = (TextView) findViewById(R.id.pay_model_toast);
        handler = new MyHandler();
        imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chanpin) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chanpin) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chanpin) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(6)) // 设置成圆角图片
                .build(); // 构建完成
        adapter = new PayModelAdapter();
        getIntentData();
        MyThread.getInstance(PayModelActivity.this).PayUrlThread(handler,Constant.PAY_TYPE,orderNum,orderFrom,"");
    }

    /**
     * intent
     */
    private void getIntentData() {
        Intent intent = getIntent();
        orderNum = intent.getStringExtra("orderNo");
        orderType = intent.getStringExtra("orderType");
        orderFrom = intent.getStringExtra("orderFrom");
        money = intent.getStringExtra("totalPrice");
        unSale = intent.getStringExtra("unSale");
        tvMoney.setText(money);
        Log.d("TAG","orderType" + orderType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        glView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                paymentCode = payModel.get(position).get("paymentCode");
                paymentId = payModel.get(position).get("paymentId");
                paymodelFlag = payModel.get(position).get("flag");
                Log.d("TAG","pay->"+paymodelFlag);
                downIndex = 1;
                MyThread.getInstance(PayModelActivity.this).ForceDownThread(handler, userName, userPwd, phoneCode);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.head_rl_title:
                if (("unSale").equals(unSale)){
                    finish();
                }else {
                    intent = new Intent(PayModelActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            //确认
            case R.id.pay_model_btn_confirm:
                if (paymentCode ==null || ("").equals(paymentCode)){
                    Toast.makeText(PayModelActivity.this,"请选择支付方式",Toast.LENGTH_SHORT).show();
                }else {
                    if ("A".equals(paymodelFlag)){
                        if (Double.parseDouble(money) < Double.parseDouble(res)) {
                            showDialog("验证码",true);
                        }else{
                            showDialog("商户账期余额不足,请选择别的支付方式!",false);
                        }
                    }else {
                        showDialog("已经收取货款?",false);
                    }
                }
                break;
            //返回主页
            case R.id.pay_model_btn_back:
                intent = new Intent(PayModelActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * doalig
     * @param message message
     * @param confirm boolean
     */
    private void showDialog(String message,boolean confirm){
            final MyDialog confirmDialog = new MyDialog(PayModelActivity.this, "提示",message, "确认", "取消", confirm, false);
            confirmDialog.setCancelable(false);
            confirmDialog.show();
            confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
                @Override
                public void doConfirm(String result) {
                    if ("123".equals(result)) {
                        downIndex = 2;
                        MyThread.getInstance(PayModelActivity.this).ForceDownThread(handler, userName, userPwd, phoneCode);
//                                            MyThread.getInstance(PayModelActivity.this).PayThread(handler,orderNum,orderFrom, paymentId, orderType, "S");
                    }
                }

                @Override
                public void doConfirm() {
                    if ("A".equals(paymodelFlag)) {
                        confirmDialog.dismiss();
                    }else{
                        downIndex = 2;
                        MyThread.getInstance(PayModelActivity.this).ForceDownThread(handler, userName, userPwd, phoneCode);
//                                            MyThread.getInstance(PayModelActivity.this).PayThread(handler,orderNum,orderFrom, paymentId, orderType, "S");
                    }
                }

                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });
    }


    /**
     * adapter
     */
    class PayModelAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return payModel.size();
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
            if (convertView ==null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(PayModelActivity.this).inflate(R.layout.item_pay_model_gl,parent,false);
                viewHolder.ivPayModel = (ImageView) convertView.findViewById(R.id.item_pay_model_iv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String path = payModel.get(position).get("payModel");
            imageLoader.displayImage(path,viewHolder.ivPayModel, options);
            return convertView;
        }
        class ViewHolder{
            ImageView ivPayModel;
        }
    }

    /**
     * 处理信息
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //支付信息
                case Constant.PAY_TYPE:
                    result = msg.getData().getString("result");
                    getJson(result);
                    Log.d("TAG", payModel.toString());
                    glView.setAdapter(adapter);
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(PayModelActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(PayModelActivity.this).getDialog(PayModelActivity.this);
                            } else {
                                if (downIndex == 1) {
                                    MyThread.getInstance(PayModelActivity.this).PayUrlThread(handler, Constant.PAY_TYPE_URL,orderNum,orderFrom,paymentId);
                                    if ("A".equals(paymodelFlag)){
                                        //获取额度
                                        MyThread.getInstance(PayModelActivity.this).PayThread(handler,Constant.PAY_DEBT_URL,orderNum, orderFrom,paymentId,"Z","Z","");
                                    }else{
                                        llShow.setVisibility(View.GONE);
                                        tvShow.setVisibility(View.VISIBLE);
                                        tvShow.setText("");
                                    }
                                } else if (downIndex == 2) {
                                    MyThread.getInstance(PayModelActivity.this).PayThread(handler,Constant.FINISH_PAY, orderNum, orderFrom, paymentId, orderType, "S",cardId);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //商户账期
                case Constant.PAY_DEBT_URL:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "pay finish result->" + result);
                    if (!ResultLog.getInstance(PayModelActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                all = object.getString("lineofcredit");
                                res = object.getString("balanceamt");
                                if (res.contains(".")) {
                                    int i = res.indexOf(".") + 3;
                                    res = res.substring(0, i);
                                }
                                cardId = object.getString("cardid");
                                tvAll.setText(all);
                                tvRes.setText(res);
                                llShow.setVisibility(View.VISIBLE);
                                tvShow.setVisibility(View.GONE);
                            }else{
                                tvShow.setText("终端还未挂靠关系，无信用额度!");
                                tvShow.setVisibility(View.VISIBLE);
                                llShow.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                // 支付图片url
                case Constant.PAY_TYPE_URL:
                    result = msg.getData().getString("result");
                    getJson(result);
                    Log.d("TAG", payModel.toString());
                    adapter.notifyDataSetChanged();
                    Log.d("TAG", "qrcodeurl->" + qrcodeUrl);
                    imageLoader.displayImage(qrcodeUrl, ivModel, options);
                    break;
                //结算
                case Constant.FINISH_PAY:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "pay finish result->" + result);
                    if (!ResultLog.getInstance(PayModelActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (("1").equals(object.getString("totline"))) {
                                String state = object.getString("errorMessage");
                                if ("ok".equals(state)) {
                                        if (("P").equals(orderFrom)){
                                            MyThread.getInstance(PayModelActivity.this).PayThread(handler,Constant.PAY_DEBT_BOOK_URL, orderNum, orderFrom, paymentId,"Z","P",cardId);
                                        }else {
                                            if("A".equals(paymodelFlag)) {
                                                MyThread.getInstance(PayModelActivity.this).PayThread(handler,Constant.PAY_DEBT_BOOK_URL, orderNum, orderFrom, paymentId,"Z", "J",cardId);
                                        }
                                    }

                                    Intent intent = new Intent(PayModelActivity.this, DetailActivity.class);
                                    intent.putExtra("orderNo", orderNum);
                                    intent.putExtra("orderType", orderType);
                                    intent.putExtra("orderFrom", orderFrom);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(PayModelActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //记账
                case Constant.PAY_DEBT_BOOK_URL:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "ji zhang->" + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        String state = object.getString("errorMessage");
                        if ("ok".equals(state)) {
                            Toast.makeText(PayModelActivity.this, "记账成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PayModelActivity.this, "记账失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * json
     * @param result json
     */
    private void getJson(String result){
        if (!ResultLog.getInstance(PayModelActivity.this).getNoRes(result)) {
            Log.d("TAG", "code result->" + result);
            payModel.clear();
            try {
                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("rows");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    qrcodeUrl = (String) jo.get("qrcodeurl");
                    String paymentCode = (String) jo.get("paymentcode");
                    String paymentUrl = (String) jo.get("paymenturl");
                    String paymentId = jo.getString("paymentid");
                    String flag = jo.getString("detailaccountflag");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("payModel", paymentUrl);
                    map.put("paymentCode", paymentCode);
                    map.put("paymentId", paymentId);
                    map.put("flag", flag);
                    payModel.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 返回键
     * @param keyCode keyCode
     * @param event event
     * @return boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (("unSale").equals(unSale)){
                finish();
            }else {
                intent = new Intent(PayModelActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
