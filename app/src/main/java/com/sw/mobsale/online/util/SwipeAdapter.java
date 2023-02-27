package com.sw.mobsale.online.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.ui.CarLoadActivity;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import java.util.Map;


public class SwipeAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private ArrayList<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();
    private Context mContext;
    private SwipeView mOldSwipeView;
    private String result;
    private MyHandler handler;

    public SwipeAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        handler = new MyHandler();
    }

    public void setListData( ArrayList<Map<String,Object>> dataLists) {
        this.dataLists = dataLists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataLists.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        SwipeView swipeView = (SwipeView) convertView;
        if (swipeView == null) {
            View view = mInflater.inflate(R.layout.item_order_number_lv, parent,false);
            swipeView = new SwipeView(mContext);
            swipeView.setContentItemView(view);
            holder = new ViewHolder(swipeView);
            swipeView.setOnSlideListener(new OnSlideListener() {

                @Override
                public void onSlide(View view, int status) {

                    if (mOldSwipeView != null && mOldSwipeView != view) {
                        mOldSwipeView.shrink();
                    }

                    if (status == SLIDE_STATUS_ON) {
                        mOldSwipeView = (SwipeView) view;
                    }
                }
            });
            swipeView.setTag(holder);
        } else {
            holder = (ViewHolder) swipeView.getTag();
        }
        if (SwipeListView.mSwipeView != null) {
            SwipeListView.mSwipeView.shrink();
        }

        holder.ivBg.setImageResource((Integer) dataLists.get(position).get("bg"));
        holder.tvName.setText((String) dataLists.get(position).get("name"));
        holder.tvAddress.setText((String) dataLists.get(position).get("address"));
        holder.tvDate.setText((String) dataLists.get(position).get("date"));
        holder.tvTime.setText((String) dataLists.get(position).get("time"));
        holder.tvNote.setText((String) dataLists.get(position).get("note"));
        holder.tvNumber.setText((String)dataLists.get(position).get("number"));
        holder.tvMonty.setText((String)dataLists.get(position).get("amt"));
        holder.ivChoose.setVisibility(View.GONE);
        holder.tvDistance.setVisibility(View.GONE);
        holder.tvKm.setVisibility(View.GONE);
        holder.leftView.setText("删除");
        holder.rightView.setText("明细");
        holder.leftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNo = dataLists.get(position).get("number").toString();
                MyThread.getInstance(mContext).OrderThread(handler, Constant.ORDER_STATUS_K,"","K",orderNo,"","");
            }
        });
        holder.rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dataLists.get(position).get("name").toString();
                Intent intent = new Intent(mContext, CarLoadActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("flag","loading");
                intent.putExtra("address",holder.tvAddress.getText().toString());
                intent.putExtra("orderNo",holder.tvNumber.getText().toString());
                intent.putExtra("amt",holder.tvMonty.getText().toString());
                mContext.startActivity(intent);
            }
        });
        return swipeView;
    }

    static class ViewHolder {
        ImageView ivBg;
        TextView tvName;
        TextView tvAddress;
        TextView tvMonty;
        TextView tvDate;
        TextView tvTime;
        TextView tvNote;
        ImageView ivChoose;
        TextView tvNumber;
        TextView tvDistance;
        TextView tvKm;
        TextView leftView;
        TextView rightView;
        ViewHolder(View convertView){
            ivBg = (ImageView) convertView.findViewById(R.id.item_order_bg);
            tvName = (TextView) convertView.findViewById(R.id.item_order_name_tv);
            tvAddress = (TextView) convertView.findViewById(R.id.item_order_address_tv);
            tvDistance = (TextView) convertView.findViewById(R.id.item_order_distance_tv);
            tvKm = (TextView) convertView.findViewById(R.id.item_order_km_tv);
            tvDate = (TextView) convertView.findViewById(R.id.item_order_date_tv);
            tvTime = (TextView) convertView.findViewById(R.id.item_order_time_tv);
            tvNote = (TextView) convertView.findViewById(R.id.item_order_note_tv);
            ivChoose = (ImageView) convertView.findViewById(R.id.item_order_number_iv_choose);
            tvNumber = (TextView) convertView.findViewById(R.id.item_order_number_tv);
            tvMonty = (TextView) convertView.findViewById(R.id.item_order_money_tv);
            leftView = (TextView) convertView.findViewById(R.id.tv_left);
            rightView = (TextView) convertView.findViewById(R.id.tv_right);
        }
    }

    /**
     * handler
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.ORDER_STATUS_K) {
                result = msg.getData().getString("result");
                try {
                    JSONObject object = new JSONObject(result);
                    String message = object.getString("errorMessage");
                    if ("OK".equals(message)) {
                        MyThread.getInstance(mContext).OrderThread(handler, Constant.ORDER_STATUS_A,"","A","","","");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg.what == Constant.ORDER_STATUS_A) {
                result = msg.getData().getString("result");
                Log.d("TAG", "loading result->" + result);
                dataLists.clear();
                //dataLists = ResultLog.getInstance().OrderLog(,result);
                Log.d("TAG", "loading  data->" + dataLists.toString());
                notifyDataSetChanged();
            }
        }
    }

    interface IUptateData{
        public void update();
    }
}
