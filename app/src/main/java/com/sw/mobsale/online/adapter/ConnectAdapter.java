package com.sw.mobsale.online.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.sw.mobsale.online.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交班数据adapter
 */
public class ConnectAdapter extends BaseAdapter{
    Context context;
    private LayoutInflater inflater;
    private List<Map<String,Object>> sellList = new ArrayList<Map<String, Object>>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    public ConnectAdapter(Context context,List<Map<String,Object>>sellList){
        this.context = context;
        this.sellList = sellList;
        this.inflater = LayoutInflater.from(context);
        this.imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chanpin) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chanpin) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chanpin) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
                .build(); // 构建完成
    }
    @Override
    public int getCount() {
        return sellList.size();
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
        if(null == convertView){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_connect_sell_lv,parent,false);
            viewHolder.ivPro = (ImageView) convertView.findViewById(R.id.item_connect_iv_pro);
            viewHolder.tvProName = (TextView) convertView.findViewById(R.id.item_connect_tv_pro_name);
            viewHolder.tvProInfo = (TextView) convertView.findViewById(R.id.item_connect_tv_pro_info);
            viewHolder.tvProWei = (TextView) convertView.findViewById(R.id.item_connect_tv_sell_wei);
            viewHolder.tvUnitName = (TextView) convertView.findViewById(R.id.item_connect_tv_sell_unitname);
            viewHolder.tvSellNumber = (TextView) convertView.findViewById(R.id.item_connect_tv_sell_number);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path = sellList.get(position).get("url").toString();
        imageLoader.displayImage(path, viewHolder.ivPro, options);
        viewHolder.tvProName.setText((String)sellList.get(position).get("name"));
        viewHolder.tvProInfo.setText((String)sellList.get(position).get("info"));
        viewHolder.tvProWei.setText((String)sellList.get(position).get("itemWeight"));
        viewHolder.tvUnitName.setText((String)sellList.get(position).get("unitName"));
        viewHolder.tvSellNumber.setText((String)sellList.get(position).get("sumQty"));
        return convertView;
    }
    class ViewHolder {
        ImageView ivPro;
        TextView tvProName;
        TextView tvProInfo;
        TextView tvProWei;
        TextView tvUnitName;
        TextView tvSellNumber;
    }
}
