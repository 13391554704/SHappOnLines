package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.LoadingAdapter;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyDialog;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.PrintDataService;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据打印
 */
public class DetailActivity extends BaseActivity implements View.OnClickListener {
    //TextView   订单时间 销售单号  店铺 地址  联系方式  总价 付款 付款方式  支付方式 总量  蓝牙地址 更换蓝牙
    private TextView tvDate, tvOrderNum, tvStore, tvAddress,tvPhone, tvTotalPrice, tvPrice, tvPay, tvModel, tvTotalNumber,tvBlueName,tvBlueUp;
    private Button btnConnect, btnPrint;
    private LinearLayout llModel;
    private ListView lvData;
    private TextView tvTitle, tvShow;
    private RelativeLayout rlBack;
    //数据源
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();
    //bluetoothAdapter
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //PrintDataService
    private PrintDataService printDataService;
    private Intent intent;
    boolean flag = false;
    private String orderNum,orderId,orderType,orderFrom;
    //蓝牙设备地址
    private String device;
    private String sale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvBlueName = (TextView)findViewById(R.id.detail_bluetooth_tv);
        tvBlueUp = (TextView)findViewById(R.id.detail_bluetooth__tv_up);
        tvDate = (TextView) findViewById(R.id.detail_ll_date_tv);
        tvOrderNum = (TextView) findViewById(R.id.detail_ll_number_tv);
        tvStore = (TextView) findViewById(R.id.detail_ll_name_tv);
        tvAddress = (TextView) findViewById(R.id.detail_ll_adress_tv);
        tvPhone = (TextView) findViewById(R.id.detail_ll_phone_tv);
        tvTotalPrice = (TextView) findViewById(R.id.detail_ll_total_price_tv);
        tvTotalNumber = (TextView) findViewById(R.id.detail_ll_total_number_tv);
        tvPrice = (TextView) findViewById(R.id.detail_ll_money_tv);
        tvPay = (TextView) findViewById(R.id.detail_ll_pay_tv);
        tvModel = (TextView) findViewById(R.id.detail_ll_model_tv);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("销售打印");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvShow = (TextView) findViewById(R.id.head_title_gone);
        tvShow.setText("完成");
        tvShow.setVisibility(View.VISIBLE);
        llModel = (LinearLayout) findViewById(R.id.detail_ll_model);
        //搜索蓝牙设备
        btnConnect = (Button) findViewById(R.id.detail_btn_connect);
        //打印
        btnPrint = (Button) findViewById(R.id.detail_btn_print);
        //商品列表
        lvData = (ListView) findViewById(R.id.detail_lv_list);
        getIntentData();
        btnPrint.setEnabled(false);
    }

    /**
     * 获取intent数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        orderNum = intent.getStringExtra("orderNo");
        orderType = intent.getStringExtra("orderType");
        orderFrom= intent.getStringExtra("orderFrom");
        sale = intent.getStringExtra("intent");
        tvOrderNum.setText(orderNum);
        SharedPreferences spf = getSharedPreferences("bluetooth",MODE_PRIVATE);
        device = spf.getString("bound","");
        Log.d("TAG",device);
        MyHandler handler = new MyHandler();
        MyThread.getInstance(DetailActivity.this).PrintThread(handler,orderNum,orderFrom,"","O","P");
      //  MyThread.getInstance(DetailActivity.this).PrintThread(handler,orderNum,orderFrom,"",orderType,"P");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isOpen()) {
            openBluetooth(DetailActivity.this);
        }
        tvBlueUp.setOnClickListener(this);
        rlBack.setOnClickListener(DetailActivity.this);
        tvShow.setOnClickListener(DetailActivity.this);
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        this.bluetoothAdapter.disable();
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return boolean
     */
    public boolean isOpen() {
        return bluetoothAdapter.isEnabled();
    }


    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //打印数据
            if (msg.what == Constant.DATA_PRINT) {
                String result = msg.getData().getString("result");
                if (!ResultLog.getInstance(DetailActivity.this).getNoRes(result)) {
                    Log.d("TAG", "print result->" + result);
                    dataLists.clear();
                    dataLists = ResultLog.getInstance(DetailActivity.this).Print(result);
                    Log.d("TAG", "print result data->" + dataLists.toString());
                    if (dataLists.size() >0) {
                        tvStore.setText(dataLists.get(0).get("store").toString());
                        tvAddress.setText(dataLists.get(0).get("address").toString());
                        tvPhone.setText(dataLists.get(0).get("phone").toString());
                        tvDate.setText(dataLists.get(0).get("date").toString());
                        tvPrice.setText(dataLists.get(0).get("amt").toString());
                        tvTotalPrice.setText(dataLists.get(0).get("amt").toString());
                        tvTotalNumber.setText(dataLists.get(0).get("qtySum").toString());
                        tvPay.setText(dataLists.get(0).get("pay").toString());
                        tvModel.setText(dataLists.get(0).get("model").toString());
                        //绑定适配器
                        lvData.setAdapter(new LoadingAdapter(DetailActivity.this, dataLists));
                    }else{
                        Toast.makeText(DetailActivity.this,"此线路上无客户无订单信息!",Toast.LENGTH_SHORT).show();
                    }

                    btnConnect.setOnClickListener(DetailActivity.this);
                    btnPrint.setOnClickListener(DetailActivity.this);
                }
            }
        }
}


    /**
     * 单击响应事件
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_rl_title:
                if (("sale").equals(sale)) {
                    finish();
                } else {
                    close();
                }
                break;
            case R.id.head_title_gone:
                close();
                break;
            //连接蓝牙搜索设备
            case R.id.detail_bluetooth__tv_up:
                intent = new Intent(DetailActivity.this, BluetoothActivity.class);
                startActivityForResult(intent, Constant.BLUE_CON);
                break;
            //连接设备
            case R.id.detail_btn_connect:
                if (device == null || ("").equals(device)) {
                    intent = new Intent(DetailActivity.this, BluetoothActivity.class);
                    startActivityForResult(intent, Constant.BLUE_CON);
                } else {
                    connBlue(device);
                }
                break;
            //打印
            case R.id.detail_btn_print:
                if (getPrint()) {
                    final MyDialog confirmDialog = new MyDialog(DetailActivity.this, "提示", "打印完成!", "确定", "返回", false, false);
                    confirmDialog.show();
                    confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm(String result) {

                        }

                        @Override
                        public void doConfirm() {
                            getPrint();
                            confirmDialog.dismiss();
                        }

                        @Override
                        public void doCancel() {
                            confirmDialog.dismiss();
                        }
                    });
                }
        }
    }

    /**
     * 打印成功
     * @return 成功再次打印
     */
    private boolean getPrint(){
    if (flag) {
        String orderDate = tvDate.getText().toString();
        String store = tvStore.getText().toString();
        String address = tvAddress.getText().toString();
        String totalNumber = tvTotalNumber.getText().toString();
        String totalPrice = tvTotalPrice.getText().toString();
        String pay = tvPay.getText().toString();
        String model = tvModel.getText().toString();
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String printDate =  sdt.format(new Date());
        printDataService.selectCommand(PrintDataService.RESET);
        printDataService.selectCommand(PrintDataService.LINE_SPACING_DEFAULT);
        printDataService.selectCommand(PrintDataService.ALIGN_CENTER);
        printDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
        printDataService.printText("\n");
        printDataService.printText("订单详情\n\n");
        printDataService.selectCommand(PrintDataService.NORMAL);
        printDataService.selectCommand(PrintDataService.ALIGN_LEFT);
        SharedPreferences spfName = getSharedPreferences("user", MODE_PRIVATE);
        String userName = spfName.getString("userCode", "");
        printDataService.printText(printDataService.printTwoData("订单编号:", orderNum + "\n"));
        printDataService.printText(printDataService.printTwoData("订单时间:", orderDate + "\n"));
        printDataService.printText(printDataService.printTwoData("商户名称:", store + "\n"));
        printDataService.printText(printDataService.printTwoData("商户地址:", address + "\n"));
        printDataService.printText("--------------------------------\n");
        printDataService.selectCommand(PrintDataService.BOLD);
        printDataService.printText(printDataService.printThreeData("商品", "数量", "金额\n"));
        printDataService.selectCommand(PrintDataService.BOLD_CANCEL);
        if (dataLists.size() > 0) {
            for (int i = 0; i < dataLists.size(); i++) {
                String itemCode = dataLists.get(i).get("itemCode").toString();
                printDataService.printText(itemCode + "\n");
                String tempNumber = dataLists.get(i).get("number").toString();
                String price = dataLists.get(i).get("itemAmt").toString();
                String itemPrice = dataLists.get(i).get("price").toString();
                String name = dataLists.get(i).get("name").toString();
                printDataService.printText(printDataService.printThreeData(name, tempNumber + " * " + itemPrice, price + "\n"));
            }
        }
        printDataService.printText("--------------------------------");
        printDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT);
        printDataService.selectCommand(PrintDataService.ALIGN_RIGHT);
        printDataService.selectCommand(PrintDataService.LINE_SPACING_DEFAULT);
        printDataService.printText(printDataService.printTwoRightData("商品数量", totalNumber + "\n"));
        printDataService.printText(printDataService.printTwoRightData("商品总额", totalPrice + "\n"));
        printDataService.printText(printDataService.printTwoRightData("实收金额", totalPrice + "\n"));
        printDataService.selectCommand(PrintDataService.NORMAL);
        printDataService.selectCommand(PrintDataService.ALIGN_LEFT);
        printDataService.printText("--------------------------------\n");
        printDataService.printText(printDataService.printTwoData("付款方式:", pay + "\n"));
        printDataService.printText(printDataService.printTwoData("支付方式:", model + "\n"));
        printDataService.printText("--------------------------------\n");
            printDataService.printText(printDataService.printTwoData("打印时间:", printDate + "\n"));
        printDataService.printText(printDataService.printTwoData("经办人:", userName + "\n"));
        printDataService.printText(printDataService.printTwoData("买家签字:", "\n\n\n"));
        printDataService.printText("----------双汇软件制作----------\n");
        printDataService.printText("\n\n\n\n");
        return true;
    }else{
        Toast.makeText(DetailActivity.this,"请连接设备!",Toast.LENGTH_SHORT).show();
        return false;
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.BLUE_CON) {
            if (resultCode == RESULT_OK) {
                String deviceAddress = data.getStringExtra("deviceAddress");
                Log.d("TAG",deviceAddress);
                connBlue(deviceAddress);
            }
        }
    }

    /**
     * 连接蓝牙
     * @param device 蓝牙地址
     */
    public void connBlue(String device){
        printDataService = new PrintDataService(this, device);
        flag = printDataService.connect();
        if (flag){
            btnPrint.setBackgroundColor(Color.parseColor("#e84232"));
            btnPrint.setTextColor(Color.parseColor("#ffffff"));
            btnPrint.setEnabled(true);
            BluetoothDevice name = printDataService.device;
            tvBlueName.setText(name.getName());
            tvBlueName.setTextColor(Color.parseColor("#0066ff"));
        }
    }
    /**
     * 返回back
     */
    private void close(){
        intent = new Intent(DetailActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //closeBluetooth();
        if (flag) {
            PrintDataService.disconnect();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (("sale").equals(sale)){
                finish();
            }else {
                close();
            }
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //closeBluetooth();
        if (flag) {
            PrintDataService.disconnect();
        }
        super.onDestroy();
    }

}
