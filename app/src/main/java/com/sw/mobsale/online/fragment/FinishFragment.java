package com.sw.mobsale.online.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.adapter.FgDetailAdapter;
import com.sw.mobsale.online.ui.DetailActivity;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配送订单Fragment
 */
public class FinishFragment extends Fragment {
    private ListView lvStore;
    private List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();
    private RelativeLayout rlUnFinish;
    private MyHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化
     * @param view view
     */
    private void initView(View view) {
        lvStore = (ListView) view.findViewById(R.id.fg_finish_lv);
        rlUnFinish = (RelativeLayout) view.findViewById(R.id.detail_rl_detail);
        handler = new MyHandler();
        MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_S,"", "S", "","","");
    }

    @Override
    public void onResume() {
        super.onResume();
        lvStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("orderNo",dataLists.get(position).get("number").toString());
                intent.putExtra("orderId",dataLists.get(position).get("id").toString());
                intent.putExtra("orderType","O");
                intent.putExtra("intent","sale");
                intent.putExtra("orderFrom",dataLists.get(position).get("fromCode").toString());
                startActivity(intent);
            }
        });
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.ORDER_STATUS_S) {
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");
                Log.d("TAG", "finish result->" + result);
                dataLists.clear();
                dataLists = ResultLog.getInstance(getActivity()).OrderNoLog(result);
                Log.d("TAG",  "finish ->" + dataLists.toString());
                if (dataLists.size() > 0) {
                    FgDetailAdapter adapter = new FgDetailAdapter(getActivity(), dataLists);
                    lvStore.setAdapter(adapter);
                    rlUnFinish.setVisibility(View.GONE);
                }else{
                    rlUnFinish.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    /**
     * 更新数据
     */
    public void update() {
        MyThread.getInstance(getActivity()).OrderThread(handler,Constant.ORDER_STATUS_S,"", "S", "","","");
    }

}
