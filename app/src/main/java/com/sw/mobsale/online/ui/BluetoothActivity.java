package com.sw.mobsale.online.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.sw.mobsale.online.R;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Bluetooth
 */
public class BluetoothActivity extends Activity {
    //未配对设备
    ListView lvUnbondDevices;
    //以配对设备
    ListView lvBondDevices;
    // 用于存放未配对蓝牙设备
    private ArrayList<BluetoothDevice> unbondDevices = null;
    // 用于存放已配对蓝牙设备
    private ArrayList<BluetoothDevice> bondDevices = null;
    //搜索设备
    Button btnSearch;
    //BluetoothAdapter
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth);
        //初始化
        initListener();
    }

    private void initListener() {
        lvUnbondDevices = (ListView) findViewById(R.id.unbondDevices);
        lvBondDevices = (ListView) findViewById(R.id.bondDevices);
        btnSearch = (Button) findViewById(R.id.searchDevices);
        this.unbondDevices = new ArrayList<BluetoothDevice>();
        this.bondDevices = new ArrayList<BluetoothDevice>();
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(receiver, intentFilter);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
            }
        });
    }


    /**
     * 添加已绑定蓝牙设备到ListView
     */
    private void addBondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = this.bondDevices.size();
        // 把item项的数据加到data中
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", this.bondDevices.get(i).getName());
            data.add(map);
        }
        String[] from = {"deviceName"};
        int[] to = {R.id.device_name};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data,
                R.layout.item_bonddevice, from, to);
        // 把适配器装载到listView中
        lvBondDevices.setAdapter(simpleAdapter);
        lvBondDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                BluetoothDevice device = bondDevices.get(arg2);
                SharedPreferences spf = getSharedPreferences("bluetooth",MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("bound",device.getAddress());
                editor.commit();
                Intent intent = new Intent();
                intent.putExtra("deviceAddress", device.getAddress());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 添加未绑定蓝牙设备到ListView
     */
    private void addUnbondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = this.unbondDevices.size();
        // 把item项的数据加到data中
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", unbondDevices.get(i).getName());
            data.add(map);
        }
        String[] from = {"deviceName"};
        int[] to = {R.id.device_name};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data,
                R.layout.item_unbonddevice, from, to);

        // 把适配器装载到listView中
        lvUnbondDevices.setAdapter(simpleAdapter);

        // 为每个item绑定监听，用于设备间的配对
        lvUnbondDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                try {
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(unbondDevices.get(arg2));
                    // 将绑定好的设备添加的已绑定list集合
                    bondDevices.add(unbondDevices.get(arg2));
                    // 将绑定好的设备从未绑定list集合中移除
                    unbondDevices.remove(arg2);
                    addBondDevicesToListView();
                    addUnbondDevicesToListView();
                } catch (Exception e) {
                    Toast.makeText(BluetoothActivity.this, "配对失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 搜索蓝牙设备
     */
    public void searchDevices() {
        bondDevices.clear();
        unbondDevices.clear();
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 添加未绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void addUnbondDevices(BluetoothDevice device) {
        System.out.println("未绑定设备名称：" + device.getName());
        if (!this.unbondDevices.contains(device)) {
            this.unbondDevices.add(device);
        }
    }

    /**
     * 添加已绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void  addBandDevices(BluetoothDevice device) {
        if (!this.bondDevices.contains(device)) {
            this.bondDevices.add(device);
        }
    }

    /**
     * 蓝牙广播接收器
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        ProgressDialog progressDialog = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    addBandDevices(device);
                } else {
                    addUnbondDevices(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressDialog = ProgressDialog.show(context, "请稍等...",
                        "搜索蓝牙设备中...", true);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                System.out.println("设备搜索完毕");
                progressDialog.dismiss();

                addUnbondDevicesToListView();
                addBondDevicesToListView();
                // bluetoothAdapter.cancelDiscovery();
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    System.out.println("--------打开蓝牙-----------");
                    lvBondDevices.setEnabled(true);
                    lvUnbondDevices.setEnabled(true);
                } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    System.out.println("--------关闭蓝牙-----------");
                    lvBondDevices.setEnabled(false);
                    lvUnbondDevices.setEnabled(false);
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
