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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.CaptureActivity;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyDialog;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.OnSlideListener;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SwipeListView;
import com.sw.mobsale.online.util.SwipeView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

public class LoadingActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private SwipeListView lvList;
    private SwipeView mOldSwipeView;
    private RelativeLayout rlDetail,rlSale,rlSaoMa,rlAdd;//订单  铺货  扫码订单  铺货汇总
    private Button btnNext; //出发
    private RelativeLayout rlNo;//无数据
    private TextView tvNumber,tvBgNum,tvQty,tvBgQty;//装车订单数量  装车铺货商品数量
    private LinearLayout llTop;//装车单显示
    private ArrayList<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>(); //数据源
    private TextView tvTitle; //head文件 title
    private RelativeLayout rlBack; //head文件 back
    private Intent intent ;
    private SwipeAdapter swipeAdapter;//adapter
    private MyHandler handler;//handler
    private String result;//json数据
    private View viewHeader,viewBg;//listview head View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("生成装车单");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        btnNext = (Button) findViewById(R.id.loading_btn_next);
        lvList = (SwipeListView) findViewById(R.id.loading_lv);
        viewHeader = View.inflate(LoadingActivity.this,R.layout.loading_lv_header,null);
        rlDetail = (RelativeLayout) viewHeader.findViewById(R.id.loading_rl_detail);
        rlSaoMa = (RelativeLayout) viewHeader.findViewById(R.id.loading_rl_saoma);
        rlSale = (RelativeLayout) viewHeader.findViewById(R.id.loading_rl_sale);
        rlAdd = (RelativeLayout) viewHeader.findViewById(R.id.loading_rl_add);
        llTop = (LinearLayout) findViewById(R.id.loading_view2);
        rlNo = (RelativeLayout) findViewById(R.id.detail_rl_detail);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvQty = (TextView) findViewById(R.id.tv_qty);
        viewBg = View.inflate(LoadingActivity.this,R.layout.loading_lv_bg,null);
        tvBgNum = (TextView) viewBg.findViewById(R.id.tv_number);
        tvBgQty = (TextView) viewBg.findViewById(R.id.tv_qty);
        llTop.setEnabled(false);
        lvList.addHeaderView(viewHeader);
        lvList.addHeaderView(viewBg);
        handler = new MyHandler();
        //查询订单,零售单 status A
        MyThread.getInstance(LoadingActivity.this).OrderThread(handler, Constant.ORDER_STATUS_A,"","A","","","");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlDetail.setOnClickListener(this);
        rlSaoMa.setOnClickListener(this);
        rlSale.setOnClickListener(this);
        rlAdd.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        rlBack.setOnClickListener(this);

        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >=1){
                    llTop.setVisibility(View.VISIBLE);
                }else{
                    llTop.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //订单
            case R.id.loading_rl_detail:
                intent = new Intent(LoadingActivity.this, LoadDetailActivity.class);
                startActivityForResult(intent, Constant.LOAD_LOADORDER);
                break;
            //铺货
            case R.id.loading_rl_sale:
                intent = new Intent(LoadingActivity.this, LoadSaleActivity.class);
                startActivity(intent);
                finish();
                break;
            //扫码订单
            case R.id.loading_rl_saoma:
                //调取扫码界面。获取订单
                intent = new Intent(LoadingActivity.this, CaptureActivity.class);
                startActivity(intent);
                finish();
                break;
            //出发
            case R.id.loading_btn_next:
                final MyDialog confirmDialog = new MyDialog(LoadingActivity.this, "提示","出发后将无法再次装车，确定出发吗?", "是","否",false,false);
                confirmDialog.show();
                confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm(String result) {

                    }

                    @Override
                    public void doConfirm() {
                        MyThread.getInstance(LoadingActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
                    }
                    @Override
                    public void doCancel() {
                        confirmDialog.dismiss();
                    }
                });
