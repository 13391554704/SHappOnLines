package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 司机 车辆
 */
public class WorkCarActivity extends Activity implements View.OnClickListener{
    private TextView tvTitle,tvNoData;
    private ListView lv;
    private Button btnRe,btnSub;
    private MyHandler handler;
    private String result;
    private List<Map<String, Object>> workCars = new ArrayList<Map<String, Object>>();//车辆信息
    private String userName,userPwd,phoneCode,workCar,text;
    private String itemWorkCar= "";
    private String itemWorkCarid="";
    private String oldWorkCar="";
    private WorkCarAdapter adapter;
    private Map<Integer,Boolean> flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_work_car);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.work_car_tv_title);
        lv = (ListView) findViewById(R.id.work_car_lv);
        tvNoData = (TextView) findViewById(R.id.work_car_no);
        btnRe = (Button) findViewById(R.id.work_car_clear);
        btnSub = (Button) findViewById(R.id.work_car_submit);
        flag = new HashMap<Integer, Boolean>();
        adapter = new WorkCarAdapter();
        handler = new MyHandler();
        Intent intent = getIntent();
//        userName = intent.getStringExtra("name");
//        userPwd = intent.getStringExtra("pwd");
//        phoneCode =intent.getStringExtra("code");
        oldWorkCar = intent.getStringExtra("oldWorkCar");
        text = intent.getStringExtra("text");
        workCar = intent.getStringExtra("workCar");
        if (workCar.equals("work")){
            tvTitle.setText("选择司机");
            MyThread.getInstance(WorkCarActivity.this).CarInfoThread(handler,Constant.WORKER_PATH,Constant.WORKER_NO,"","","","");
        }else{
            tvTitle.setText("选择车辆");
            MyThread.getInstance(WorkCarActivity.this).CarInfoThread(handler,Constant.CARNUM_PATH,Constant.CAR_NO,"","","","");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRe.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.contain(position)){
                    adapter.remove();
                    itemWorkCar = "";
                    itemWorkCarid = "";
                }else{
                    adapter.remove();
                    adapter.add(position);
                    if (workCar.equals("work")){
                        itemWorkCar = workCars.get(position).get("drivername").toString();
                        itemWorkCarid = workCars.get(position).get("id").toString();
                    }else{
                        itemWorkCar = workCars.get(position).get("carnum").toString();
                        itemWorkCarid = workCars.get(position).get("id").toString();
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d("TAG","name"+itemWorkCar);
                Log.d("TAG","id"+itemWorkCarid);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.work_car_submit:
                if (workCar.equals("work")){
                    if (flag.size()==0){
                        Toast.makeText(WorkCarActivity.this,"请选择司机!",Toast.LENGTH_SHORT).show();
                    } else {
                        if (("").equals(itemWorkCarid)||(itemWorkCarid).equals(oldWorkCar)){
                            finish();
                        }else {
                            MyThread.getInstance(WorkCarActivity.this).CarInfoThread(handler, Constant.WORKER_CHOOSE_PATH, Constant.WORKER_NO_SUBMIT,"","",oldWorkCar,itemWorkCarid);
                        }
                    }
                }else {
                    if (flag.size()==0){
                        Toast.makeText(WorkCarActivity.this,"请选择车辆!",Toast.LENGTH_SHORT).show();
                    } else {
                        if (("").equals(itemWorkCarid)||(itemWorkCarid).equals(oldWorkCar)){
                            finish();
                        }else {
                            MyThread.getInstance(WorkCarActivity.this).CarInfoThread(handler, Constant.CAR_CHOOSE_PATH, Constant.SUBMIT_CAR,  oldWorkCar, itemWorkCarid, "", "");
                        }
                    }
                }
                break;
            case R.id.work_car_clear:
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
            switch (msg.what) {
                //单选车辆列表
                case Constant.CAR_NO:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "Main-car ->" + result);
                    if(!ResultLog.getInstance(WorkCarActivity.this).getNoRes(result)) {
                        workCars.clear();
                        workCars = ResultLog.getInstance(WorkCarActivity.this).CarNo(result);
                        if (workCars.size() > 0) {
                            for (int i = 0; i < workCars.size(); i++) {
                                String name = workCars.get(i).get("carnum").toString();
                                if (name.equals(text)) {
                                    flag.put(0, true);
                                }
                            }
                            lv.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                            lv.setAdapter(adapter);
                        }else{
                            tvNoData.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.GONE);
                            tvNoData.setText("经销商无可用车辆!");
                        }
                    }
                    break;
                //单选司机列表
                case Constant.WORKER_NO:
                    result = msg.getData().getString("result");
                    Log.d("TAG", "Main-worker ->" + result);
                    if(!ResultLog.getInstance(WorkCarActivity.this).getNoRes(result)) {
                        workCars.clear();
                        workCars = ResultLog.getInstance(WorkCarActivity.this).Worker(result);
                        if (workCars.size() > 0) {
                            for (int i = 0; i < workCars.size(); i++) {
                                String name = workCars.get(i).get("drivername").toString();
                                if (name.equals(text)) {
                                    flag.put(0, true);
                                }
                            }
                            lv.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                            lv.setAdapter(adapter);
                        }else{
                            tvNoData.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.GONE);
                            tvNoData.setText("经销商无可用司机!");
                        }
                    }
                    break;
                //车辆单选框确定
                case Constant.SUBMIT_CAR:
                    result = msg.getData().getString("result");
                    submitText(result, "该车辆被占用!");
                    break;
                //司机单选框确定
                case Constant.WORKER_NO_SUBMIT:
                    result = msg.getData().getString("result");
                    submitText(result,"该司机被占用!");
                    break;
            }
        }
    }

    /**
     * 确定
     * @param result 消息
     */
    public void submitText(String result,String text){
        if(!ResultLog.getInstance(WorkCarActivity.this).getNoRes(result)) {
            Log.d("TAG", "submit workCar->" + result);
            try {
                JSONObject object = new JSONObject(result);
                String message = object.getString("errorMessage");
                if (("ok").equals(message)) {
                    Intent intent = new Intent();
                    intent.putExtra("workCar",itemWorkCar);
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Toast.makeText(WorkCarActivity.this,text,Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * adapter
     */
    class WorkCarAdapter extends BaseAdapter{

        public boolean contain(int position){
            return flag.containsKey(position);
        }
        public void add(int position){
            flag.put(position,true);
        }
        public void remove(){
            flag.clear();
        }
        @Override
        public int getCount() {
            return workCars.size();
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
            MyHolder holder;
            if (convertView == null){
                holder = new MyHolder();
                convertView = LayoutInflater.from(WorkCarActivity.this).inflate(R.layout.item_work_car,parent,false);
                holder.iv = (ImageView)convertView.findViewById(R.id.item_work_car_iv);
                holder.tv = (TextView) convertView.findViewById(R.id.item_work_car_tv);
                convertView.setTag(holder);
            }else{
                holder = (MyHolder) convertView.getTag();
            }
            if (flag.containsKey(position)){
                holder.iv.setImageResource(R.drawable.checked);
            }else{
                holder.iv.setImageResource(R.drawable.manager_choose);
            }
            if (workCar.equals("work")){
                holder.tv.setText(workCars.get(position).get("drivername").toString());
            }else {
                holder.tv.setText(workCars.get(position).get("carnum").toString());
            }
            return convertView;
        }

       class MyHolder{
           ImageView iv;
           TextView tv;
       }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
