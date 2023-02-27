package com.sw.mobsale.online.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.AddressAdapter;
import com.sw.mobsale.online.ui.AddressActivity;
import com.sw.mobsale.online.ui.ChangeUserActivity;
import com.sw.mobsale.online.ui.ConnectActivity;
import com.sw.mobsale.online.ui.LoadingActivity;
import com.sw.mobsale.online.ui.MainActivity;
import com.sw.mobsale.online.ui.ResProductActivity;
import com.sw.mobsale.online.ui.RetailActivity;
import com.sw.mobsale.online.ui.WorkCarActivity;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyApplication;
import com.sw.mobsale.online.util.MyLoc;
import com.sw.mobsale.online.util.MyScrollView;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 主Fragment
 */
public class MainFragment extends Fragment implements View.OnClickListener{
    //布局
    private MyScrollView scrollView;
    private RelativeLayout rlLoading, rlRes, rlConnect, rlSell,rlNoNet; //装车 库存 交班 铺货 无网络
    private ImageView ivNetSet,ivNetRefrush;
    private GifImageView ivStatus;//状态
    private LinearLayout llUser,llNote;// 切换用户 说明
    private TextView tvSeller,tvSellerPhone;
    public ListView lv;
    private TextView tvRegStore,tvUser,tvUserName,tvCar,tvWorker; //订单商铺 登陆用户 业务员 车辆
    private RelativeLayout rlPoi,rlOrder; //附近店铺,线路终端
    //数据
    private Intent intent;
    public ArrayList<Map<String, Object>> storeLists = new ArrayList<Map<String, Object>>();//附近店铺
//    public List<Map<String, Object>> car = new ArrayList<Map<String, Object>>();//车辆信息
   //车号
//    private String[] carNum;
//    public List<Map<String, Object>> workers = new ArrayList<Map<String, Object>>();//司机信息
//    //司机
//    private String[] workerNum;
    //网络返回状态
    private String result = "";
    private MyHandler handler;
    //获取登录帐号，密码，设备号
    private String phoneCode,name,userPwd;
    //切换用户帐号，密码
//    private String inputUser,inputPwd;
//    //默认车辆
//    private String itemCar;
//    private int carId = -1;
//    private String itemCarid = "";
    private String submitCarId = "";
    //默认司机
//    private String itemWork;
//    private int workId = -1;
//    private String itemWorkid = "";
    private String submitWorkId = "";
    private String defaultCar = "点击选择车辆";
    private String defaultWorker = "点击选择司机";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    /**
    * 初始化标签
    */
    public void initView(View view) {
        llNote = (LinearLayout) view.findViewById(R.id.main_ll_note);
        llUser = (LinearLayout) view.findViewById(R.id.main_ll_user);
        ivStatus = (GifImageView) view.findViewById(R.id.main_iv_status);
        tvUser = (TextView) view.findViewById(R.id.main_tv_user);
        tvUserName = (TextView) view.findViewById(R.id.main_tv_user_name);
        tvCar = (TextView) view.findViewById(R.id.main_tv_car);
        tvWorker = (TextView) view.findViewById(R.id.main_tv_worker);
        rlLoading = (RelativeLayout) view.findViewById(R.id.main_rl_loading);
        rlRes = (RelativeLayout) view.findViewById(R.id.main_rl_res);
        rlConnect = (RelativeLayout) view.findViewById(R.id.main_rl_connect);
        rlSell = (RelativeLayout) view.findViewById(R.id.main_rl_sale);
        rlNoNet = (RelativeLayout) view.findViewById(R.id.main_rl_net_bug);
        scrollView = (MyScrollView) view.findViewById(R.id.main_sc_gone);
        tvSeller = (TextView) view.findViewById(R.id.main_tv_seller_name);
        tvSellerPhone = (TextView) view.findViewById(R.id.main_tv_seller_phone);
        lv = (ListView) view.findViewById(R.id.main_lv_nearby);
        ivNetRefrush = (ImageView) view.findViewById(R.id.main_net_refrush_iv);
        ivNetSet = (ImageView) view.findViewById(R.id.main_net_set_iv);
        rlPoi = (RelativeLayout) view.findViewById(R.id.fg_main_rl_nearby);
        rlOrder = (RelativeLayout) view.findViewById(R.id.fg_main_rl_order);
        tvRegStore = (TextView) view.findViewById(R.id.main_tv_near);
        MyLoc loc = new MyLoc(getActivity());
        loc.getPoi();
        handler = new MyHandler();
        getSpfData();
        Intent intent= getActivity().getIntent();
        String network = intent.getStringExtra("network");
        if (network == null||("").equals(network)){
            scrollView.setVisibility(View.VISIBLE);
            rlNoNet.setVisibility(View.GONE);
            //判断用户状态  是否绑车
            MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.LOGIN_DETAIL_PATH,Constant.MAIN_DETAIL,"","","","");
        }else{
            scrollView.setVisibility(View.GONE);
            rlNoNet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取SharedPreferences
     */
    public void getSpfData(){
        SharedPreferences spf = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        name = spf.getString("userCode","");
        userPwd = spf.getString("password","");
        phoneCode = spf.getString("phoneCode","");
        tvUser.setText(name);
        Log.d("TAG","name" + name +" pwd"+ userPwd +  "code" + phoneCode);
//        if (name == "" || ("").equals(name)){
//            rlLoading.setEnabled(false);
//            rlConnect.setEnabled(false);
//            tvUser.setText("请登录");
//        }else {
//            tvUser.setText(name);
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //事件监听
        llUser.setOnClickListener(this);
        tvCar.setOnClickListener(this);
        tvWorker.setOnClickListener(this);
        rlLoading.setOnClickListener(this);
        rlRes.setOnClickListener(this);
        rlConnect.setOnClickListener(this);
        rlSell.setOnClickListener(this);
        ivNetSet.setOnClickListener(this);
        ivNetRefrush.setOnClickListener(this);
        rlPoi.setOnClickListener(this);
        rlOrder.setOnClickListener(this);
        //listview条目单击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String store = storeLists.get(position).get("customerName").toString();
                String address = storeLists.get(position).get("address").toString();
                Intent intent = new Intent(getActivity(), RetailActivity.class);
                intent.putExtra("customerName",store);
                intent.putExtra("address",address);
                intent.putExtra("orderType","R");
                startActivity(intent);
            }
        });
    }

    /**
     * 是否开始工作
     * @param car car
     * @param worker worker
     * @return boolean
     */
    public boolean isStart(String car ,String worker){
        if ((defaultCar).equals(car) ||(defaultWorker).equals(worker)){
            if ((defaultCar).equals(car)) {
                Toast.makeText(getActivity(), "请绑定车辆",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "请绑定司机",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String car = tvCar.getText().toString();
        String name = tvUser.getText().toString();
        String worker = tvWorker.getText().toString();
        switch (v.getId()) {
            //切换用户
            case R.id.main_ll_user:
                intent = new Intent(getActivity(),ChangeUserActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("pwd",userPwd);
                intent.putExtra("code",phoneCode);
                startActivity(intent);
               // showInputDiaLog();
                break;
            //选择车辆
            case R.id.main_tv_car:
                getWorkCar(car,submitCarId,"car");
                //MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.CARNUM_PATH,Constant.CAR_NO,name,userPwd,phoneCode,"","","","");
                break;
            //选择司机
            case R.id.main_tv_worker:
                getWorkCar(worker,submitWorkId,"work");
                //MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.WORKER_PATH,Constant.WORKER_NO,name,userPwd,phoneCode,"","","","");
                break;
            //装车
            case R.id.main_rl_loading:
                if (isStart(car,worker)){
                    MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_F,"", "F", "","","");
                }
                break;
            //库存
            case R.id.main_rl_res:
                if (isStart(car,worker)){
                    intent = new Intent(getActivity(), ResProductActivity.class);
                    startActivity(intent);
                }
                break;
            //交班
            case R.id.main_rl_connect:
                if (isStart(car,worker)){
                    MyThread.getInstance(getActivity()).ConnectThread(handler,Constant.CONNECT_A,"A");
                }
                break;
            //铺货
            case R.id.main_rl_sale:
                if (isStart(car,worker)){
                    intent = new Intent(getActivity(), RetailActivity.class);
                    intent.putExtra("customerName","零售客户");
                    intent.putExtra("address","");
                    intent.putExtra("phone","");
                    startActivity(intent);
                }
                break;
            //附近店铺
            case R.id.fg_main_rl_nearby:
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                startActivity(intent);
                break;
            //订单商铺
            case R.id.fg_main_rl_order:
                tvRegStore.setTextColor(Color.parseColor("#d84232"));
                //MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_I,"", "I", "","","");
                MyThread.getInstance(getActivity()).RouteThread(handler,Constant.LINE_ROUTE_PATH_STORE);
                break;
            //刷新
            case R.id.main_net_refrush_iv:
                if(MyApplication.isNetworkAvailable(getActivity())) {
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    Toast.makeText(getActivity(),"无网络连接",Toast.LENGTH_SHORT).show();
                }
                break;
            //设置网络
            case R.id.main_net_set_iv:
                //跳转到设置页面
                startActivity(new Intent((Settings.ACTION_SETTINGS)));
                break;

        }
    }

    /**
     * 车辆 司机
     * @param text 文本内容
     * @param workCar 标识
     */
    private void getWorkCar(String text,String oldWorkCar,String workCar){
        intent = new Intent(getActivity(),WorkCarActivity.class);
//        intent.putExtra("name",name);
//        intent.putExtra("pwd",userPwd);
//        intent.putExtra("code",phoneCode);
        intent.putExtra("oldWorkCar",oldWorkCar);
        intent.putExtra("workCar",workCar);
        intent.putExtra("text",text);
        startActivityForResult(intent,Constant.WORKER_CAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.WORKER_CAR){
           if (resultCode == getActivity().RESULT_OK){
               MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.LOGIN_DETAIL_PATH,Constant.MAIN_DETAIL,"","","","");
           }
        }
    }

    /**
     * 输入对话框
     */
//    private void showInputDiaLog() {
//        View textEntryView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_login, null);
//        final EditText etUser = (EditText) textEntryView.findViewById(R.id.et_userName);
//        final EditText etPwd = (EditText) textEntryView.findViewById(R.id.et_passwrod);
//        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity())
//                .setTitle("切换用户")
//                .setView(textEntryView)
//                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        inputUser = etUser.getText().toString();
//                        inputPwd = etPwd.getText().toString();
//                        if (inputUser.equals(name)){
//                            Toast.makeText(getActivity(),"该用户已登录",Toast.LENGTH_SHORT).show();
//                            etUser.setText("");
//                            etPwd.setText("");
//                            return;
//                        }
//                        //起线程  判断该帐号,密码 设备号是否匹配后台数据表
//                       MyThread.getInstance(getActivity()).BackThread(handler,name,userPwd,phoneCode);
//                    }
//                })
//                .setNegativeButton("取消", null);
//        dlg.setCancelable(false);
//        AlertDialog dialog = dlg.create();
//        dialog.setView(textEntryView,0,0,0,0);
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            public void onShow(DialogInterface dialog) {
//                InputMethodManager imm =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(etUser, InputMethodManager.SHOW_IMPLICIT);
//            }
//        });
//        dialog.show();
//    }

    /**
     * 车辆单选列表对话框
     */
//    private void showSingleChoiceDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("请选择车辆");
//        builder.setSingleChoiceItems(carNum,carId ,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemCar = carNum[which];
//                itemCarid = car.get(which).get("id").toString();
//                Log.d("TAG",itemCar + itemCarid);
//            }
//        });
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("TAG",itemCar + itemCarid);
//                Log.d("TAG","submitCarId ->"+submitCarId);
//                if (itemCarid == null ||("").equals(itemCarid)){
//                    Message message = new Message();
//                    message.what = Constant.NO_CAR;
//                    handler.sendMessage(message);
//                }else {
//                    if (!(itemCarid).equals(submitCarId)) {
//                        MyThread.getInstance(getActivity()).CarInfoThread(handler, Constant.CAR_CHOOSE_PATH, Constant.SUBMIT_CAR, name, userPwd, phoneCode, submitCarId, itemCarid,"","");
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemCar = "";
//                itemCarid = "";
//            }
//        });
//        builder.create();
//        builder.show();
//    }

    /**
     * 司机单选列表对话框
     */
//    private void showWorkerSingleChoiceDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("请选择司机");
//        builder.setSingleChoiceItems(workerNum,workId ,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemWork = workerNum[which];
//                itemWorkid = workers.get(which).get("id").toString();
//                Log.d("TAG",itemWork + itemWorkid);
//            }
//        });
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("TAG",itemWork + itemWorkid);
//                Log.d("TAG","submitWorkId ->"+submitCarId);
//                if (itemWorkid == null ||("").equals(itemWorkid)){
//                    Message message = new Message();
//                    message.what = Constant.NO_WORKER;
//                    handler.sendMessage(message);
//                }else {
//                    if (!(itemWorkid).equals(submitWorkId)) {
//                        MyThread.getInstance(getActivity()).CarInfoThread(handler, Constant.WORKER_CHOOSE_PATH, Constant.WORKER_NO_SUBMIT, name, userPwd, phoneCode,"","",submitWorkId,itemWorkid);
//                    }
//                }
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemWork = "";
//                itemWorkid = "";
//            }
//        });
//        builder.create();
//        builder.show();
//    }

    /**
     * 确定 车辆 司机
     * @param text toast
     */
//    public void CarWorker(String result,String text){
//        try {
//            JSONObject object = new JSONObject(result);
//            String message = object.getString("errorMessage");
//            if (("ok").equals(message)) {
////                            tvCar.setText(itemCar);
////                            tvCar.setTextColor(Color.parseColor("#333333"));
////                            submitCarId = itemCarid;
////                            carId = 0;
//                MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.LOGIN_DETAIL_PATH,Constant.MAIN_DETAIL,name,userPwd,phoneCode,"","","","");
//            } else {
//                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * handler
     */
    class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //进入页面 判断用户是否绑定车辆
                case Constant.MAIN_DETAIL:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "MAIN DETAIL->" + result);
                   if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                       try {
                           JSONObject object = new JSONObject(result);
                           JSONArray array = object.getJSONArray("rows");
                           for (int i = 0; i < array.length(); i++) {
                               JSONObject jo = array.getJSONObject(i);
                               String carNum = jo.getString("carnum");
                               String terminalName = jo.getString("terminalname");
                               submitCarId = jo.getString("carnumid");
                               submitWorkId = jo.getString("cardriverid");
                               String worker = jo.getString("drivername");
                               tvUserName.setText(terminalName);
                               scrollView.setVisibility(View.VISIBLE);
                               rlNoNet.setVisibility(View.GONE);
                               if (carNum == null || ("").equals(carNum)) {
                                   tvCar.setText(defaultCar);
                                   tvCar.setTextColor(Color.parseColor("#e84232"));
//                                   carId = -1;
                               } else {
                                   tvCar.setText(carNum);
                                   tvCar.setTextColor(Color.parseColor("#333333"));
//                                   carId = 0;
                               }
                               if (worker == null || ("").equals(worker)) {
                                   tvWorker.setText(defaultWorker);
                                   tvWorker.setTextColor(Color.parseColor("#e84232"));
//                                   workId = -1;
                               } else {
                                   tvWorker.setText(worker);
                                   tvWorker.setTextColor(Color.parseColor("#333333"));
//                                   workId = 0;
                               }
                               String statusCode = jo.getString("statuscode");
                               if (("S").equals(statusCode)) {
                                   // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
                                   // 将gif图资源转化为GifDrawable
                                   GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.main_status_start);
                                   // gif1加载一个动态图gif
                                   ivStatus.setImageDrawable(gifDrawable);
                                   tvWorker.setEnabled(false);
                                   tvCar.setEnabled(false);
                               } else {
                                   GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.main_status_n);
                                   // gif1加载一个动态图gif
                                   ivStatus.setImageDrawable(gifDrawable);
                               }
                               String seller = jo.getString("sellername");
                               String sellerPhone = jo.getString("mobileno");
                               tvSeller.setText(seller);
                               tvSellerPhone.setText(sellerPhone);
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                    break;
//                //单选车辆列表
//                case Constant.CAR_NO:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-car ->" + result);
//                        car.clear();
//                        car = ResultLog.getInstance(getActivity()).CarNo(result);
//                        carNum = new String[car.size()];
//                        for (int a = 0, b = car.size(); a < b; a++) {
//                            carNum[a] = car.get(a).get("carnum").toString();
//                        }
//                        showSingleChoiceDialog();
//                    }
//                    break;
//                //车辆单选框确定
//                case Constant.SUBMIT_CAR:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-submit car ->" + result);
//                        CarWorker(result, "该车辆被占用!");
//                    }
//                    break;
//                //车辆空确定
//                case Constant.NO_CAR:
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        if (carId == -1) {
//                            Toast.makeText(getActivity(), "请选择车辆", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                //司机空确定
//                case Constant.NO_WORKER:
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        if (workId == -1) {
//                            Toast.makeText(getActivity(), "请选择司机", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                //单选司机列表
//                case Constant.WORKER_NO:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-worker ->" + result);
//                        workers.clear();
//                        workers = ResultLog.getInstance(getActivity()).Worker(result);
//                        workerNum = new String[workers.size()];
//                        for (int a = 0, b = workers.size(); a < b; a++) {
//                            workerNum[a] = workers.get(a).get("drivername").toString();
//                        }
//                        showWorkerSingleChoiceDialog();
//                    }
//                    break;
//                //司机单选框确定
//                case Constant.WORKER_NO_SUBMIT:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-submit worker ->" + result);
//                        CarWorker(result, "该司机被占用!");
//                    }
//                    break;
//                // 切换用户 ->用户退出
//                case Constant.USER_BACK:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        try {
//                            JSONObject object = new JSONObject(result);
//                            String message = object.getString("errorMessage");
//                            if (message.equals("offLine")) {
//                                MyThread.getInstance(getActivity()).LoginThread(handler, inputUser, inputPwd, phoneCode);
//                            } else {
//                                Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                // 切换用户 ->用户登陆
//                case Constant.USER_LOGIN:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        ResultLog.getInstance(getActivity()).LoginLog(result, getActivity(), inputUser, inputPwd, phoneCode);
//                    }
//                    break;

                //装车前判断用户状态
                case Constant.ORDER_STATUS_F:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            Log.d("TAG", message);
                            if (message.equals("ok")) {
                                intent = new Intent(getActivity(), LoadingActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "已出发!不能再次装车!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //交班前判断用户是否有销售记录
                case Constant.CONNECT_A:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            Log.d("TAG", message);
                            if (message.equals("err")) {
                                Toast.makeText(getActivity(), "无销售数据不准许交班!", Toast.LENGTH_SHORT).show();
                            } else {
                                intent = new Intent(getActivity(), ConnectActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //订单商铺
                //case Constant.ORDER_STATUS_I:
                case Constant.ROUTE_LINE:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        Log.d("TAG", "route store->" + result);
                        storeLists.clear();
                        storeLists = ResultLog.getInstance(getActivity()).RouteStore(result);
                        if (storeLists.size() > 0) {
                            lv.setAdapter(new AddressAdapter(getActivity(), storeLists));
                            llNote.setVisibility(View.GONE);
                        } else {
                            tvRegStore.setTextColor(Color.parseColor("#454545"));
                            Toast.makeText(getActivity(), "无商铺", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
