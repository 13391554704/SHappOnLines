package com.sw.mobsale.online.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.sw.mobsale.online.ui.MainActivity;
import com.sw.mobsale.online.ui.SplashActivity;

/**
 * BroadcastReceiver
 */
public class MyReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
       // if (!isNetworkAvailable(context)) {
        if(!MyApplication.isNetworkAvailable(context)){
            Toast.makeText(context, "网络不给力!", Toast.LENGTH_SHORT).show();
            Intent it = new Intent(context, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("network","false");
            context.startActivity(it);
        }else{
            Toast.makeText(context, "网络正常!", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
