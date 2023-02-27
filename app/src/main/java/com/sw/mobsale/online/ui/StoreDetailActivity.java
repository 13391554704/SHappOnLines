package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 零售客户
 */
public class StoreDetailActivity extends Activity {
    //界面
    private ListView lvStore;
    private List<Map<String, Object>> dataLists;
    private Button btnRe;
    private LinearLayout llTop;
    private MyHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_detail);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        lvStore = (ListView) findViewById(R.id.store_detail_lv);
        btnRe = (Button) findViewById(R.id.store_detail_btn_re);
        llTop = (LinearLayout) findViewById(R.id.store_detail_ll);
        dataLists = new ArrayList<Map<String, Object>>();
        handler = new MyHandler();
        MyThread.getInstance(StoreDetailActivity.this).AddressThread(handler,"I","","","",Constant.STORE_QUERY);
    }



    @Override
    protected void onResume() {
        super.onResume();
        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                it.putExtra("name", dataLists.get(position).get("name").toString());
                it.putExtra("address",dataLists.get(position).get("address").toString());
                it.putExtra("phone", dataLists.get(position).get("phone").toString());
                it.putExtra("province", dataLists.get(position).get("province").toString());
                it.putExtra("city", dataLists.get(position).get("city").toString());
                it.putExtra("country", dataLists.get(position).get("country").toString());
                setResult(RESULT_OK, it);
                finish();
            }
        });
        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StoreDetailActivity.this, "点击弹出框外部关闭窗口~", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //未完成配送商户数量
                case Constant.STORE_QUERY:
                    String result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(StoreDetailActivity.this).getNoRes(result)) {
                        Log.d("TAG", " store result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("rows");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = array.getJSONObject(i);
                                String name = jo.getString("buyername");
                                String address = jo.getString("addr");
                                String phone = jo.getString("mobileno");
                                String province = jo.getString("provinceareaid");
                                String city = jo.getString("cityareaid");
                                String country = jo.getString("countyareaid");
                                String code = jo.getString("buyercode");
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("name", name);
                                map.put("address", address);
                                map.put("phone", phone);
                                map.put("province", province);
                                map.put("city", city);
                                map.put("country", country);
                                map.put("code", code);
                                dataLists.add(map);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (dataLists.size() > 0) {
                            lvStore.setAdapter(new StoreDetailAdapter());
                        }
                    }
                    break;

            }
        }
    }
    /**
     * adapter
     */
    class StoreDetailAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return dataLists.size();
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
                convertView = LayoutInflater.from(StoreDetailActivity.this).inflate(R.layout.item_store_detail,parent,false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_store_detail_tv_store);
                viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.item_store_detail_tv_address);
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.item_store_detail_tv_phone);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvName.setText((String) dataLists.get(position).get("name"));
            viewHolder.tvAddress.setText((String)dataLists.get(position).get("address"));
            viewHolder.tvNumber.setText((String)dataLists.get(position).get("phone"));
            return convertView;
        }
        class ViewHolder{
            TextView tvName;
            TextView tvAddress;
            TextView tvNumber;

        }
    }

    //重写此方法用来设置当点击activity外部时候，关闭此弹出框
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        finish();
        return true;
    }
    //此方法在布局文件中定义，用来保证点击弹出框内部的时候不会被关闭，如果不设置此方法则单击弹出框内部时候会导致弹出框关闭
    /*
    public void tip(View view)
    {
        Toast.makeText(this, "点击弹出框外部关闭窗口~", Toast.LENGTH_SHORT).show();
    }
    */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
