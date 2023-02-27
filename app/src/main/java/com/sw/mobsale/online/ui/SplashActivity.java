package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyApplication;
import com.sw.mobsale.online.util.MyLoc;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.UpdateVersion;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 引导页
 */
public class SplashActivity extends Activity {
    private String network = "";
    private MyHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
       /* UpdateVersion updateVersion = new UpdateVersion(SplashActivity.this);
        updateVersion.checkUpdate();*/
        MyLoc loc = new MyLoc(SplashActivity.this);
        loc.getPoi();
        if(!MyApplication.isNetworkAvailable(SplashActivity.this)){
            network = "false";
        }
        handler = new MyHandler();
        MyThread.getInstance(SplashActivity.this).RouteThread(handler, Constant.TEST_SERVER_PATH);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences spfName=getSharedPreferences("user", MODE_PRIVATE);
//        String code = spfName.getString("userCode","");//sk
//        Log.d("TAG",code);
//        if (code == "" ||("").equals(code)){
//            Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//            intent.putExtra("network",network);
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//            intent.putExtra("network",network);
//            startActivity(intent);
//            finish();
//        }
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.ROUTE_LINE:
                    String result = msg.getData().getString("result");
                    Log.d("TAG",result);
                    if (("").equals(result)){
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra("network",network);
                        startActivity(intent);
                        finish();
                    }else {
                        SharedPreferences spfName = getSharedPreferences("user", MODE_PRIVATE);
                        String code = spfName.getString("userCode", "");//sk
                        Log.d("TAG", code);
                        if (code == "" || ("").equals(code)) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.putExtra("network", network);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("network", network);
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
            }
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
