package com.sw.mobsale.online.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 零售地址
 */
public class QueryAddrActivity extends Activity implements View.OnClickListener{
    private Button btnRe,btnSubmit;
    private GridView gv;
    private TextView tvP,tvCity,tvCountry;
    private EditText etAddr;
    private List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();//存数据
    private String province = "";
    private String city = "";
    private String country = "";
    private String jsAddress = "";
    private MyHandler handler;
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_query_addr);
        initView();
    }

    private void initView() {
        tvP = (TextView)findViewById(R.id.item_addr_p);
        tvCity = (TextView)findViewById(R.id.item_addr_city);
        tvCountry = (TextView)findViewById(R.id.item_addr_country);
        etAddr = (EditText)findViewById(R.id.item_addr_et);
        gv = (GridView)findViewById(R.id.query_gv);
        btnRe = (Button) findViewById(R.id.query_btn_re);
        btnSubmit = (Button) findViewById(R.id.query_btn_submit);
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        etAddr.setText(address);
        handler = new MyHandler();
        CharSequence text = etAddr.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            //移动到字体后面
            Selection.setSelection(spanText, text.length());
        }
        tvP.setBackgroundResource(R.drawable.dia_text_bg_selected);
        MyThread.getInstance(QueryAddrActivity.this).AddressThread(handler,"M","","P","", Constant.ADDRESS_P);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRe.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_btn_re:
                finish();
                break;
            case R.id.item_addr_p:
                MyThread.getInstance(QueryAddrActivity.this).AddressThread(handler,"M","","P","", Constant.ADDRESS_P);
                break;
            case R.id.query_btn_submit:
                if (!("").equals(country)&&!("").equals(city)&&!("").equals(province)){
                    jsAddress = tvP.getText().toString() + tvCity.getText().toString() + tvCountry.getText().toString() + etAddr.getText().toString();
                }else {
                    jsAddress = etAddr.getText().toString();
                }
                Intent it = new Intent();
                it.putExtra("province", province);
                it.putExtra("city",city);
                it.putExtra("country", country);
                it.putExtra("jsAddress",jsAddress);
                setResult(RESULT_OK, it);
                finish();
        }

    }
    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //address 省
                case Constant.ADDRESS_P:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(QueryAddrActivity.this).getNoRes(result)) {
                        addressList.clear();
                        addressList = ResultLog.getInstance(QueryAddrActivity.this).AddressLog(result);
                        Log.d("TAG", " province ->" + addressList.toString());
                        if (addressList.size() > 0) {
                            gv.setVisibility(View.VISIBLE);
                            gv.setAdapter(new AddressAdapter());
                        } else {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", "");
                            map.put("bute", "无");
                            map.put("code", "");
                            map.put("name", "");
                            addressList.add(map);
                        }
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                province = addressList.get(position).get("id").toString();
                                tvP.setText(addressList.get(position).get("name").toString());
                                tvP.setTextColor(Color.parseColor("#333333"));
                                gv.setVisibility(View.GONE);
                                tvCity.setBackgroundResource(R.drawable.dia_text_bg_selected);
                                tvP.setBackgroundResource(R.drawable.dia_text_bg_unselected);
                                tvCountry.setBackgroundResource(R.drawable.dia_text_bg_unselected);
                                MyThread.getInstance(QueryAddrActivity.this).AddressThread(handler, "M", "", "R", province, Constant.ADDRESS_CITY);
                            }
                        });
                    }
                    break;
                //address 市
                case Constant.ADDRESS_CITY:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(QueryAddrActivity.this).getNoRes(result)) {
                        addressList.clear();
                        addressList = ResultLog.getInstance(QueryAddrActivity.this).AddressLog(result);
                        Log.d("TAG", "city ->" + addressList.toString());
                        if (addressList.size() > 0) {
                            gv.setVisibility(View.VISIBLE);
                            gv.setAdapter(new AddressAdapter());
                        } else {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", "");
                            map.put("bute", "无");
                            map.put("code", "");
                            map.put("name", "");
                            addressList.add(map);
                        }
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                city = addressList.get(position).get("id").toString();
                                tvCity.setText(addressList.get(position).get("name").toString());
                                tvCity.setTextColor(Color.parseColor("#333333"));
                                gv.setVisibility(View.GONE);
                                tvCountry.setBackgroundResource(R.drawable.dia_text_bg_selected);
                                tvP.setBackgroundResource(R.drawable.dia_text_bg_unselected);
                                tvCity.setBackgroundResource(R.drawable.dia_text_bg_unselected);
                                MyThread.getInstance(QueryAddrActivity.this).AddressThread(handler, "M", "", "C", city, Constant.ADDRESS_COUNTRY);

                            }
                        });
                    }
                    break;
                //address 县区
                case Constant.ADDRESS_COUNTRY:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(QueryAddrActivity.this).getNoRes(result)) {
                        addressList.clear();
                        addressList = ResultLog.getInstance(QueryAddrActivity.this).AddressLog(result);
                        Log.d("TAG", "country ->" + addressList.toString());
                        if (addressList.size() > 0) {
                            gv.setVisibility(View.VISIBLE);
                            gv.setAdapter(new AddressAdapter());
                        } else {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("id", "");
                            map.put("bute", "无");
                            map.put("code", "");
                            map.put("name", "");
                            addressList.add(map);
                        }
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                country = addressList.get(position).get("id").toString();
                                tvCountry.setText(addressList.get(position).get("name").toString());
                                tvCountry.setTextColor(Color.parseColor("#333333"));
                                gv.setVisibility(View.GONE);
                                tvCountry.setBackgroundResource(R.drawable.dia_text_bg_selected);
                                tvP.setBackgroundResource(R.drawable.dia_text_bg_selected);
                                tvCity.setBackgroundResource(R.drawable.dia_text_bg_selected);
                            }
                        });
                    }
                    break;
            }
        }
    }
    /**
     * adapter
     */
    class AddressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addressList.size();
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
                convertView = LayoutInflater.from(QueryAddrActivity.this).inflate(R.layout.item_address_gl,parent,false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_address_gl_tv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvName.setText(addressList.get(position).get("name").toString());
            return convertView;
        }
        class ViewHolder{
            TextView tvName;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
