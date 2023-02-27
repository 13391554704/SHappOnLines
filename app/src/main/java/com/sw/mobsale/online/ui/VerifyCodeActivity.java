package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SystemBarTintManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 授权码
 */
public class VerifyCodeActivity extends Activity implements View.OnClickListener{
    //edittext 验证码
    private EditText etCode;
    //button 确定
    private Button btnSubmit;
    //head 返回
    private RelativeLayout rlBack;
    //json 用户名 密码 设备号
    private String result,name,password,phoneCode;
    // handler
    private MyHandler handler;
    //code
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verify_code);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        //通知栏颜色
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(Color.parseColor("#e84232"));
        etCode = (EditText) findViewById(R.id.code_et_code);
        btnSubmit = (Button) findViewById(R.id.code_btn_submit);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        handler = new MyHandler();
        getIntentData();
    }

    /**
     * intent数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("userCode");
        password = intent.getStringExtra("password");
        phoneCode = intent.getStringExtra("phoneCode");
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSubmit.setOnClickListener(this);
        rlBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        code = etCode.getText().toString().trim();
        switch (v.getId()){
            case R.id.code_btn_submit:
                MyThread.getInstance(VerifyCodeActivity.this).VerifyCodeThread(handler,name,password,phoneCode,code);
                break;
            case R.id.head_rl_title:
                finish();
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
            if (msg.what == Constant.VERIFY_CODE) {
                result = msg.getData().getString("result");
                Log.d("TAG", "code->"+result);
                if (!ResultLog.getInstance(VerifyCodeActivity.this).getNoRes(result)) {
                    ResultLog.getInstance(VerifyCodeActivity.this).CodeLog(result, VerifyCodeActivity.this, name, password, phoneCode);
                }
            }
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
