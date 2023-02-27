package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.ScreenManager;

public class PersonKnowActivity extends BaseActivity {
    private TextView tvTitle,tvContent;
    private RelativeLayout rlBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_know);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("用户须知");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(PersonKnowActivity.this,MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });
        tvContent = (TextView) findViewById(R.id.person_content_know);
        tvContent.setText("    1.滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答答\n" +
                "    2.滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答答\n" +
                "    3.滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答答\n" +
                "    4.滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答滴答答\n");

    }
    /**
     * 返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Intent intent = new Intent(PersonKnowActivity.this,MainActivity.class);
            //startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