//                AlertDialog.Builder dialog = new AlertDialog.Builder(LoadingActivity.this);
//                dialog.setTitle("提示");
//                dialog.setMessage("出发后将无法再次装车，确定出发吗?");
//                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //出发前把装车单上传  确认出发  status Y
//                      //  MyThread.getInstance(LoadingActivity.this).StartThread(handler,"Y");
//                        // 在线判断
//                        MyThread.getInstance(LoadingActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
//                    }
//                });
//                dialog.setNegativeButton("否", null);
//                dialog.create().show();
                break;
            //铺货汇总
            case R.id.loading_rl_add:
                intent = new Intent(LoadingActivity.this, CarLoadActivity.class);
                intent.putExtra("name", "saleAll");
                startActivity(intent);
                break;
            //返回
            case R.id.head_rl_title:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取订单
        if (requestCode ==Constant.LOAD_LOADORDER){
            if (resultCode ==RESULT_OK){
                //查询订单,零售单 status A
                MyThread.getInstance(LoadingActivity.this).OrderThread(handler, Constant.ORDER_STATUS_A,"","A","","","");
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
            switch (msg.what){
                //判断是否在线
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadingActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(LoadingActivity.this).getDialog(LoadingActivity.this);
                            } else {
                                MyThread.getInstance(LoadingActivity.this).StartThread(handler, "Y");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //出发状态  Y
                case Constant.ORDER_STATUS_Y:
                    result = msg.getData().getString("result");
                    Log.d("TAG","load start result->" +result);
                    if (!ResultLog.getInstance(LoadingActivity.this).getNoRes(result)) {
                        //, LoadingActivity.this
                        ResultLog.getInstance(LoadingActivity.this).FinishLog(result);
                    }
                    break;
                //单据信息
                case Constant.ORDER_STATUS_A:
                    result = msg.getData().getString("result");
                    Log.d("TAG","load order result->" +result);
                    if (!ResultLog.getInstance(LoadingActivity.this).getNoRes(result)) {
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(LoadingActivity.this).LoadingLog(result);
                        Log.d("TAG", dataLists.toString());
                        if (dataLists.size() > 0) {
                            tvNumber.setText(dataLists.get(0).get("orderQty").toString());
                            tvBgNum.setText(dataLists.get(0).get("orderQty").toString());
                            tvQty.setText(dataLists.get(0).get("detailQty").toString());
                            tvBgQty.setText(dataLists.get(0).get("detailQty").toString());
                            btnNext.setEnabled(true);
                            rlNo.setVisibility(View.GONE);
                            btnNext.setBackgroundColor(Color.parseColor("#e84232"));
                            btnNext.setTextColor(Color.parseColor("#ffffff"));
                        } else {
                            tvNumber.setText("0");
                            tvBgNum.setText("0");
                            tvQty.setText("0");
                            tvBgQty.setText("0");
                            btnNext.setEnabled(false);
                            btnNext.setBackgroundColor(Color.parseColor("#aaaaaa"));
                            btnNext.setTextColor(Color.parseColor("#ffffff"));
                            rlNo.setVisibility(View.VISIBLE);
                        }
                        swipeAdapter = new SwipeAdapter();
                        lvList.setAdapter(swipeAdapter);
                    }
                    break;
                //删除
                case Constant.ORDER_STATUS_K:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(LoadingActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("OK".equals(message)) {
                                MyThread.getInstance(LoadingActivity.this).OrderThread(handler, Constant.ORDER_STATUS_A, "", "A", "", "", "");
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
    class SwipeAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            SwipeView swipeView = (SwipeView) convertView;
            if (swipeView == null) {                                                //item_loading_order_lv
                View view =LayoutInflater.from(LoadingActivity.this).inflate(R.layout.item_order_number_lv, parent, false);
                swipeView = new SwipeView(LoadingActivity.this);
                swipeView.setContentItemView(view);
                holder = new ViewHolder(swipeView);
                swipeView.setOnSlideListener(new OnSlideListener() {

                    @Override
                    public void onSlide(View view, int status) {

                        if (mOldSwipeView != null && mOldSwipeView != view) {
                            mOldSwipeView.shrink();
                        }

                        if (status == SLIDE_STATUS_ON) {
                            mOldSwipeView = (SwipeView) view;
                        }
                    }
                });
                swipeView.setTag(holder);
            } else {
                holder = (ViewHolder) swipeView.getTag();
            }
            if (SwipeListView.mSwipeView != null) {
                SwipeListView.mSwipeView.shrink();
            }

            holder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
            holder.tvName.setText((String) dataLists.get(position).get("name"));
            holder.tvFrom.setText((String) dataLists.get(position).get("fromName"));
            holder.tvAddress.setText((String) dataLists.get(position).get("address"));
//            holder.tvDistance.setText((String) dataLists.get(position).get("distance"));
//            holder.tvKm.setText((String) dataLists.get(position).get("unit"));
            holder.tvDate.setText((String) dataLists.get(position).get("date"));
            holder.tvTime.setText((String) dataLists.get(position).get("time"));
            holder.tvNote.setText((String) dataLists.get(position).get("note"));
            holder.tvNumber.setText((String) dataLists.get(position).get("number"));
            holder.tvMonty.setText((String) dataLists.get(position).get("amt"));
//            holder.ivChoose.setImageResource(R.drawable.manager_choose);
//            holder.ivChoose.setVisibility(View.GONE);
//            holder.tvKm.setVisibility(View.GONE);
//            holder.tvDistance.setVisibility(View.GONE);
            holder.leftView.setText("删除");
            holder.rightView.setText("明细");
            holder.leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderNo = dataLists.get(position).get("number").toString();
                    String orderId = dataLists.get(position).get("id").toString();;
                    String from = dataLists.get(position).get("fromCode").toString();
                    MyThread.getInstance(LoadingActivity.this).OrderThread(handler, Constant.ORDER_STATUS_K, "", "K", orderNo,orderId,from);
                }
            });
            holder.rightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = dataLists.get(position).get("name").toString();
                    Intent intent = new Intent(LoadingActivity.this, CarLoadActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("flag", "loading");
                    intent.putExtra("address", holder.tvAddress.getText().toString());
                    intent.putExtra("orderNo", holder.tvNumber.getText().toString());
                    intent.putExtra("orderId",dataLists.get(position).get("id").toString());
                    intent.putExtra("orderFrom",dataLists.get(position).get("fromCode").toString());
                    intent.putExtra("amt", holder.tvMonty.getText().toString());
                    startActivity(intent);
                }
            });
            return swipeView;
        }
        class ViewHolder {
            ImageView ivBg;
            TextView tvName;
            TextView tvFrom;
            TextView tvAddress;
            TextView tvMonty;
            TextView tvDate;
            TextView tvTime;
            TextView tvNote;
//            ImageView ivChoose;
            TextView tvNumber;
//            TextView tvDistance;
//            TextView tvKm;
            TextView leftView;
            TextView rightView;

            ViewHolder(View convertView) {
                ivBg = (ImageView) convertView.findViewById(R.id.item_order_bg);
                tvName = (TextView) convertView.findViewById(R.id.item_order_name_tv);
                tvFrom = (TextView) convertView.findViewById(R.id.item_order_from_tv);
                tvAddress = (TextView) convertView.findViewById(R.id.item_order_address_tv);
                //tvDistance = (TextView) convertView.findViewById(R.id.item_order_distance_tv);
                //tvKm = (TextView) convertView.findViewById(R.id.item_order_km_tv);
                tvDate = (TextView) convertView.findViewById(R.id.item_order_date_tv);
                tvTime = (TextView) convertView.findViewById(R.id.item_order_time_tv);
                tvNote = (TextView) convertView.findViewById(R.id.item_order_note_tv);
                //ivChoose = (ImageView) convertView.findViewById(R.id.item_order_number_iv_choose);
                tvNumber = (TextView) convertView.findViewById(R.id.item_order_number_tv);
                tvMonty = (TextView) convertView.findViewById(R.id.item_order_money_tv);
                leftView = (TextView) convertView.findViewById(R.id.tv_left);
                rightView = (TextView) convertView.findViewById(R.id.tv_right);
            }
        }
    }

    /**
     * 返回键
     * @param keyCode keyCode
     * @param event KeyEvent
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            intent = new Intent(LoadingActivity.this, MainActivity.class);
//            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
