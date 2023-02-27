package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
import com.sw.mobsale.online.adapter.LoadingAdapter;
import com.sw.mobsale.online.adapter.StoreAdapter;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.OnSlideListener;
import com.sw.mobsale.online.util.ReSwipeListView;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SwipeListView;
import com.sw.mobsale.online.util.SwipeView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 库存
 */
public class ResProductActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private TextView tvTitle,tvStoreNumber;//head  title    未配送数
    private RelativeLayout rlBack;//head back
    private LinearLayout llStore,llPro,llResPro,llSale;//未配送门店 未配送商品 剩余商品 未结算零售单
    //listview
    private ListView lvPro, lvStoreName,lvResPro;
    private ReSwipeListView lvSale;//lv
    private SwipeView mOldSwipeView;
    private ImageView ivStore,ivPro,ivRes,ivSale;//未配送门店 未配送商品 剩余商品 未结算零售单
    private ArrayList<Map<String, Object>> storeList = new ArrayList<Map<String, Object>>(); //未配送门店数据源
    private List<Map<String, Object>> proLists = new ArrayList<Map<String, Object>>();   //未配送商品数据源
    private List<Map<String, Object>> resProLists = new ArrayList<Map<String, Object>>();  //剩余商品
    private  ArrayList<Map<String, Object>> saleList = new ArrayList<Map<String, Object>>(); //未结算零售单
    private  MyHandler handler;//handler
    private String result;//json
    private boolean isStore = true;//标识
    private boolean isPro = true;//标识
    private boolean isRes = true;//标识
    private boolean isSale = true;//标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_product);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("库存管理");
        tvStoreNumber = (TextView) findViewById(R.id.res_unfinish_store_number);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        llStore = (LinearLayout) findViewById(R.id.res_ll_unfinish);
        llPro = (LinearLayout) findViewById(R.id.res_ll_unfinish_pro);
        llResPro = (LinearLayout) findViewById(R.id.res_ll_res_pro);
        llSale = (LinearLayout) findViewById(R.id.res_ll_sale_un_finish);
        ivStore = (ImageView) findViewById(R.id.res_iv);
        ivPro = (ImageView) findViewById(R.id.res_iv_un_pro);
        ivRes = (ImageView) findViewById(R.id.res_iv_pro);
        ivSale = (ImageView) findViewById(R.id.res_iv_sale);
        lvStoreName = (ListView) findViewById(R.id.res_pro_unfinish_lv);
        lvPro = (ListView) findViewById(R.id.res_pro_un_lv);
        lvResPro = (ListView) findViewById(R.id.res_pro_lv);
        lvSale = (ReSwipeListView) findViewById(R.id.res_pro_sale_lv);
        handler = new MyHandler();
        MyThread.getInstance(ResProductActivity.this).OrderThread(handler,Constant.ORDER_STATUS_B,"", "B", "","","");

    }



    @Override
    protected void onResume() {
        super.onResume();
        rlBack.setOnClickListener(this);
        llStore.setOnClickListener(this);
        llPro.setOnClickListener(this);
        llResPro.setOnClickListener(this);
        llSale.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.head_rl_title:
                finish();
                break;
            //未完成配送商户
            case R.id.res_ll_unfinish:
                if (isStore) {
                    isStore = false;
                    MyThread.getInstance(ResProductActivity.this).OrderThread(handler,Constant.ORDER_STATUS_U,"", "U", "","","");
                }else{
                    isStore = true;
                    ivStore.setImageResource(R.drawable.manager_xia);
                    lvStoreName.setVisibility(View.GONE);
                }
                break;
            //未完成配送商品
            case R.id.res_ll_unfinish_pro:
                if (isPro) {
                    isPro = false;
                    MyThread.getInstance(ResProductActivity.this).OrderThread(handler,Constant.ORDER_STATUS_E,"", "E", "","","");

                }else{
                    isPro = true;
                    ivPro.setImageResource(R.drawable.manager_xia);
                    lvPro.setVisibility(View.GONE);
                }
                break;
            //零售商品
            case R.id.res_ll_res_pro:
                if (isRes) {
                    isRes = false;
                    MyThread.getInstance(ResProductActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_T,"","T","","");
                }else {
                    isRes = true;
                    ivRes.setImageResource(R.drawable.manager_xia);
                    lvResPro.setVisibility(View.GONE);
                }
                break;
            //挂单
            case R.id.res_ll_sale_un_finish:
                if (isSale) {
                    isSale = false;
                    MyThread.getInstance(ResProductActivity.this).RetailThread(handler,"1", Constant.SALE_RETAIL_G,"","G","","");
                }else {
                    isSale = true;
                    ivSale.setImageResource(R.drawable.manager_xia);
                    lvSale.setVisibility(View.GONE);
                }
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
                //未完成配送商户数量
                case Constant.ORDER_STATUS_B:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " res store result->" + result);
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("rows");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jo = array.getJSONObject(i);
                                String storeNum = jo.getString("countnum");
                                tvStoreNumber.setText(storeNum);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //未完成配送商户
                case Constant.ORDER_STATUS_U:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " res store result->" + result);
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        storeList.clear();
                        storeList = ResultLog.getInstance(ResProductActivity.this).OrderNoLog(result);
                        Log.d("TAG", " res store->" + storeList.toString());
                        if (storeList.size() > 0) {
                            ivStore.setImageResource(R.drawable.manager_shang);
                            lvStoreName.setVisibility(View.VISIBLE);
                            //商铺
                            lvStoreName.setAdapter(new FgDetailAdapter(ResProductActivity.this, storeList));
                        } else {
                            isStore = true;
                            ivStore.setImageResource(R.drawable.manager_xia);
                            lvStoreName.setVisibility(View.GONE);
                            Toast.makeText(ResProductActivity.this, "无订单", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                //未完成配送商品
                case  Constant.ORDER_STATUS_E:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        Log.d("TAG", " res store result->" + result);
                        proLists.clear();
                        proLists = ResultLog.getInstance(ResProductActivity.this).OrderDetailLog(result);
                        Log.d("TAG", " res order->" + proLists.toString());
                        if (proLists.size() > 0) {
                            ivPro.setImageResource(R.drawable.manager_shang);
                            lvPro.setVisibility(View.VISIBLE);
                            //未配送商品
                            LoadingAdapter loadingAdapter = new LoadingAdapter(ResProductActivity.this, proLists);
                            lvPro.setAdapter(loadingAdapter);
                        } else {
                            isPro = true;
                            ivPro.setImageResource(R.drawable.manager_xia);
                            lvPro.setVisibility(View.GONE);
                            Toast.makeText(ResProductActivity.this, "无订单商品", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                //零售商品
                case Constant.SALE_RETAIL_T:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " sale all result->" + result);
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        resProLists.clear();
                        resProLists = ResultLog.getInstance(ResProductActivity.this).OrderDetailLog(result);
                        Log.d("TAG", "sale all ->" + resProLists.toString());
                        if (resProLists.size() > 0) {
                            ivRes.setImageResource(R.drawable.manager_shang);
                            lvResPro.setVisibility(View.VISIBLE);
                            //零售商品
                            StoreAdapter storeAdapter = new StoreAdapter(ResProductActivity.this, resProLists);
                            lvResPro.setAdapter(storeAdapter);
                        } else {
                            isRes = true;
                            ivRes.setImageResource(R.drawable.manager_xia);
                            lvResPro.setVisibility(View.GONE);
                            Toast.makeText(ResProductActivity.this, "无零售商品", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                //挂单
                case Constant.SALE_RETAIL_G:
                    result = msg.getData().getString("result");
                    Log.d("TAG", " sale unfinish result->" + result);
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        saleList.clear();
                        saleList = ResultLog.getInstance(ResProductActivity.this).OrderNoLog(result);
                        Log.d("TAG", "sale unfinish ->" + saleList.toString());
                        if (saleList.size() > 0) {
                            ivSale.setImageResource(R.drawable.manager_shang);
                            lvSale.setVisibility(View.VISIBLE);
                            //销售
                            lvSale.setAdapter(new SellAdapter());
                            lvSale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(ResProductActivity.this, PayModelActivity.class);
                                    intent.putExtra("orderNo", saleList.get(position).get("number").toString());
                                    intent.putExtra("orderType", "R");
                                    intent.putExtra("unSale", "unSale");
                                    intent.putExtra("totalPrice", saleList.get(position).get("amt").toString());
                                    intent.putExtra("orderFrom", saleList.get(position).get("fromCode").toString());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            isSale = true;
                            ivSale.setImageResource(R.drawable.manager_xia);
                            lvSale.setVisibility(View.GONE);
                            Toast.makeText(ResProductActivity.this, "无零售挂单", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                //删除挂单item
                case Constant.SALE_RETAIL_B:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(ResProductActivity.this).getNoRes(result)) {
                        Log.d("TAG", "delete  result->" + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (("ok").equals(message)) {
                                MyThread.getInstance(ResProductActivity.this).RetailThread(handler, "", Constant.SALE_RETAIL_G, "", "G", "","");
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
     * 页面数据listview适配器
     */
    class SellAdapter extends BaseAdapter {

        //构造方法
        public SellAdapter() {
        }

        @Override
        public int getCount() {
            return saleList.size();
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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder viewHolder;
            SwipeView swipeView = (SwipeView) convertView;
            if (swipeView == null) {
                View view =LayoutInflater.from(ResProductActivity.this).inflate(R.layout.item_loading_order_lv, parent,false);
                swipeView = new SwipeView(ResProductActivity.this);
                swipeView.setContentItemView(view);
                viewHolder = new ViewHolder(swipeView);
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
                swipeView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) swipeView.getTag();
            }
            if (SwipeListView.mSwipeView != null) {
                SwipeListView.mSwipeView.shrink();
            }
                viewHolder.ivBg.setImageResource((Integer) saleList.get(position).get("bg"));
                viewHolder.tvName.setText((String) saleList.get(position).get("name"));
                viewHolder.tvFrom.setText((String) saleList.get(position).get("fromName"));
                viewHolder.tvAddress.setText((String) saleList.get(position).get("address"));
                viewHolder.tvDate.setText((String) saleList.get(position).get("date"));
                viewHolder.tvTime.setText((String) saleList.get(position).get("time"));
                viewHolder.tvNote.setText((String) saleList.get(position).get("note"));
                viewHolder.tvNumber.setText((String)saleList.get(position).get("number"));
                viewHolder.tvMonty.setText((String)saleList.get(position).get("amt"));
                viewHolder.ivChoose.setImageResource(R.drawable.manager_choose);
                viewHolder.ivChoose.setVisibility(View.GONE);
                viewHolder.tvDistance.setVisibility(View.GONE);
                viewHolder.tvKm.setVisibility(View.GONE);
                viewHolder.leftView.setText("删除");
                viewHolder.rightView.setText("明细");
                viewHolder.leftView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = viewHolder.tvNumber.getText().toString();
                        Log.d("TAG","shop->" + number);
                        String id = saleList.get(position).get("id").toString();
                        MyThread.getInstance(ResProductActivity.this).RetailThread(handler,"", Constant.SALE_RETAIL_B,"","B",number,id);

                    }
                });
                viewHolder.rightView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = viewHolder.tvNumber.getText().toString();
                        Intent intent = new Intent(ResProductActivity.this,CarLoadActivity.class);
                        intent.putExtra("name","unfinish");
                        intent.putExtra("number",number);
                        intent.putExtra("orderFrom",saleList.get(position).get("fromCode").toString());
                        startActivity(intent);
                    }
                });

            return swipeView;
        }
        class ViewHolder {
            TextView leftView;
            TextView rightView;
            ImageView ivBg;
            TextView tvName;
            TextView tvFrom;
            TextView tvKm;
            TextView tvDistance;
            TextView tvAddress;
            TextView tvMonty;
            TextView tvDate;
            TextView tvTime;
            TextView tvNote;
            ImageView ivChoose;
            TextView tvNumber;
            ViewHolder(View convertView){
                leftView = (TextView) convertView.findViewById(R.id.tv_left);
                rightView = (TextView) convertView.findViewById(R.id.tv_right);
                ivBg = (ImageView) convertView.findViewById(R.id.item_order_bg);
                tvName = (TextView) convertView.findViewById(R.id.item_order_name_tv);
                tvFrom = (TextView) convertView.findViewById(R.id.item_order_from_tv);
                tvAddress = (TextView) convertView.findViewById(R.id.item_order_address_tv);
                tvDistance = (TextView) convertView.findViewById(R.id.item_order_distance_tv);
                tvKm =  (TextView) convertView.findViewById(R.id.item_order_km_tv);
                tvDate = (TextView) convertView.findViewById(R.id.item_order_date_tv);
                tvTime = (TextView) convertView.findViewById(R.id.item_order_time_tv);
                tvNote = (TextView) convertView.findViewById(R.id.item_order_note_tv);
                ivChoose = (ImageView) convertView.findViewById(R.id.item_order_number_iv_choose);
                tvNumber = (TextView) convertView.findViewById(R.id.item_order_number_tv);
                tvMonty = (TextView) convertView.findViewById(R.id.item_order_money_tv);
            }
        }
    }
    /**
     * 返回键
     * @param keyCode keyCode
     * @param event event
     * @return boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
