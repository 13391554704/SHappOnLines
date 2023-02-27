package com.sw.mobsale.online.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
import com.sw.mobsale.online.ui.DetailSellActivity;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 未配送Fragment
 */
public class UnFinishFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;//刷新
    private ListView lvStore;
    private FgDetailAdapter adapter;
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();
    private RelativeLayout rlNoData;//无订单
    private MyHandler handler;
    //经纬度
    private double lat,lon;
    private String address = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unfinish, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化
     * @param view
     */
    private void initView(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.unfinish_refresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        lvStore = (ListView) view.findViewById(R.id.fg_unfinish_lv);
        rlNoData = (RelativeLayout) view.findViewById(R.id.detail_rl_detail);
        refreshLayout.setEnabled(false);
        SharedPreferences spf = getActivity().getSharedPreferences("loc", Context.MODE_PRIVATE);
        String la = spf.getString("lat", "1");
        String lo = spf.getString("lon", "1");
        Log.d("TAG", "lo->" + lo);
        Log.d("TAG", "la->" + la);
        lat = Double.valueOf(la);
        lon = Double.valueOf(lo);
        handler = new MyHandler();
        MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_U,"", "U", "","","");
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_U,"", "U", "","","");
            }
        });
        lvStore.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    refreshLayout.setEnabled(true);
                else
                    refreshLayout.setEnabled(false);
            }
        });
        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailSellActivity.class);
                intent.putExtra("orderNo",dataLists.get(position).get("number").toString());
                intent.putExtra("orderId",dataLists.get(position).get("id").toString());
                intent.putExtra("orderFrom",dataLists.get(position).get("fromCode").toString());
                intent.putExtra("orderType","O");
                startActivity(intent);
              //  getActivity().finish();
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
            switch (msg.what){
                case  Constant.ORDER_STATUS_U:
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    Log.d("TAG", "unfinish result->" + result);
//                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        dataLists.clear();
                        dataLists = ResultLog.getInstance(getActivity()).OrderNoLog(result);
                        Log.d("TAG", "unfinish ->" + dataLists.toString());
                        if (dataLists.size() > 0) {
                            adapter = new FgDetailAdapter(getActivity(), dataLists);
                            lvStore.setAdapter(adapter);
                            rlNoData.setVisibility(View.GONE);
                        } else {
                            rlNoData.setVisibility(View.VISIBLE);
                        }
                        refreshLayout.setRefreshing(false);
//                    }
//                    for (int i = 0;i < dataLists.size();i++){
//                        address = dataLists.get(i).get("address").toString();
//                        getD(address);
//                    }
//                    if (dataLists.size() > 0) {
//                        adapter = new FgDetailAdapter(getActivity(), dataLists);
//                        lvStore.setAdapter(adapter);
//                        rlNoData.setVisibility(View.GONE);
//                    }else{
//                        rlNoData.setVisibility(View.VISIBLE);
//                    }
//                    refreshLayout.setRefreshing(false);
                    break;
//                case 999:
//                    String distance =  msg.getData().getString("distance");
//                    String unit =  msg.getData().getString("unit");
//                    String addr =  msg.getData().getString("address");
//                    for (int i = 0;i < dataLists.size();i++){
//                        if ((dataLists.get(i).get("address").toString()).equals(addr)) {
//                            dataLists.get(i).put("distance", distance);
//                            dataLists.get(i).put("unit", unit);
//                        }
//                    }
//                    Log.d("TAG", "unfinish re->" + dataLists.toString());
//                    if (dataLists.size() > 0) {
//                        adapter = new FgDetailAdapter(getActivity(), dataLists);
//                        lvStore.setAdapter(adapter);
//                        rlNoData.setVisibility(View.GONE);
//                    }else{
//                        rlNoData.setVisibility(View.VISIBLE);
//                    }
//                    refreshLayout.setRefreshing(false);
            }
        }
    }


    /**
     * 返回输入地址的经纬度坐标
     * key lng(经度),lat(纬度)
     */
    public LatLng getGeocoderLatitude(String address){
        BufferedReader in = null;
        LatLng ll = null;
        String KEY_1 = "xwngXjOeiqjwwGZ62Rt4aKviAqoxmrGL";//7d9fbeb43e975cd1e9477a7e5d5e192a
        try {
            //将地址转换成utf-8的16进制
            address = URLEncoder.encode(address, "UTF-8");
            URL tirc = new URL("http://api.map.baidu.com/geocoder?address="+ address +"&output=json&key="+ KEY_1);
            in = new BufferedReader(new InputStreamReader(tirc.openStream(),"UTF-8"));
            String res;
            StringBuilder sb = new StringBuilder("");
            while((res = in.readLine())!=null){
                sb.append(res.trim());
            }
            String str = sb.toString();
            //StringUtils.isNotEmpty(str)
            if(!("").equals(str)){
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if(lngStart > 0 && lngEnd > 0 && latEnd > 0){
                    String lng = str.substring(lngStart+5, lngEnd);
                    String lat = str.substring(lngEnd+7, latEnd);
                    Log.d("TAG","LL->"+lat+"ll->"+lng);
                    ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    return ll;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ll;
    }

    /**
     * 距离
     * @param address address
     */
    public void getD(final String address) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(address, 5);
                    String unit = "m";
                    double distance = 0;
                    if (addresses.size() > 0) {
                        double Latitude = addresses.get(0).getLatitude();
                        double Longitude = addresses.get(0).getLongitude();
                        Log.d("TAG", "lat->" + Latitude);
                        Log.d("TAG", "lon->" + Longitude);
                        LatLng llng = new LatLng(lat, lon);
                        LatLng lng = new LatLng(Latitude, Longitude);
                        distance = DistanceUtil.getDistance(llng, lng);
                        String d = String.valueOf(distance);
                        if (d.contains(".")) {
                            int dis = d.indexOf(".");
                            d = d.substring(0, dis);
                            Log.d("TAG", "dis->" + d);
                        }
                        if (d.length() > 3) {
                            Double dis = Double.parseDouble(d);
                            distance = dis / 1000;
                            unit = "km";
                        } else {
                            distance = Double.parseDouble(d);
                            unit = "m";
                        }
                    }
                    Message message = new Message();
                    message.what = 999;
                    Bundle bundle = new Bundle();
                    bundle.putString("distance",String.valueOf(distance));
                    bundle.putString("unit",unit);
                    bundle.putString("address",address);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 更新数据
     */
    public void update(){
        MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_U,"", "U", "","","");
    }
}
