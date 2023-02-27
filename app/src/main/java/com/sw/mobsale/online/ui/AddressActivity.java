package com.sw.mobsale.online.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.AddressAdapter;
import com.sw.mobsale.online.util.MyDialog;
import com.sw.mobsale.online.util.ScreenManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

/**
 * Loc NearBy
 */
public class AddressActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private ListView lvStore;
    //分类搜索
    private TextView tvFood,tvShop,tvStore,tvAll;
    //经纬度
    private double lat,lon;
    private MySearch mySearch = new MySearch();
    private PoiSearch mPoiSearch = null;
    //无网络
    private ImageView ivBug,ivRefresh;
    private RelativeLayout rlNet;
    //head
    private TextView tvTitle;
    private RelativeLayout rlBack;
    //搜索关键字
    private String keyWord = "餐厅";
    //弹出框
    private AlertDialog builder;
    //弹出框动画
    private GifImageView myGif;
    //数据 //附近店铺
    public ArrayList<Map<String, Object>> storeLists = new ArrayList<Map<String, Object>>();
    private AddressAdapter  addressAdapter;
    //lv footer
    private View footerView;
    private MyDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvFood = (TextView) findViewById(R.id.main_tv_food);
        tvShop = (TextView) findViewById(R.id.main_tv_shop);
        tvStore = (TextView) findViewById(R.id.main_tv_store);
        tvAll = (TextView) findViewById(R.id.main_tv_all);
        lvStore = (ListView) findViewById(R.id.address_lv);
        tvTitle = (TextView) findViewById(R.id.main_title);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlNet = (RelativeLayout) findViewById(R.id.main_rl_net_bug);
        ivBug = (ImageView) findViewById(R.id.main_net_set_iv);
        ivRefresh = (ImageView) findViewById(R.id.main_net_refrush_iv);
        tvTitle.setText("附近店铺");
        footerView = View.inflate(AddressActivity.this,R.layout.item_lv_footer,null);
        TextView tv = (TextView) footerView.findViewById(R.id.footer_view_tv);
        tv.setText("已到底部");
        lvStore.addFooterView(footerView,"",false);
        //搜索
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mySearch);
        //取定位经纬度
        SharedPreferences spf = getSharedPreferences("loc", Context.MODE_PRIVATE);
        String la = spf.getString("lat","1");
        String lo = spf.getString("lon","1");
        Log.d("TAG","lo->"+lo + "la->"+la);
        lat = Double.valueOf(la);
        lon = Double.valueOf(lo);
        //dialog view
