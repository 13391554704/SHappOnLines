package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.Constant;
import com.sw.mobsale.online.util.MyThread;
import com.sw.mobsale.online.util.ResultLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 路线  商品分类
 */
public class RightActivity extends Activity implements View.OnClickListener{
    private TextView tvRoute,tvAll,tvServer,tvOwn;
    private ListView lvProLeft,lvProRight;//listview
    private LinearLayout llLeft;
    private Button btnRe,btnCon;//取消   确定
    private List<Map<String,Object>> leftLists = new ArrayList<Map<String,Object>>();
    private List<Map<String,Object>> leftOwnLists = new ArrayList<Map<String,Object>>();//自营商品列表
    private ArrayList<String> itemName = new ArrayList<String>();//商品id  路线id
    private ArrayList<String> itemOwnName = new ArrayList<String>();// 发送后台自营商品
    private ArrayList<String> routeName = new ArrayList<String>();//路线名称
    private Map<Integer,Integer> selected = new HashMap<Integer, Integer>(); //item 选择
    private Map<Integer,Integer> ownSelected = new HashMap<Integer, Integer>();//选中自营商品列表
    private String result;//json
    private MyHandler handler;//handler
    private String choose;//intent数据
    private RouteAdapter adapter;//adapter
    private TypeOwnAdapter ownAdapter;//adapter
    private boolean isOwn = true;//产品自营
    private boolean isServer = true;//产品平台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_right);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        tvRoute = (TextView) findViewById(R.id.right_tv_choose);
        tvAll = (TextView) findViewById(R.id.right_tv_all);
        tvOwn = (TextView)findViewById(R.id.right_left_own);
        tvServer = (TextView) findViewById(R.id.right_left_tv_server);
        lvProLeft = (ListView) findViewById(R.id.right_left_lv);
        lvProRight = (ListView) findViewById(R.id.right_left_lv_left);
        llLeft = (LinearLayout) findViewById(R.id.right_left_ll_lv);
        btnRe = (Button) findViewById(R.id.right_btn_re);
        btnCon = (Button) findViewById(R.id.right_btn_con);
        handler = new MyHandler();
        adapter = new RouteAdapter();
        getIntentData();
    }

    /**
     * 根据intent获取不同的数据
     */
    private void getIntentData() {
        Intent intent =getIntent();
        choose = intent.getStringExtra("choose");
        if (("route").equals(choose)) {
            tvRoute.setText("路线汇总");
            tvAll.setText("全选");
            tvServer.setVisibility(View.GONE);
            leftLists.clear();
            MyThread.getInstance(RightActivity.this).RouteThread(handler,Constant.LINE_ROUTE_PATH);
            return;
        }
        if(("loadSale").equals(choose)){
            tvRoute.setText("商品类别");
            llLeft.setVisibility(View.VISIBLE);
            tvAll.setVisibility(View.GONE);
            leftLists.clear();
            leftOwnLists.clear();
            ownAdapter = new TypeOwnAdapter();
            MyThread.getInstance(RightActivity.this).SaleTypeAllThread(handler,Constant.LOAD_SALE_PATH_A,"","","L");
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //自营商品
        lvProRight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ownAdapter.isItemSelected(position)){
                        ownAdapter.removeSelected(position);
                        itemOwnName.remove(leftOwnLists.get(position).get("id").toString());
                    }else{
                        ownAdapter.addSelected(position);
                        itemOwnName.add(leftOwnLists.get(position).get("id").toString());
                    }
                    ownAdapter.notifyDataSetChanged();
                Log.d("TAG",itemOwnName.toString());
                if (itemOwnName.size() == leftOwnLists.size()){
                    tvOwn.setTextColor(Color.parseColor("#e84133"));
                    tvOwn.setBackgroundResource(R.drawable.rightboder);
                }else{
                    tvOwn.setTextColor(Color.parseColor("#808080"));
                    tvOwn.setBackgroundResource(R.drawable.rightbg);
                }
            }
        });

        //平台商品  路线信息
        lvProLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(("route").equals(choose)){
                    if (adapter.isItemSelected(position)){
                        adapter.removeSelected(position);
                        itemName.remove(leftLists.get(position).get("routeCode").toString());
                        routeName.remove(leftLists.get(position).get("route").toString());
                    }else{
                        adapter.addSelected(position);
                        itemName.add(leftLists.get(position).get("routeCode").toString());
                        routeName.add(leftLists.get(position).get("route").toString());
                    }
                    adapter.notifyDataSetChanged();
                    if (itemName.size() == leftLists.size()){
                        tvAll.setTextColor(Color.parseColor("#e84133"));
                        tvAll.setBackgroundResource(R.drawable.rightboder);
                    }else{
                        tvAll.setTextColor(Color.parseColor("#808080"));
                        tvAll.setBackgroundResource(R.drawable.rightbg);
                    }
                }
                if(("loadSale").equals(choose)){
                    if (adapter.isItemSelected(position)){
                        adapter.removeSelected(position);
                        itemName.remove(leftLists.get(position).get("id").toString());
                    }else{
                        adapter.addSelected(position);
                        itemName.add(leftLists.get(position).get("id").toString());
                    }
                    adapter.notifyDataSetChanged();
                    if (itemName.size() == leftLists.size()){
                        tvServer.setTextColor(Color.parseColor("#e84133"));
                        tvServer.setBackgroundResource(R.drawable.rightboder);
                    }else{
                        tvServer.setTextColor(Color.parseColor("#808080"));
                        tvServer.setBackgroundResource(R.drawable.rightbg);
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d("TAG",itemName.toString());
            }
        });
        btnRe.setOnClickListener(this);
        btnCon.setOnClickListener(this);
        tvServer.setOnClickListener(this);
        tvOwn.setOnClickListener(this);
        tvAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //自营全选
            case R.id.right_left_own:
                if (isOwn){
                    ownAdapter.removeSelected();
                    itemOwnName.clear();
                    tvOwn.setTextColor(Color.parseColor("#e84133"));
                    tvOwn.setBackgroundResource(R.drawable.rightboder);
                    for (int i = 0; i <leftOwnLists.size();i++){
                        ownAdapter.addSelected(i);
                        ownAdapter.notifyDataSetChanged();
                        itemOwnName.add(leftOwnLists.get(i).get("id").toString());
                    }
                    isOwn = false;
                }else{
                    tvOwn.setTextColor(Color.parseColor("#808080"));
                    tvOwn.setBackgroundResource(R.drawable.rightbg);
                    ownAdapter.removeSelected();
                    itemOwnName.clear();
                    ownAdapter.notifyDataSetChanged();
                    isOwn = true;
                }
                Log.d("TAG",itemOwnName.toString());
                break;
            //平台全选
            case R.id.right_left_tv_server:
                if (isServer){
                    adapter.removeSelected();
                    itemName.clear();
                    tvServer.setTextColor(Color.parseColor("#e84133"));
                    tvServer.setBackgroundResource(R.drawable.rightboder);
                    for (int i = 0; i < leftLists.size();i++){
                        adapter.addSelected(i);
                        adapter.notifyDataSetChanged();
                        itemName.add(leftLists.get(i).get("id").toString());
                    }
                    isServer = false;
                }else{
                    tvServer.setTextColor(Color.parseColor("#808080"));
                    tvServer.setBackgroundResource(R.drawable.rightbg);
                    adapter.removeSelected();
                    itemName.clear();
                    adapter.notifyDataSetChanged();
                    isServer = true;
                }
                Log.d("TAG",itemName.toString());
                break;
            //全选
            case R.id.right_tv_all:
                String check = tvAll.getText().toString();
                if ("全选".equals(check)) {
                    adapter.removeSelected();
                    itemName.clear();
                    routeName.clear();
                    tvAll.setTextColor(Color.parseColor("#e84133"));
                    tvAll.setBackgroundResource(R.drawable.rightboder);
                    for (int i = 0; i < leftLists.size(); i++) {
                        adapter.addSelected(i);
                        adapter.notifyDataSetChanged();
                        itemName.add(leftLists.get(i).get("routeCode").toString());
                        routeName.add(leftLists.get(i).get("route").toString());
                    }
                    tvAll.setText("取消全选");
                }
                if ("取消全选".equals(check)) {
                    adapter.removeSelected();
                    tvAll.setTextColor(Color.parseColor("#808080"));
                    tvAll.setBackgroundResource(R.drawable.rightbg);
                    adapter.notifyDataSetChanged();
                    tvAll.setText("全选");
                    itemName.clear();
                    routeName.clear();
                }
                break;
            //返回
            case R.id.right_btn_re:
                finish();
                break;
            //确定
            case R.id.right_btn_con:
                Log.d("TAG",itemName.toString());
                if (("route").equals(choose)) {
                    Intent intent =getIntent();
                    String routeCode="";
                    if (itemName.size() > 0) {
                        for (int i = 0; i < itemName.size(); i++) {
                            routeCode = routeCode + "," + itemName.get(i);
                        }
                        routeCode = routeCode.substring(1);
                    }
                    String route = "";
                    if (routeName.size() > 0) {
                        for (int i = 0; i < routeName.size(); i++) {
                            route = route + "," + routeName.get(i);
                        }
                        route = route.substring(1);
                        intent.putExtra("route",routeCode);
                        intent.putExtra("routeName",route);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else{
                        Toast.makeText(RightActivity.this,"请选择路线!",Toast.LENGTH_SHORT).show();
                    }
                }
                if (("loadSale").equals(choose)){
                    Intent intent =getIntent();
                    String typeOwnCode="";
                    String typeCode="";
                    if (itemName.size() > 0 || itemOwnName.size() > 0) {
                        if (itemName.size() > 0 ) {
                            for (int i = 0; i < itemName.size(); i++) {
                                typeCode = typeCode + "," + itemName.get(i);
                            }
                            typeCode = typeCode.substring(1);
                        }
                        if (itemOwnName.size() > 0) {
                            for (int i = 0; i < itemOwnName.size(); i++) {
                                typeOwnCode = typeOwnCode + "," + itemOwnName.get(i);
                            }
                            typeOwnCode = typeOwnCode.substring(1);
                        }
                        intent.putExtra("id",typeCode);
                        intent.putExtra("ownId",typeOwnCode);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else{
                        Toast.makeText(RightActivity.this,"请选择类别!",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    /**
     * 处理信息
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //路线信息
                case Constant.ROUTE_LINE:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(RightActivity.this).getNoRes(result)) {
                        leftLists = ResultLog.getInstance(RightActivity.this).getRoute(result);
                        lvProLeft.setAdapter(adapter);
                    }
                    break;
                //分类信息
                case  Constant.LOAD_SALE:
                    result = msg.getData().getString("result");
                    if (!ResultLog.getInstance(RightActivity.this).getNoRes(result)) {
                        leftLists = ResultLog.getInstance(RightActivity.this).getItemType(result);
                        lvProLeft.setAdapter(adapter);
                        leftOwnLists = ResultLog.getInstance(RightActivity.this).getItemOwnType(result);
                        lvProRight.setAdapter(ownAdapter);
                    }
                    break;
            }
        }
    }

    /**
     * adapter
     */
    class RouteAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return leftLists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 添加选中项
         * @param position
         */
        public void addSelected(int position){
            selected.put(position,position);
            Log.d("TAG",selected.toString());

        }

        /**
         * 删除选中项
         * @param position
         */
        public void removeSelected(int position){
            if (selected.containsKey(position)){
                selected.remove(position);
                Log.d("TAG",selected.toString());
            }
        }
        /**
         * 删除所有
         */
        public void removeSelected(){
            selected.clear();
        }

        /**
         * 是有选中
         * @param position
         * @return
         */
        public boolean isItemSelected(int position){
            return  selected.containsKey(position) ? true : false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(RightActivity.this).inflate(R.layout.item_right_lv, parent, false);
                viewHolder.tvRoute = (TextView) convertView.findViewById(R.id.item_right_tv_one);
                viewHolder.tvGone = (TextView) convertView.findViewById(R.id.item_right_tv_gone);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvRoute.setTextColor(Color.parseColor("#333333"));
            //viewHolder.tvRoute.setBackgroundColor(Color.parseColor("#ffffff"));
            viewHolder.tvRoute.setBackgroundResource(R.drawable.rightbg);
            viewHolder.tvGone.setVisibility(View.VISIBLE);
            if (("route").equals(choose)) {
                viewHolder.tvRoute.setText(leftLists.get(position).get("route").toString());
            }else{
                viewHolder.tvRoute.setText(leftLists.get(position).get("typeName").toString());
            }
            //判读选中集合饱含条目就把选中的图片显示出来
          //  if (chooseRoute.contains(position)){
            if (selected.containsKey(position)){
                viewHolder.tvRoute.setTextColor(Color.parseColor("#e84133"));
                //viewHolder.tvRoute.setBackgroundColor(Color.parseColor("#f8f8f8"));
                viewHolder.tvRoute.setBackgroundResource(R.drawable.rightboder);
                viewHolder.tvGone.setVisibility(View.VISIBLE);
            }else{
                viewHolder.tvRoute.setTextColor(Color.parseColor("#333333"));
                //viewHolder.tvRoute.setBackgroundColor(Color.parseColor("#ffffff"));
                viewHolder.tvRoute.setBackgroundResource(R.drawable.rightbg);
                viewHolder.tvGone.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        class ViewHolder{
            TextView tvRoute;
            TextView tvGone;
        }
    }

    /**
     * adapter
     */
    class TypeOwnAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return leftOwnLists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 添加选中项
         * @param position
         */
        public void addSelected(int position){
            ownSelected.put(position,position);
            Log.d("TAG",ownSelected.toString());

        }

        /**
         * 删除选中项
         * @param position
         */
        public void removeSelected(int position){
            if (ownSelected.containsKey(position)){
                ownSelected.remove(position);
                Log.d("TAG",ownSelected.toString());
            }
        }
        /**
         * 删除所有
         */
        public void removeSelected(){
            ownSelected.clear();
        }

        /**
         * 是有选中
         * @param position
         * @return
         */
        public boolean isItemSelected(int position){
            return  ownSelected.containsKey(position) ? true : false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(RightActivity.this).inflate(R.layout.item_right_lv, parent, false);
                viewHolder.tvRoute = (TextView) convertView.findViewById(R.id.item_right_tv_one);
                viewHolder.tvGone = (TextView) convertView.findViewById(R.id.item_right_tv_gone);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvRoute.setTextColor(Color.parseColor("#333333"));
            viewHolder.tvRoute.setBackgroundResource(R.drawable.rightbg);
            viewHolder.tvGone.setVisibility(View.VISIBLE);
            viewHolder.tvRoute.setText(leftOwnLists.get(position).get("typeName").toString());
            //判读选中集合饱含条目就把选中的图片显示出来
            if (ownSelected.containsKey(position)){
                viewHolder.tvRoute.setTextColor(Color.parseColor("#e84133"));
                viewHolder.tvRoute.setBackgroundResource(R.drawable.rightboder);
                viewHolder.tvGone.setVisibility(View.VISIBLE);
            }else{
                viewHolder.tvRoute.setTextColor(Color.parseColor("#333333"));
                viewHolder.tvRoute.setBackgroundResource(R.drawable.rightbg);
                viewHolder.tvGone.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        class ViewHolder{
            TextView tvRoute;
            TextView tvGone;
        }
    }
    //重写此方法用来设置当点击activity外部时候，关闭此弹出框
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        finish();
        return true;
    }
    //此方法在布局文件中定义，用来保证点击弹出框内部的时候不会被关闭，如果不设置此方法则单击弹出框内部时候会导致弹出框关闭
    public void tip(View view)
    {
        Toast.makeText(this, "点击弹出框外部关闭窗口~", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
