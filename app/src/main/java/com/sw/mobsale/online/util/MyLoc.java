package com.sw.mobsale.online.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Sxs on 2017/7/6 0006
 */
public class MyLoc {
    Context context;
    public BDLocationListener myListener = new MyLocationListener();
    private boolean isFirstLocation = true;
    public LocationClient mLocClient = null;
    //经纬度
    private double lat,lon;
    public String address;
    public MyLoc(Context context){
        this.context = context;
    }
    /**
     * 定位信息
     */
    public void getPoi() {
        // 定位初始化
        // 开启定位图层
        mLocClient = new LocationClient(context.getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null){
                Toast.makeText(context,"网络信号差",Toast.LENGTH_LONG).show();
                return;
            }

            if (isFirstLocation) {
                isFirstLocation = false;
                lat = location.getLatitude();//纬度
                lon = location.getLongitude();//经度
                address = location.getAddrStr();//获取所在地址
                //保存定位信息
                SharedPreferences spf = context.getSharedPreferences("loc",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("lat",String.valueOf(lat));
                editor.putString("lon",String.valueOf(lon));
                editor.putString("address",address);
                Log.d("TAG","my lat->"+lat);
                Log.d("TAG","my lon->"+lon);
                editor.commit();
            }
        }
    }
}
