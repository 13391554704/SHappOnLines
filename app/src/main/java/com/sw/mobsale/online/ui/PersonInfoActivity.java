package com.sw.mobsale.online.ui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.ScreenManager;

public class PersonInfoActivity extends BaseActivity {
    // 性别
    private RadioGroup rgSex;
    private RadioButton rbMale, rbFemale;
    private TextView tvTitle;
    private EditText etName,etAge;
    private RelativeLayout rlBack;
    private MyHandler handler;
    //设置
    private Button btnSetting;
    String sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("个人信息");
        etName = (EditText) findViewById(R.id.info_et_name);
        etAge = (EditText) findViewById(R.id.info_et_age);
        btnSetting = (Button) findViewById(R.id.info_btn_setting);
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //性别  男  女
        rbMale = (RadioButton) findViewById(R.id.info_rb_male);
        rbFemale = (RadioButton) findViewById(R.id.info_rb_female);
        rgSex = (RadioGroup) findViewById(R.id.info_rg_sex);
        handler = new MyHandler();
        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbMale.getId()) {
                    rbFemale.setChecked(false);
                    rbMale.setChecked(true);
                    SharedPreferences spf = getSharedPreferences("info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = spf.edit();
                    sex = "男";
                    editor.putString("sex",sex);
                    editor.commit();

                }
                if (checkedId == rbFemale.getId()) {
                    rbFemale.setChecked(true);
                    rbMale.setChecked(false);
                    SharedPreferences spf = getSharedPreferences("info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = spf.edit();
                    sex = "女";
                    editor.putString("sex",sex);
                    editor.commit();

                }
            }
        });
        SharedPreferences spfName=getSharedPreferences("info", MODE_PRIVATE);
        String name = spfName.getString("name","");
        String age = spfName.getString("age","");
         String male = spfName.getString("sex","");
        if (("男").equals(male)){
            rbMale.setChecked(true);
        }else{
            rbFemale.setChecked(true);
        }
        etAge.setText(age);
        etName.setText(name);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的名字密码
                String userName = etName.getText().toString();
                String age = etAge.getText().toString();
                SharedPreferences spf = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("name",userName);
                editor.putString("age",age);
                editor.putString("sex",sex);
                editor.commit();
                Toast.makeText(PersonInfoActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
            }
        });
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
