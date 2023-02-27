package com.sw.mobsale.online.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sw.mobsale.online.R;

public class QueryStoreActivity extends BaseActivity {
    private EditText etName;
    private Button btnConfirm;
    private TextView tvTitle;
    private RelativeLayout rlBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_store);
        initView();
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.store_et_name);
        btnConfirm = (Button) findViewById(R.id.store_name_btn);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("查找商铺");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeName = etName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("storeName",storeName);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
    }
}
