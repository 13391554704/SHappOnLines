package com.sw.mobsale.online.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.SQLiteManager;
import com.sw.mobsale.online.util.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UnSendActivity extends BaseActivity {
    ListView lvUnSend;
    Button btnSend;
    SQLiteManager manager;
    private ArrayList<Map<String, Object>> storeList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_send);
        lvUnSend = (ListView) findViewById(R.id.un_send_lv);
        btnSend = (Button) findViewById(R.id.un_send_btn);
        manager = SQLiteManager.getInstance(UnSendActivity.this);
        putData();
        lvUnSend.setAdapter(new FgDetailAdapter(UnSendActivity.this,storeList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接后台，改变状态
                new SendThread().start();
            }
        });
    }

    class SendThread extends Thread{
        @Override
        public void run() {
            super.run();
            //// TODO: 2017/5/23 0023  
        }
    }
    /**
     * 未上传单据
     */
    private void putData(){
        try {
            String sql = "select distinct customerName,customerAddress,transDate,transTime,transNo from app_pos_sale_data where isSent=?";
            Table table = manager.queryData2Table(sql,new String[]{"N"});
            if (table.getRowCount() > 0) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String customerName = table.getCellValue(i, "customerName");
                    map.put("customerName", customerName);
                    map.put("address", table.getCellValue(i, "customerAddress"));
                    map.put("orderNo", table.getCellValue(i, "transNo"));
                    map.put("date", table.getCellValue(i, "transDate"));
                    map.put("time",table.getCellValue(i, "transTime"));
                    map.put("bg", Constant.image[i % 5]);
                    map.put("distance", "500");
                    map.put("payType", table.getCellValue(i, "payType"));
                    map.put("image", R.drawable.main_item_unfinish);
                    storeList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
