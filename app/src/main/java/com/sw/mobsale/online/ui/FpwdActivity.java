package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.ScreenManager;
import com.sw.mobsale.online.util.SystemBarTintManager;

/**
 * 忘记密码
 */
public class FpwdActivity extends BaseActivity implements View.OnClickListener{
    private EditText etUser,etRegPwd, etRegRepwd,etCode;   //EditText 用户名 密码 重复密码 验证码
    private TextView tvCode;//显示文字
    private Button btnSumbit; //提交按钮
    private TextView tvHead;  //head文件 标题
    private RelativeLayout rlBack;  //head文件 返回
    int count = 60;//时间
    MyHandler handler;// handler
    String userName;//用户名
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpwd);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
        //通知栏颜色
        SystemBarTintManager mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintColor(Color.parseColor("#e84232"));
    }

    /**
     * 初始化
     */
    private void initView() {
        etUser = (EditText) findViewById(R.id.fpwd_et_phone);
        etRegPwd = (EditText) findViewById(R.id.fpwd_ll_et_passwrod);
        etRegRepwd = (EditText) findViewById(R.id.fpwd_ll_reg_et_repasswrod);
        etCode = (EditText) findViewById(R.id.fpwd_ll_et_code);
        tvCode = (TextView) findViewById(R.id.fpwd_ll_code_tv);
        btnSumbit = (Button) findViewById(R.id.fpwd_btn_submit);
        tvHead = (TextView) findViewById(R.id.main_title);
        tvHead.setText("忘记密码");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        Intent intent = getIntent();
        userName = intent.getStringExtra("user");
        etUser.setText(userName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSumbit.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        tvCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_rl_title:
                finish();
                break;
            case R.id.fpwd_ll_code_tv:
                new Thread(){
                  @Override
                  public void run() {
                      super.run();
                      try {
                          while(count > 0) {
                              //从后台获取验证码
                              count--;
                              Thread.sleep(1000);
                              Message msg = new Message();
                              msg.what = 1;
                              handler.sendMessage(msg);
                          }
                          Message msg = new Message();
                          msg.what = 2;
                          handler.sendMessage(msg);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  }
              }.start();
              break;
          case R.id.fpwd_btn_submit:
              String pwd = etRegPwd.getText().toString();
              String rePwd = etRegRepwd.getText().toString();
              String code = etCode.getText().toString();
              if (pwd == null || pwd.trim().equals("")) {
                  Toast.makeText(FpwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                  return;
              }
              if (pwd.length() < 6){
                  Toast.makeText(FpwdActivity.this, "密码不能少于6位", Toast.LENGTH_LONG).show();
                  return;
              }
              if (rePwd == null || rePwd.trim().equals("") || !rePwd.equals(pwd)) {
                  Toast.makeText(FpwdActivity.this, "密码保持一致", Toast.LENGTH_SHORT).show();
                  return;
              }
              if (code == null ){
                  Toast.makeText(FpwdActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
                  return;
              }
              //提交数据  后台验证。。

              Intent it = new Intent();
              it.putExtra("name", userName);
              it.putExtra("password", pwd);
              setResult(RESULT_OK, it);
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
             switch (msg.what){
                 case 1:
                     tvCode.setText("剩余"+count+"秒");
                     tvCode.setTextColor(Color.parseColor("#e0e0e0"));
                     tvCode.setEnabled(false);
                     tvCode.setBackgroundResource(R.drawable.regllboder);
                     break;
                 case 2:
                     tvCode.setText("获取验证码");
                     tvCode.setTextColor(Color.parseColor("#dd7373"));
                     tvCode.setEnabled(true);
                     tvCode.setBackgroundResource(R.drawable.regtvboder);
                     break;
             }
         }
     }
}

