package com.sw.mobsale.online.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import com.sw.mobsale.online.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单列表adapter
 */
public class FgDetailAdapter extends BaseAdapter {
    private List<Map<String,Object>> storeData = new ArrayList<Map<String, Object>>();
    private LayoutInflater inflater;
    Context context;
    public FgDetailAdapter(Context context, List<Map<String,Object>> storeData){
        this.context = context;
        this.storeData = storeData;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return storeData.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_loading_order_lv,parent,false);
            viewHolder.ivBg = (ImageView) convertView.findViewById(R.id.item_order_bg);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.item_order_name_tv);
            viewHolder.tvFrom = (TextView) convertView.findViewById(R.id.item_order_from_tv);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.item_order_address_tv);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.item_order_distance_tv);
            viewHolder.tvKm = (TextView) convertView.findViewById(R.id.item_order_km_tv);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.item_order_date_tv);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.item_order_time_tv);
            viewHolder.tvNote = (TextView) convertView.findViewById(R.id.item_order_note_tv);
            viewHolder.ivChoose = (ImageView) convertView.findViewById(R.id.item_order_number_iv_choose);
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.item_order_number_tv);
            viewHolder.tvMonty = (TextView) convertView.findViewById(R.id.item_order_money_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivBg.setImageResource((Integer) storeData.get(position).get("bg"));
        viewHolder.tvName.setText((String) storeData.get(position).get("name"));
        viewHolder.tvFrom.setText((String) storeData.get(position).get("fromName"));
        viewHolder.tvAddress.setText((String) storeData.get(position).get("address"));
        viewHolder.tvDistance.setText((String) storeData.get(position).get("distance"));
        viewHolder.tvKm.setText((String) storeData.get(position).get("unit"));
        viewHolder.tvDate.setText((String) storeData.get(position).get("date"));
        viewHolder.tvTime.setText((String) storeData.get(position).get("time"));
        viewHolder.tvNote.setText((String) storeData.get(position).get("note"));
        viewHolder.tvNumber.setText((String)storeData.get(position).get("number"));
        viewHolder.tvMonty.setText((String)storeData.get(position).get("amt"));
        viewHolder.ivChoose.setVisibility(View.GONE);
        return convertView;
    }

    class ViewHolder{
        ImageView ivBg;
        TextView tvName;
        TextView tvFrom;
        TextView tvAddress;
        TextView tvKm;
        TextView tvMonty;
        TextView tvDate;
        TextView tvTime;
        TextView tvNote;
        ImageView ivChoose;
        TextView tvNumber;
        TextView tvDistance;
    }
}
