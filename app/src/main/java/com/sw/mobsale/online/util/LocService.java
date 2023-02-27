package com.sw.mobsale.online.util;


import android.content.Context;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


public class LocService{

    public LocationClient mLocClient = null;
    private static LocService locService;
    public static LocService getInstance(){
        if (locService == null){
            synchronized (LocService.class){
                locService = new LocService();
            }
        }
        return locService;
    }


    public void getMap(Context context, BDLocationListener listener) {
        // 定位初始化
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(listener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

    }

    public void UnRegister(BDLocationListener listener){
        mLocClient.stop();
        mLocClient.unRegisterLocationListener(listener);
    }
}
