package com.sw.mobsale.online.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sw.mobsale.online.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定位附近adapter
 */
public class AddressAdapter extends BaseAdapter{
    Context context;
    private LayoutInflater inflater;
    private List<Map<String,Object>>storeLists = new ArrayList<Map<String, Object>>();
    private double Latitude,Longitude;
    public AddressAdapter(Context context,List<Map<String,Object>>storeLists){
        this.context = context;
        this.storeLists = storeLists;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return storeLists.size();
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
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_address_lv_new,parent,false);
            viewHolder.tvStore = (TextView) convertView.findViewById(R.id.item_address_tv_store);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.item_address_tv_address);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.item_address_tv_distance);
            viewHolder.tvUnit = (TextView) convertView.findViewById(R.id.item_address_tv_unit);
            viewHolder.ivLoc = (ImageView) convertView.findViewById(R.id.item_address_iv_address);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.item_address_tv_phone);
            viewHolder.ivPhone = (ImageView) convertView.findViewById(R.id.item_address_iv_phone);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvStore.setText((String)storeLists.get(position).get("customerName"));
        viewHolder.tvAddress.setText((String)storeLists.get(position).get("address"));
        viewHolder.tvDistance.setText((String)storeLists.get(position).get("distance"));
        viewHolder.tvUnit.setText((String)storeLists.get(position).get("unit"));
        viewHolder.tvPhone.setText((String)storeLists.get(position).get("phone"));

        //地图
        viewHolder.ivLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = viewHolder.tvAddress.getText().toString();
                getLat(address);
                Log.d("TAG","LAN->"+ Longitude + Longitude);
                Uri uri  = Uri.parse("geo:"+Latitude+","+Longitude+"?q="+address+"");
                Intent i1 = new Intent(Intent.ACTION_VIEW,uri);
                context.startActivity(i1);
            }
        });
        //电话
        if (("无").equals(viewHolder.tvPhone.getText().toString())){
            viewHolder.ivPhone.setImageResource(R.drawable.main_near_null_1);
            viewHolder.ivPhone.setEnabled(false);
        }else{
            viewHolder.ivPhone.setImageResource(R.drawable.near_phone);
            viewHolder.ivPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (!TextUtils.isEmpty(viewHolder.tvPhone.getText().toString())) {
                        intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + viewHolder.tvPhone.getText().toString()));
                    } else {
                        intent = new Intent(Intent.ACTION_CALL_BUTTON);
                    }
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }
    class ViewHolder{
        TextView tvStore;
        TextView tvAddress;
        TextView tvDistance;
        TextView tvUnit;
        TextView tvPhone;
        ImageView ivPhone;
        ImageView ivLoc;
    }
    /**
     * 将位置信息转化为经纬度信息
     * @param address
     * @return
     */
    public void getLat(String address){
        GeoCoder geoCoder = GeoCoder.newInstance();
        int i = address.indexOf("市")+1;
        String city = address.substring(0,i);
        GeoCodeOption mGeoCodeOption = new GeoCodeOption();
        mGeoCodeOption.address(address);
        mGeoCodeOption.city(city);
        Log.d("TAG","city->"+city);
        Log.d("TAG","address->"+address);
        geoCoder.geocode(mGeoCodeOption);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                Latitude = geoCodeResult.getLocation().latitude;
                Longitude = geoCodeResult.getLocation().longitude;
                Log.d("TAG","LON->"+ Longitude +" "+ Longitude);
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }
}
