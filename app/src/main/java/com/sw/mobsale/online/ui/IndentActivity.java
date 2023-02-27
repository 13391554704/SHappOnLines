package com.sw.mobsale.online.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.IndentAdapter;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提交订单
 */
public class IndentActivity extends BaseActivity implements View.OnClickListener {
    private ListView lvList;//listview
    private EditText tvStore, tvPhone;//买家，地址
    private TextView tvTotalNumber,tvAddress,tvChoose,tvTotalPrice;//数量,价格
    private Button btnSubmit;//查询商铺 ,  提交订单
    private ImageView ivLoc;  //定位客户地址
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();//存数据
    private TextView tvTitle; //head文件 标题
    private RelativeLayout rlBack;//head文件 返回
    private String totalPrice;   //传递数据 价格
    private MyHandler handler;//handler
    private String  result; //后台返回结果
    private String address ="";
    private String province = "";//省
    private String city = "";//市
    private String country = "";//县区
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvStore = (EditText) findViewById(R.id.indent_ll_tv_name);
        tvChoose = (TextView) findViewById(R.id.indent_ll_btn_choose);
        tvAddress = (TextView) findViewById(R.id.indent_ll_tv_address);
        tvPhone = (EditText) findViewById(R.id.indent_ll_tv_phone);
        ivLoc = (ImageView) findViewById(R.id.sell_iv_loc);
        tvTotalNumber = (TextView) findViewById(R.id.indent_ll_total_number_tv);
        tvTotalPrice = (TextView) findViewById(R.id.indent_ll_total_money_tv);
        btnSubmit = (Button) findViewById(R.id.indent_ll_total_submit);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("确认订单");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        lvList = (ListView) findViewById(R.id.indent_lv);
        handler = new MyHandler();
        //初始化数据
        getIntentData();
        //购物车数据 status D
        MyThread.getInstance(IndentActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_D,"","D","","");

    }

    /**
     * 获取界面传递数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        String store = intent.getStringExtra("customerName");
        address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        tvStore.setText(store);
        if (address == null || ("").equals(address)){
            SharedPreferences spf = getSharedPreferences("loc",MODE_PRIVATE);
            address = spf.getString("address","");
            //tvAddress.setText(saleaddress);
        }else {
            //tvAddress.setText(address);
            tvPhone.setText(phone);
        }
        getTouth(tvStore);
        getTouth(tvPhone);
    }

    /**
     * 验证手机号是否正确ֻ
     * @param mobiles  手机号格式
     * @return boolean
     */
    public  boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    /**
     * 光标处于edittext末尾
     * @param textView tv
     */
    private void getTouth(TextView textView){
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            //移动到字体后面
            Selection.setSelection(spanText, text.length());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        tvChoose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        ivLoc.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
    }

    /**
     * 监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.head_rl_title:
                finish();
                break;
            //查询商铺
            case R.id.indent_ll_btn_choose:
                intent= new Intent(IndentActivity.this, StoreDetailActivity.class);
                intent.putExtra("store",tvStore.getText().toString());
                startActivityForResult(intent, Constant.CHOOSE_STORE);
                break;
            //定位
            case R.id.sell_iv_loc:
//                Intent intent = new Intent(IndentActivity.this, TestActivity.class);
//                startActivityForResult(intent, Constant.SELL_TEST);
                getAddr();
                break;
            //查询省市区
            case R.id.indent_ll_tv_address:
                getAddr();
                break;
            //提交
            case R.id.indent_ll_total_submit:
                if (isSub()){
                    MyThread.getInstance(IndentActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(IndentActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_S,"","S","","LS001",tvStore.getText().toString(),tvAddress.getText().toString());
                }
                break;
        }
    }

    /**
     * 查询省市区
     */
    private void getAddr(){
        String addr = tvAddress.getText().toString();
        intent = new Intent(IndentActivity.this,QueryAddrActivity.class);
        if (("").equals(addr)){
            intent.putExtra("address",address);
        }else{
            intent.putExtra("address",addr);
        }
        startActivityForResult(intent,Constant.ADDRESS_SUBMIT);
    }

    /**
     * 判断提交订单条件
     * @return
     */
    private boolean isSub(){
        if (("").equals(tvPhone.getText().toString())|| !isMobileNO(tvPhone.getText().toString())){
            if (("").equals(tvPhone.getText().toString())){
                Toast.makeText(IndentActivity.this,"请输入手机号!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(IndentActivity.this, "请输入正确手机号!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        if (("").equals(province)||("").equals(city)||("").equals(country)){
            Toast.makeText(IndentActivity.this,"请选择省市区!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (("").equals(tvStore.getText().toString())) {
            Toast.makeText(IndentActivity.this,"商铺名称不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        //定位
//        if (requestCode == Constant.SELL_TEST) {
//            if (resultCode == RESULT_OK) {
//                address = data.getStringExtra("address");
//                tvAddress.setText(address);
//                getTouth(tvAddress);
//            }
//        }
        //选择商铺
        if (requestCode == Constant.CHOOSE_STORE) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String address = data.getStringExtra("address");
                String phone = data.getStringExtra("phone");
                tvStore.setText(name);
                tvAddress.setText(address);
                tvPhone.setText(phone);
                province = data.getStringExtra("province");
                city = data.getStringExtra("city");
                country = data.getStringExtra("country");
            }
        }
        //选择地址
        if (requestCode == Constant.ADDRESS_SUBMIT) {
            if (resultCode == RESULT_OK) {
                String addr = data.getStringExtra("jsAddress");
                tvAddress.setText(addr);
                province = data.getStringExtra("province");
                city = data.getStringExtra("city");
                country = data.getStringExtra("country");
            }
        }
        getTouth(tvStore);
        getTouth(tvAddress);
        getTouth(tvPhone);
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //订单明细
                case Constant.SALE_RETAIL_D:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(IndentActivity.this).getNoRes(result)) {
                        Log.d("TAG", " ind result->" + result);
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(IndentActivity.this).RetailDetailLog(result);
                        Log.d("TAG", "sell ->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            lvList.setAdapter(new IndentAdapter(IndentActivity.this, dataLists));
                            String qty = dataLists.get(0).get("sumQty").toString();
                            totalPrice = dataLists.get(0).get("sumAmt").toString();
                            tvTotalNumber.setText(qty);
                            tvTotalPrice.setText(totalPrice);
                        } else {
                            Toast.makeText(IndentActivity.this, "没有商品信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                //确认在线
                case Constant.FORCE_DOWN:
                    String result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(IndentActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG","force down->"+ result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)){
                                ResultLog.getInstance(IndentActivity.this).getDialog(IndentActivity.this);
                            }else{
                                MyThread.getInstance(IndentActivity.this).ConfirmOrderThread(handler,"1", Constant.SALE_RETAIL_S,"","S","","",tvPhone.getText().toString(),tvStore.getText().toString(),tvAddress.getText().toString(),province,city,country,tvPhone.getText().toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //确认订单
                case Constant.SALE_RETAIL_S:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " btn result->" + result);
                    if (!ResultLog.getInstance(IndentActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (("1").equals(object.getString("totline"))) {
                                String orderNum = object.getString("errorMessage");
                                Intent intent = new Intent(IndentActivity.this, PayModelActivity.class);
                                intent.putExtra("orderNo", orderNum);
                                intent.putExtra("totalPrice", totalPrice);
                                intent.putExtra("orderType", "R");
                                intent.putExtra("orderFrom", "S");
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

}
