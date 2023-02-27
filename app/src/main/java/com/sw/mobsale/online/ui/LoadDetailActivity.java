
package com.sw.mobsale.online.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.OnSlideListener;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.SaleSwipeListView;
import com.sw.mobsale.online.util.SaleSwipeView;
import com.sw.mobsale.online.util.ScreenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单
 */
public class LoadDetailActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private SaleSwipeView mOldSwipeView;
    private SaleSwipeListView lvOrder;
    private OrderAdapter adapter;
    private RelativeLayout rlChoose;//全选布局
    private CheckBox cb;//全选
    private TextView tvAllChoose,tvRoute,tvShow;//全选 路线信息  路线
    private Button btnConfirm; //确定
    //head文件
    private TextView tvTitle,tvChoose,tvNoData;//title choose //提示文字
    private RelativeLayout rlBack,rlNo;// back  //提示无订单
    //订单数据
    private ArrayList<Map<String, Object>> orderNumber = new ArrayList<Map<String, Object>>();
    //传递数据
    private ArrayList<String> chooseNumber = new ArrayList<String>();
    //item选择数据
    private Map<Integer,Integer>flag = new HashMap<Integer, Integer>();
    //server返回结果
    private String result;
    //handler
    private MyHandler handler;
    //筛选返回的路线
    private String route;
    String orderNo = "";
    String orderId ="";
    private boolean first = true;
    private View footerView;   //listview footer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_detail);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        lvOrder = (SaleSwipeListView) findViewById(R.id.load_lv);
        rlChoose = (RelativeLayout) findViewById(R.id.load_rl_choose);
        rlNo = (RelativeLayout) findViewById(R.id.retail_rl_detail);
        tvNoData = (TextView) findViewById(R.id.detail_rl_content_tv);
        cb = (CheckBox) findViewById(R.id.load_check_cb);
        tvAllChoose = (TextView) findViewById(R.id.load_check_tv);
        tvRoute = (TextView) findViewById(R.id.load_detail_route_tv);
        tvShow = (TextView) findViewById(R.id.load_detail_route_show);
        btnConfirm = (Button) findViewById(R.id.load_btn_confirm);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvChoose = (TextView) findViewById(R.id.head_title_gone);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        tvChoose.setVisibility(View.VISIBLE);
        tvChoose.setText("路线");
        tvTitle.setText("获取订单");
        rlNo.setVisibility(View.VISIBLE);
        tvNoData.setText("通过路线获取订单");
        tvShow.setVisibility(View.GONE);
        handler = new MyHandler();
        adapter = new OrderAdapter();
        //footerView
        footerView = View.inflate(LoadDetailActivity.this, R.layout.item_lv_footer, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlBack.setOnClickListener(this);
        rlChoose.setOnClickListener(this);
        tvChoose.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.isItemSelected(position)){
                    adapter.removeSelected(position);
                    //删除订单号
                    chooseNumber.remove(orderNumber.get(position).get("number").toString());
                }else{
                    adapter.addSelected(position);
                    //记录下订单号
                    chooseNumber.add(orderNumber.get(position).get("number").toString());
                }
                adapter.notifyDataSetChanged();
                Log.d("TAG",chooseNumber.toString());
                //判断是否全选
                if (chooseNumber.size() == orderNumber.size()){
                    cb.setBackgroundResource(R.drawable.checked);
                    tvAllChoose.setText("取消全选");
                }else{
                    cb.setBackgroundResource(R.drawable.unchecked);
                    tvAllChoose.setText("全选");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //全选
            case R.id.load_rl_choose:
                String check = tvAllChoose.getText().toString();
                if ("全选".equals(check)) {
                    chooseNumber.clear();
                    adapter.removeSelected();
                    for (int i = 0; i < orderNumber.size(); i++) {
                        adapter.addSelected(i);
                        adapter.notifyDataSetChanged();
                        String chooseNum = orderNumber.get(i).get("number").toString();
                        chooseNumber.add(chooseNum);
                    }
                    cb.setBackgroundResource(R.drawable.checked);
                    tvAllChoose.setText("取消全选");
                }else if ("取消全选".equals(check)) {
                    adapter.removeSelected();
                    adapter.notifyDataSetChanged();
                    cb.setBackgroundResource(R.drawable.unchecked);
                    tvAllChoose.setText("全选");
                    chooseNumber.clear();
                }
                break;
            //返回
            case R.id.head_rl_title:
                finish();
                break;
            //筛选
            case R.id.head_title_gone:
                Intent intent = new Intent(LoadDetailActivity.this,RightActivity.class);
                intent.putExtra("choose","route");
                startActivityForResult(intent, Constant.ORDER_CHOOSE);
                break;
            //确定
            case R.id.load_btn_confirm:
                //orderId
                if (chooseNumber.size() > 0) {
                    //上传单号。。下载订单详情
                    orderNo = "";
                    for (int i = 0; i < chooseNumber.size(); i++) {
                        orderNo = orderNo + "," + chooseNumber.get(i);
                    }
                    orderNo = orderNo.substring(1);
//                    btnConfirm.setBackgroundColor(Color.parseColor("#f0f0f0"));
//                    btnConfirm.setEnabled(false);
                    MyThread.getInstance(LoadDetailActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    MyThread.getInstance(LoadDetailActivity.this).OrderThread(handler, Constant.ORDER_STATUS_O, route, "O", orderNo);
                }else{
                    Toast.makeText(LoadDetailActivity.this,"请选择订单",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ORDER_CHOOSE){
            if (resultCode ==RESULT_OK){
                orderNumber.clear();
                route = data.getStringExtra("route");
                String routeName = data.getStringExtra("routeName");
                if (routeName.length() > 0){
                    tvShow.setVisibility(View.VISIBLE);
                    tvRoute.setText( routeName);
                    MyThread.getInstance(LoadDetailActivity.this).OrderThread(handler,route);
                }
            }
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
                //订单信息
                case Constant.LOAD_ORDER:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadDetailActivity.this).getNoRes(result)) {
                        Log.d("TAG", "order result->" + result);
                        orderNumber.clear();
                        chooseNumber.clear();
                        flag.clear();
                        cb.setBackgroundResource(R.drawable.unchecked);
                        tvAllChoose.setText("全选");
                        orderNumber = ResultLog.getInstance(LoadDetailActivity.this).OrderNoLog(result);
                        Log.d("TAG", orderNumber.toString());
                        if (orderNumber.size() > 0) {
                            if (first) {
                                first = false;
                                if (orderNumber.size() > 6) {
                                    lvOrder.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                lvOrder.setAdapter(adapter);
                            } else {
                                lvOrder.removeFooterView(footerView);
                                if (orderNumber.size() > 6) {
                                    lvOrder.addFooterView(footerView, null, false);
                                }
                                rlNo.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                            rlNo.setVisibility(View.GONE);
                        } else {
                            lvOrder.removeFooterView(footerView);
                            rlNo.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                //判断是否在线
                case Constant.FORCE_DOWN:
                     result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadDetailActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(LoadDetailActivity.this).getDialog(LoadDetailActivity.this);
                            } else {
                                MyThread.getInstance(LoadDetailActivity.this).OrderThread(handler, Constant.ORDER_STATUS_O, route, "O", orderNo, orderId, "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //确定
                case Constant.ORDER_STATUS_O:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadDetailActivity.this).getNoRes(result)) {
                        Log.d("TAG", "confirm result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String state = object.getString("errorMessage");
                            if ("OK".equals(state)) {
                                Intent intent = getIntent();
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                chooseNumber.clear();
                                flag.clear();
                                orderNo = "";
                                orderId = "";
                                Toast.makeText(LoadDetailActivity.this, state, Toast.LENGTH_SHORT).show();
                                MyThread.getInstance(LoadDetailActivity.this).OrderThread(handler, route);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * adapter
     */
    class OrderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return orderNumber.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 添加选中项
         * @param position position
         */
        public void addSelected(int position){
            flag.put(position,position);
        }

        /**
         * 删除选中项
         * @param position position
         */
        public void removeSelected(int position){
            if (flag.containsKey(position)){
                flag.remove(position);
            }
        }

        /**
         * 删除所有
         */
        public void removeSelected(){
            flag.clear();
        }

        /**
         * 是有选中
         * @param position  position
         * @return true?false
         */
        public boolean isItemSelected(int position){
           // return flag.containsKey(position) ? true : false;
            return  flag.containsKey(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder viewHolder;
            SaleSwipeView swipeView = (SaleSwipeView) convertView;
            if (swipeView == null) {
                View view = LayoutInflater.from(LoadDetailActivity.this).inflate(R.layout.item_loading_order_lv,parent,false);
                swipeView = new SaleSwipeView(LoadDetailActivity.this);
                swipeView.setContentItemView(view);
                viewHolder = new Holder(swipeView);
                swipeView.setOnSlideListener(new OnSlideListener() {

                    @Override
                    public void onSlide(View view, int status) {

                        if (mOldSwipeView != null && mOldSwipeView != view) {
                            mOldSwipeView.shrink();
                        }

                        if (status == SLIDE_STATUS_ON) {
                            mOldSwipeView = (SaleSwipeView) view;
                        }
                    }
                });
                swipeView.setTag(viewHolder);
            } else {
                viewHolder = (Holder) swipeView.getTag();
            }
            if (SaleSwipeListView.mSwipeView != null) {
                SaleSwipeListView.mSwipeView.shrink();
            }
            viewHolder.ivBg.setImageResource((Integer) orderNumber.get(position).get("bg"));
            viewHolder.tvName.setText((String)orderNumber.get(position).get("name"));
            viewHolder.tvFrom.setText((String)orderNumber.get(position).get("fromName"));
            viewHolder.tvAddress.setText((String)orderNumber.get(position).get("address"));
            viewHolder.tvDate.setText((String)orderNumber.get(position).get("date"));
            viewHolder.tvTime.setText((String)orderNumber.get(position).get("time"));
            viewHolder.tvNote.setText((String)orderNumber.get(position).get("note"));
            viewHolder.tvNumber.setText((String)orderNumber.get(position).get("number"));
            viewHolder.tvMoney.setText((String)orderNumber.get(position).get("amt"));
            viewHolder.ivChoose.setVisibility(View.GONE);
            viewHolder.tvKm.setVisibility(View.GONE);
            viewHolder.tvDistance.setVisibility(View.GONE);
            viewHolder.leftView.setText("明细");
            viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = orderNumber.get(position).get("name").toString();
                    Intent intent = new Intent(LoadDetailActivity.this, CarLoadActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("intent","loadDetail");
                    intent.putExtra("address",orderNumber.get(position).get("address").toString());
                    intent.putExtra("orderNo",orderNumber.get(position).get("number").toString());
                    intent.putExtra("orderId",orderNumber.get(position).get("id").toString());
                    intent.putExtra("orderFrom",orderNumber.get(position).get("fromCode").toString());
                    intent.putExtra("amt",orderNumber.get(position).get("amt").toString());
                    startActivity(intent);
                }
            });
            //判读选中集合饱含条目就把选中的图片显示出来
            if (flag.containsKey(position)){
                viewHolder.ivChoose.setVisibility(View.VISIBLE);
                viewHolder.ivChoose.setImageResource(R.drawable.checked);
            }
            return swipeView;
        }
        class Holder {
            ImageView ivBg;
            TextView tvName;
            TextView tvFrom;
            TextView tvAddress;
            TextView tvDate;
            TextView tvTime;
            TextView tvNote;
            TextView tvMoney;
            ImageView ivChoose;
            TextView tvNumber;
            TextView tvDistance;
            TextView tvKm;
            TextView leftView;
            LinearLayout llAll;
            public Holder (View convertView){
                ivBg = (ImageView) convertView.findViewById(R.id.item_order_bg);
                tvName = (TextView) convertView.findViewById(R.id.item_order_name_tv);
                tvFrom = (TextView) convertView.findViewById(R.id.item_order_from_tv);
                tvAddress = (TextView) convertView.findViewById(R.id.item_order_address_tv);
                tvDate = (TextView) convertView.findViewById(R.id.item_order_date_tv);
                tvTime = (TextView) convertView.findViewById(R.id.item_order_time_tv);
                tvNote = (TextView) convertView.findViewById(R.id.item_order_note_tv);
                ivChoose = (ImageView) convertView.findViewById(R.id.item_order_number_iv_choose);
                tvNumber = (TextView) convertView.findViewById(R.id.item_order_number_tv);
                tvMoney = (TextView) convertView.findViewById(R.id.item_order_money_tv);
                leftView = (TextView) convertView.findViewById(R.id.tv_left);
                llAll = (LinearLayout) convertView.findViewById(R.id.item_order_ll);
                tvDistance = (TextView) convertView.findViewById(R.id.item_order_distance_tv);
                tvKm = (TextView) convertView.findViewById(R.id.item_order_km_tv);
            }
        }
    }
}
