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
 * ???Fragment
 */
public class MainFragment extends Fragment implements View.OnClickListener{
    //??????
    private MyScrollView scrollView;
    private RelativeLayout rlLoading, rlRes, rlConnect, rlSell,rlNoNet; //?????? ?????? ?????? ?????? ?????????
    private ImageView ivNetSet,ivNetRefrush;
    private GifImageView ivStatus;//??????
    private LinearLayout llUser,llNote;// ???????????? ??????
    private TextView tvSeller,tvSellerPhone;
    public ListView lv;
    private TextView tvRegStore,tvUser,tvUserName,tvCar,tvWorker; //???????????? ???????????? ????????? ??????
    private RelativeLayout rlPoi,rlOrder; //????????????,????????????
    //??????
    private Intent intent;
    public ArrayList<Map<String, Object>> storeLists = new ArrayList<Map<String, Object>>();//????????????
//    public List<Map<String, Object>> car = new ArrayList<Map<String, Object>>();//????????????
   //??????
//    private String[] carNum;
//    public List<Map<String, Object>> workers = new ArrayList<Map<String, Object>>();//????????????
//    //??????
//    private String[] workerNum;
    //??????????????????
    private String result = "";
    private MyHandler handler;
    //???????????????????????????????????????
    private String phoneCode,name,userPwd;
    //???????????????????????????
//    private String inputUser,inputPwd;
//    //????????????
//    private String itemCar;
//    private int carId = -1;
//    private String itemCarid = "";
    private String submitCarId = "";
    //????????????
//    private String itemWork;
//    private int workId = -1;
//    private String itemWorkid = "";
    private String submitWorkId = "";
    private String defaultCar = "??????????????????";
    private String defaultWorker = "??????????????????";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    /**
    * ???????????????
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
            //??????????????????  ????????????
            MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.LOGIN_DETAIL_PATH,Constant.MAIN_DETAIL,"","","","");
        }else{
            scrollView.setVisibility(View.GONE);
            rlNoNet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ??????SharedPreferences
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
//            tvUser.setText("?????????");
//        }else {
//            tvUser.setText(name);
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //????????????
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
        //listview??????????????????
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
     * ??????????????????
     * @param car car
     * @param worker worker
     * @return boolean
     */
    public boolean isStart(String car ,String worker){
        if ((defaultCar).equals(car) ||(defaultWorker).equals(worker)){
            if ((defaultCar).equals(car)) {
                Toast.makeText(getActivity(), "???????????????",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "???????????????",Toast.LENGTH_SHORT).show();
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
            //????????????
            case R.id.main_ll_user:
                intent = new Intent(getActivity(),ChangeUserActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("pwd",userPwd);
                intent.putExtra("code",phoneCode);
                startActivity(intent);
               // showInputDiaLog();
                break;
            //????????????
            case R.id.main_tv_car:
                getWorkCar(car,submitCarId,"car");
                //MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.CARNUM_PATH,Constant.CAR_NO,name,userPwd,phoneCode,"","","","");
                break;
            //????????????
            case R.id.main_tv_worker:
                getWorkCar(worker,submitWorkId,"work");
                //MyThread.getInstance(getActivity()).CarInfoThread(handler,Constant.WORKER_PATH,Constant.WORKER_NO,name,userPwd,phoneCode,"","","","");
                break;
            //??????
            case R.id.main_rl_loading:
                if (isStart(car,worker)){
                    MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_F,"", "F", "","","");
                }
                break;
            //??????
            case R.id.main_rl_res:
                if (isStart(car,worker)){
                    intent = new Intent(getActivity(), ResProductActivity.class);
                    startActivity(intent);
                }
                break;
            //??????
            case R.id.main_rl_connect:
                if (isStart(car,worker)){
                    MyThread.getInstance(getActivity()).ConnectThread(handler,Constant.CONNECT_A,"A");
                }
                break;
            //??????
            case R.id.main_rl_sale:
                if (isStart(car,worker)){
                    intent = new Intent(getActivity(), RetailActivity.class);
                    intent.putExtra("customerName","????????????");
                    intent.putExtra("address","");
                    intent.putExtra("phone","");
                    startActivity(intent);
                }
                break;
            //????????????
            case R.id.fg_main_rl_nearby:
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                startActivity(intent);
                break;
            //????????????
            case R.id.fg_main_rl_order:
                tvRegStore.setTextColor(Color.parseColor("#d84232"));
                //MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_I,"", "I", "","","");
                MyThread.getInstance(getActivity()).RouteThread(handler,Constant.LINE_ROUTE_PATH_STORE);
                break;
            //??????
            case R.id.main_net_refrush_iv:
                if(MyApplication.isNetworkAvailable(getActivity())) {
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    Toast.makeText(getActivity(),"???????????????",Toast.LENGTH_SHORT).show();
                }
                break;
            //????????????
            case R.id.main_net_set_iv:
                //?????????????????????
                startActivity(new Intent((Settings.ACTION_SETTINGS)));
                break;

        }
    }

    /**
     * ?????? ??????
     * @param text ????????????
     * @param workCar ??????
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
     * ???????????????
     */
//    private void showInputDiaLog() {
//        View textEntryView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_login, null);
//        final EditText etUser = (EditText) textEntryView.findViewById(R.id.et_userName);
//        final EditText etPwd = (EditText) textEntryView.findViewById(R.id.et_passwrod);
//        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity())
//                .setTitle("????????????")
//                .setView(textEntryView)
//                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        inputUser = etUser.getText().toString();
//                        inputPwd = etPwd.getText().toString();
//                        if (inputUser.equals(name)){
//                            Toast.makeText(getActivity(),"??????????????????",Toast.LENGTH_SHORT).show();
//                            etUser.setText("");
//                            etPwd.setText("");
//                            return;
//                        }
//                        //?????????  ???????????????,?????? ????????????????????????????????????
//                       MyThread.getInstance(getActivity()).BackThread(handler,name,userPwd,phoneCode);
//                    }
//                })
//                .setNegativeButton("??????", null);
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
     * ???????????????????????????
     */
//    private void showSingleChoiceDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("???????????????");
//        builder.setSingleChoiceItems(carNum,carId ,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemCar = carNum[which];
//                itemCarid = car.get(which).get("id").toString();
//                Log.d("TAG",itemCar + itemCarid);
//            }
//        });
//        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
//        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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
     * ???????????????????????????
     */
//    private void showWorkerSingleChoiceDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("???????????????");
//        builder.setSingleChoiceItems(workerNum,workId ,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                itemWork = workerNum[which];
//                itemWorkid = workers.get(which).get("id").toString();
//                Log.d("TAG",itemWork + itemWorkid);
//            }
//        });
//        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
//        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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
     * ?????? ?????? ??????
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
                //???????????? ??????????????????????????????
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
                                   // ??????????????????gif??????????????????????????????gif?????????????????????GifDrawable
                                   // ???gif??????????????????GifDrawable
                                   GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.main_status_start);
                                   // gif1?????????????????????gif
                                   ivStatus.setImageDrawable(gifDrawable);
                                   tvWorker.setEnabled(false);
                                   tvCar.setEnabled(false);
                               } else {
                                   GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.main_status_n);
                                   // gif1?????????????????????gif
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
//                //??????????????????
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
//                //?????????????????????
//                case Constant.SUBMIT_CAR:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-submit car ->" + result);
//                        CarWorker(result, "??????????????????!");
//                    }
//                    break;
//                //???????????????
//                case Constant.NO_CAR:
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        if (carId == -1) {
//                            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                //???????????????
//                case Constant.NO_WORKER:
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        if (workId == -1) {
//                            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                //??????????????????
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
//                //?????????????????????
//                case Constant.WORKER_NO_SUBMIT:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        Log.d("TAG", "Main-submit worker ->" + result);
//                        CarWorker(result, "??????????????????!");
//                    }
//                    break;
//                // ???????????? ->????????????
//                case Constant.USER_BACK:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        try {
//                            JSONObject object = new JSONObject(result);
//                            String message = object.getString("errorMessage");
//                            if (message.equals("offLine")) {
//                                MyThread.getInstance(getActivity()).LoginThread(handler, inputUser, inputPwd, phoneCode);
//                            } else {
//                                Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//                // ???????????? ->????????????
//                case Constant.USER_LOGIN:
//                    result = msg.getData().getString("result");
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
//                        ResultLog.getInstance(getActivity()).LoginLog(result, getActivity(), inputUser, inputPwd, phoneCode);
//                    }
//                    break;

                //???????????????????????????
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
                                Toast.makeText(getActivity(), "?????????!??????????????????!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //??????????????????????????????????????????
                case Constant.CONNECT_A:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            Log.d("TAG", message);
                            if (message.equals("err")) {
                                Toast.makeText(getActivity(), "??????????????????????????????!", Toast.LENGTH_SHORT).show();
                            } else {
                                intent = new Intent(getActivity(), ConnectActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //????????????
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
                            Toast.makeText(getActivity(), "?????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
