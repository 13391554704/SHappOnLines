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
 * 订单详情adapter
 */
public class IndentAdapter extends BaseAdapter {
    private List<Map<String,Object>> dataLists = new ArrayList<Map<String, Object>>();
    Context context;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    public IndentAdapter(Context context,List<Map<String,Object>>dataLists){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dataLists = dataLists;
        this.imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chanpin) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chanpin) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chanpin) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_indent_lv,parent,false);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.indent_item_iv);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.indent_item_tv_name);
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.indent_item_tv_info);
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.indent_item_tv_number);
            viewHolder.tvUnitName = (TextView) convertView.findViewById(R.id.indent_item_tv_unitName);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.indent_item_tv_price);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path = dataLists.get(position).get("url").toString();
        imageLoader.displayImage(path,viewHolder.ivIcon,options);
        viewHolder.tvName.setText((String) dataLists.get(position).get("name"));
        viewHolder.tvInfo.setText((String)dataLists.get(position).get("info"));
        viewHolder.tvNumber.setText((String)dataLists.get(position).get("saleNum"));
        viewHolder.tvUnitName.setText((String)dataLists.get(position).get("unitName"));
        viewHolder.tvPrice.setText((String)dataLists.get(position).get("price"));

        return convertView;
    }
    class ViewHolder{
        ImageView ivIcon;
        TextView tvName;
        TextView tvInfo;
        TextView tvNumber;
        TextView tvUnitName;
        TextView tvPrice;
    }
}
