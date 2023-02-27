package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.CodeUtils;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SystemBarTintManager;
import java.util.UUID;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText etUserName,etUserPwd,etCode;  //用户名  密码 验证码
    private Button btnLogin; //登录按钮
    private TextView tvFpwd; // 忘记密码
    private ImageView ivCode;//验证码tupian
    private String userName,userPwd,phoneCode;//输入的用户名，密码。设备号
    private MyHandler handler;//handler
    private long exitTime = 0; //离开时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        etUserName  = (EditText) findViewById(R.id.et_userName);
        etUserPwd = (EditText) findViewById(R.id.et_passwrod);
        etCode = (EditText) findViewById(R.id.et_verify);
        ivCode = (ImageView) findViewById(R.id.iv_code);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvFpwd = (TextView) findViewById(R.id.ll_tv_fpwd);
        ivCode.setImageBitmap(CodeUtils.getInstance().createBitmap());
        handler = new MyHandler();
        //通知栏颜色
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(Color.parseColor("#ffffff"));
        Intent intent = getIntent();
        String network = intent.getStringExtra("network");
        if (("false").equals(network)){
           Toast.makeText(this,"网络异常,请重新设置网络!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //点击事件
        ivCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvFpwd.setOnClickListener(this);
    }

    /**
     * 用户点击事件
     *
     * @param v v
     */
    @Override
    public void onClick(View v) {
        //获取用户输入的名字 密码 验证码  设备号
        userName = etUserName.getText().toString().trim();
        userPwd = etUserPwd.getText().toString();
        String code = etCode.getText().toString();
        phoneCode = getPhoneNumber();
        switch (v.getId()) {
            //图片验证码
            case R.id.iv_code:
                ivCode.setImageBitmap(CodeUtils.getInstance().createBitmap());
                break;
            //登陆
            case R.id.btnLogin:
                //判断帐号密码
                ResultLog.getInstance(LoginActivity.this).ConfirmLog(LoginActivity.this,userName,userPwd,code);
                //后台判断
                MyThread.getInstance(LoginActivity.this).LoginThread(handler,userName,userPwd,phoneCode);
                break;
            //找回密码
            case R.id.ll_tv_fpwd:
                Intent intent = new Intent(LoginActivity.this, FpwdActivity.class);
                intent.putExtra("user",userName);
                startActivityForResult(intent,Constant.INTENT_FPWD);
                break;
        }
    }

    /**
     * 跳转界面数据
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.INTENT_FPWD){
            if (resultCode ==RESULT_OK){
                String userName = data.getExtras().getString("name");
                String userPwd = data.getExtras().getString("password");
                etUserName.setText(userName);
                etUserPwd.setText(userPwd);
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
            if (msg.what == Constant.USER_LOGIN) {
                String  result = msg.getData().getString("result");
                Log.d("TAG","Login -->" + result);
                if (("").equals(result)) {
                    Toast.makeText(LoginActivity.this,"服务器解析失败，请稍后重试!",Toast.LENGTH_SHORT).show();
                }else {
                    ResultLog.getInstance(LoginActivity.this).LoginLog(result, LoginActivity.this, userName, userPwd, phoneCode);
                }
            }
        }
    }
    /**
     * 获取设备号
     * @return 设备号
     */
    public String getPhoneNumber() {
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        // return uniqueId;
        return tmDevice;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * 连续点击两次退出
     * @param keyCode keyCode
     * @param event event
     * @return boolean
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