//        View AddDataView  = LayoutInflater.from(AddressActivity.this).inflate(R.layout.main_loading_dialog, null);
//        //正在加载动画
//        myGif = (GifImageView) AddDataView.findViewById(R.id.main_pb);
//        try {
//            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
//            // 将gif图资源转化为GifDrawable
//            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.near_loading2);
//            // gif1加载一个动态图gif
//            myGif .setImageDrawable(gifDrawable);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // dialog builder
//        builder = new AlertDialog.Builder(AddressActivity.this).setIcon(R.drawable.icon).setTitle("提示").setView(AddDataView).create();
        confirmDialog = new MyDialog(AddressActivity.this, "提示","", "", "",false,true);
        //默认为菜市场
        setTextColor();
        tvAll.setTextColor(Color.parseColor("#e84232"));
        keyWord = "菜市场";
        Search(keyWord);
    }

    /**
     * 设置text初始值
     */
    private void setTextColor(){
        tvAll.setTextColor(Color.parseColor("#454545"));
        tvFood.setTextColor(Color.parseColor("#454545"));
        tvStore.setTextColor(Color.parseColor("#454545"));
        tvShop.setTextColor(Color.parseColor("#454545"));
    }
    @Override
    protected void onResume() {
        super.onResume();
        //listview条目监听
        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String store = storeLists.get(position).get("customerName").toString();
                String address = storeLists.get(position).get("address").toString();
                String phone = storeLists.get(position).get("phone").toString();
                Intent intent = new Intent(AddressActivity.this, RetailActivity.class);
                intent.putExtra("customerName",store);
                intent.putExtra("address",address);
                intent.putExtra("phone",phone);
                startActivity(intent);
            }
        });
        //head back
        rlBack.setOnClickListener(this);
        //刷新
        ivRefresh.setOnClickListener(this);
        //设置网络
        ivBug.setOnClickListener(this);
        //餐厅
        tvFood.setOnClickListener(this);
        //超市
        tvShop.setOnClickListener(this);
        //商铺
        tvStore.setOnClickListener(this);
        //菜市场
        tvAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //head back
            case R.id.head_rl_title:
                finish();
                break;
            //刷新
            case R.id.main_net_refrush_iv:
                //默认为菜市场
                setTextColor();
                tvAll.setTextColor(Color.parseColor("#e84232"));
                keyWord = "菜市场";
                Search(keyWord);
                break;
            //设置网络
            case R.id.main_net_set_iv:
                //跳转到设置页面
                startActivity(new Intent((Settings.ACTION_SETTINGS)));
                break;
            //餐厅
            case R.id.main_tv_food:
                setTextColor();
                tvFood.setTextColor(Color.parseColor("#e84232"));
                keyWord = "餐厅";
                Search(keyWord);
                break;
            //超市
            case R.id.main_tv_shop:
                setTextColor();
                tvShop.setTextColor(Color.parseColor("#e84232"));
                keyWord = "超市";
                Search(keyWord);
                break;
            //商铺
            case R.id.main_tv_store:
                setTextColor();
                tvStore.setTextColor(Color.parseColor("#e84232"));
                keyWord = "商铺";
                Search(keyWord);
                break;
            //菜市场
            case R.id.main_tv_all:
                setTextColor();
                tvAll.setTextColor(Color.parseColor("#e84232"));
                keyWord = "菜市场";
                Search(keyWord);
                break;
        }
    }

    /**
     * 按关键字搜索附近
     * @param keyWord 搜索关键字
     */
    private void Search(String keyWord){
//        builder.show();
        confirmDialog.show();
        Log.d("TAG",keyWord);
        LatLng center = new LatLng(lat,lon);
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword(
                keyWord).sortType(PoiSortType.distance_from_near_to_far).location(center)
                .radius(2000).pageNum(0);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /**
     * 搜索附近
     */
    public class MySearch implements OnGetPoiSearchResultListener {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(AddressActivity.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                storeLists.clear();
                return;
            }
            //附近信息
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                storeLists.clear();
                for (PoiInfo poiInfo : poiResult.getAllPoi()) {
                    String store = poiInfo.name;
                    String address = poiInfo.address;
                    String phoneNum = poiInfo.phoneNum;
                    double nearLat =  poiInfo.location.latitude;
                    double nearLon =  poiInfo.location.longitude;
                    if (store.contains("(")) {
                        int i = store.indexOf("(");
                        store = store.substring(0, i);
                    }
                    if (address.contains("(")) {
                        int j = address.indexOf("(");
                        address = address.substring(0, j);
                    }
                    LatLng latLng = new LatLng(nearLat,nearLon);
                    LatLng latLng1 = new LatLng(lat,lon);
                    double distance =  DistanceUtil.getDistance(latLng,latLng1);
                    String d = String.valueOf(distance);
                    if (d.contains(".")){
                        int a = d.indexOf(".");
                        d = d.substring(0,a);
                    }
                    phoneNum = phoneNum.replace("(","").replace(")","-");
                    if (phoneNum ==null||("").equals(phoneNum)){
                        phoneNum = "无";
                    }else{
                        if (phoneNum.contains(",")){
                            int a = phoneNum.indexOf(",");
                            phoneNum = phoneNum.substring(0,a);
                        }
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("customerName", store);
                    map.put("address", address);
                    map.put("distance",d);
                    map.put("unit","m");
                    map.put("phone",phoneNum);
                    storeLists.add(map);
                }
            }
            Log.d("TAG",storeLists.toString());
            addressAdapter = new AddressAdapter(AddressActivity.this,storeLists);
            lvStore.setAdapter(addressAdapter);
//            builder.dismiss();
            confirmDialog.dismiss();
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(AddressActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(AddressActivity.this, poiDetailResult.getName() + ": " + poiDetailResult.getAddress(), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    }

    /**
     * 返回键
     * @param keyCode keycode标识
     * @param event KeyEvent
     * @return 返回true false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(AddressActivity.this,MainActivity.class);
//            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
