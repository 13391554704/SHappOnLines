package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.SystemBarTintManager;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * BaseActivity
 */
public class BaseActivity extends Activity {
    public String userName,userPwd,phoneCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        MyHandler handler = new MyHandler();
        //通知栏颜色
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(Color.parseColor("#e84232"));
        SharedPreferences sdf = getSharedPreferences("user", Context.MODE_PRIVATE);
        userName = sdf.getString("userCode","");
        userPwd = sdf.getString("password","");
        phoneCode =sdf.getString("phoneCode","");
        MyThread.getInstance(BaseActivity.this).ForceDownThread(handler,userName,userPwd,phoneCode);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * handler
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.FORCE_DOWN:
                    String result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(BaseActivity.this).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(BaseActivity.this).getDialog(BaseActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
            }
        }
    }
}
