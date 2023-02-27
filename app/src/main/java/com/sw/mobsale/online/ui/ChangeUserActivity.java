package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 切换用户
 */
public class ChangeUserActivity extends Activity implements View.OnClickListener{
    private EditText etUser,etPwd;
    private Button btnRe,btnSub;
    private String inputUser,inputPwd,name,userPwd,phoneCode;
    private MyHandler handler;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_user);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        etUser = (EditText) findViewById(R.id.change_user_et_userName);
        etPwd = (EditText) findViewById(R.id.change_user_et_passwrod);
        btnRe = (Button) findViewById(R.id.change_user_clear);
        btnSub = (Button) findViewById(R.id.change_user_submit);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        userPwd = intent.getStringExtra("pwd");
        phoneCode =intent.getStringExtra("code");
        handler = new MyHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRe.setOnClickListener(this);
        btnSub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        inputUser = etUser.getText().toString();
        inputPwd = etPwd.getText().toString();
        switch (v.getId()){
            case R.id.change_user_clear:
                finish();
                break;
            case R.id.change_user_submit:
                if (("").equals(inputUser)||("").equals(inputPwd)){
                    Toast.makeText(ChangeUserActivity.this,"请输入用户名密码!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inputUser.equals(name)){
                    Toast.makeText(ChangeUserActivity.this,"该用户已登录",Toast.LENGTH_SHORT).show();
                    etUser.setText("");
                    etPwd.setText("");
                }else{
                    //起线程  判断该帐号,密码 设备号是否匹配后台数据表
                    MyThread.getInstance(ChangeUserActivity.this).BackThread(handler,name,userPwd,phoneCode);
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
            switch (msg.what){
                // 切换用户 ->用户退出
                case Constant.USER_BACK:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(ChangeUserActivity.this).getNoRes(result)) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if (message.equals("offLine")) {
                                MyThread.getInstance(ChangeUserActivity.this).LoginThread(handler, inputUser, inputPwd, phoneCode);
                            } else {
                                Toast.makeText(ChangeUserActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                // 切换用户 ->用户登陆
                case Constant.USER_LOGIN:
                    result = msg.getData().getString("result");
                    if (("").equals(result)) {
                        Toast.makeText(ChangeUserActivity.this, "服务器解析失败，请稍后重试!", Toast.LENGTH_SHORT).show();
                    }else{
                        ResultLog.getInstance(ChangeUserActivity.this).LoginLog(result, ChangeUserActivity.this, inputUser, inputPwd, phoneCode);
                    }
                    break;

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
