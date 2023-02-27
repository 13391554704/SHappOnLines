package com.sw.mobsale.online.ui;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.ScreenManager;


public class ChangePwdActivity extends BaseActivity implements View.OnClickListener{
    //界面
    private EditText etUser,etPwd,etRePwd;
    private Button btnSubmit;
    //head文件
    private TextView tvTitle;
    private RelativeLayout rlBack;
    private MyHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        etUser = (EditText) findViewById(R.id.change_pwd_et_phone);
        etPwd = (EditText) findViewById(R.id.change_pwd_ll_et_passwrod);
        etRePwd = (EditText) findViewById(R.id.change_pwd_ll_reg_et_repasswrod);
        btnSubmit = (Button) findViewById(R.id.change_pwd_btn_submit);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("修改密码");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        handler = new MyHandler();
        SharedPreferences spfName=getSharedPreferences("user", Context.MODE_PRIVATE);
        String name = spfName.getString("userCode","");
        etUser.setText(name);
        getTouth(etUser);
    }

    /**
     * 光标处于edittext末尾
     * @param textView tv
     */
    public void getTouth(EditText textView){
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            //移动到字体后面
            Selection.setSelection(spanText, text.length());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        rlBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_rl_title:
                finish();
                break;
            case R.id.change_pwd_btn_submit:
                String pwd = etPwd.getText().toString();
                String rePwd = etRePwd.getText().toString();
                if (pwd == null || pwd.trim().equals("")) {
                    Toast.makeText(ChangePwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() < 6){
                    Toast.makeText(ChangePwdActivity.this, "密码不能少于6位", Toast.LENGTH_LONG).show();
                    return;
                }
                if (rePwd == null || rePwd.trim().equals("") || !rePwd.equals(pwd)) {
                    Toast.makeText(ChangePwdActivity.this, "密码保持一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                //上传数据，更新业务员表。。
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
        }
    }
}
