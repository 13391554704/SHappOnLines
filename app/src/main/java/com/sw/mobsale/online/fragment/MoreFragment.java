package com.sw.mobsale.online.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.ui.ChangePwdActivity;
import com.sw.mobsale.online.ui.LoginActivity;
import com.sw.mobsale.online.ui.NetOrderActivity;
import com.sw.mobsale.online.ui.PersonInfoActivity;
import com.sw.mobsale.online.ui.PersonKnowActivity;
import com.sw.mobsale.online.ui.StoreManagerActivity;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 设置Fragment
 */
public class MoreFragment extends Fragment implements View.OnClickListener{
    //布局
    private LinearLayout llKnow, llVersion, llInfo,llDetail,llChangePwd;
    //退出按钮
    private ImageView btnBack;
    String result;
    MyHandler handler;
    String userName,userPwd,phoneCode;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        initView(view);
        return view;
    }

    /**
     *  初始化
     * @param view  view
     */
    private void initView(View view) {
        llDetail = (LinearLayout) view.findViewById(R.id.fg_more_ll_detail);
        llInfo = (LinearLayout) view.findViewById(R.id.fg_user_ll_person_info);
        llKnow = (LinearLayout) view.findViewById(R.id.fg_user_ll_person_know);
        llVersion = (LinearLayout) view.findViewById(R.id.fg_user_ll_version);
        llChangePwd = (LinearLayout) view.findViewById(R.id.fg_more_ll_change_pwd);
        btnBack = (ImageView) view.findViewById(R.id.fg_user_btn_back);
        handler = new MyHandler();
        //llVersion.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        llDetail.setOnClickListener(this);
        llVersion.setOnClickListener(this);
        llInfo.setOnClickListener(this);
        llKnow.setOnClickListener(this);
        llChangePwd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //传递数据
            case R.id.fg_more_ll_detail:
                intent = new Intent(getActivity(), StoreManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.fg_user_btn_back:
                SharedPreferences sdf = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                userName = sdf.getString("userCode","");
                userPwd = sdf.getString("password","");
                phoneCode =sdf.getString("phoneCode","");
                MyThread.getInstance(getActivity()).ForceDownThread(handler,userName,userPwd,phoneCode);
                break;
            case R.id.fg_more_ll_change_pwd:
                intent = new Intent(getActivity(), ChangePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.fg_user_ll_person_know:
                intent = new Intent(getActivity(), PersonKnowActivity.class);
                startActivity(intent);
                break;
            case R.id.fg_user_ll_person_info:
//                intent = new Intent(getActivity(), PersonInfoActivity.class);
//                startActivity(intent);
                //getActivity().finish();
                startActivity(new Intent((Settings.ACTION_SETTINGS)));
                break;
            case R.id.fg_user_ll_version:
                intent = new Intent(getActivity(), NetOrderActivity.class);
                startActivity(intent);
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
                case Constant.FORCE_DOWN:
                    result = msg.getData().getString("result");
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        try {
                            Log.d("TAG", "force down->" + result);
                            JSONObject object = new JSONObject(result);
                            String message = object.getString("errorMessage");
                            if ("err".equals(message)) {
                                ResultLog.getInstance(getActivity()).getDialog(getActivity());
                            } else {
                                //起线程  判断该帐号,密码 设备号是否匹配后台数据表
                                MyThread.getInstance(getActivity()).BackThread(handler, userName, userPwd, phoneCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constant.USER_BACK:
                    result = msg.getData().getString("result");
                    Log.d("TAG", result);
                    if(!ResultLog.getInstance(getActivity()).getNoRes(result)) {
                        ResultLog.getInstance(getActivity()).DownLog(result, getActivity());
                    }
                    break;
            }
        }
    }
}

