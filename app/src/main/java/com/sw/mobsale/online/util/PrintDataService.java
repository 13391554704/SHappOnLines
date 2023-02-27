package com.sw.mobsale.online.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * 打印
 */
public class PrintDataService {

    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    /**
     * 打印三列时，中间一列的中心线距离打印纸左侧的距离
     */
    private static final int LEFT_LENGTH = 16;

    /**
     * 打印三列时，中间一列的中心线距离打印纸右侧的距离
     */
    private static final int RIGHT_LENGTH = 16;

    /**
     * 打印三列时，第一列汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 5;
    //复位打印机
    public static final byte[] RESET = {0x1b, 0x40};
    // 左对齐
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};
    // 中间对齐
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};
    // 右对齐
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};
    //选择加粗模式
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};
    //取消加粗模式
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};
    //宽高加倍
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};
    // 宽加倍
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};
    //高加倍
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};
    //字体不放大
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};
    // 设置默认行间距
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};
    private Context context = null;
    private String deviceAddress = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    public BluetoothDevice device = null;
    private static BluetoothSocket bluetoothSocket = null;
    private static OutputStream outputStream = null;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isConnection = false;

    public PrintDataService(Context context, String deviceAddress) {
        super();
        this.context = context;
        this.deviceAddress = deviceAddress;
        this.device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);
    }

    /**
     * 获取设备名称
     *
     * @return String
     */
    public String getDeviceName() {
        return this.device.getName();
    }

    /**
     * 连接蓝牙设备
     */
    public boolean connect() {
        if (!this.isConnection) {
            try {
                bluetoothSocket = this.device
                        .createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                this.isConnection = true;
                if (this.bluetoothAdapter.isDiscovering()) {
                    System.out.println("关闭适配器！");
                    this.bluetoothAdapter.isDiscovering();
                }
            } catch (Exception e) {
                Toast.makeText(this.context, "连接失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this.context, this.device.getName() + "连接成功！",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return true;
        }
    }

    /**
     * 断开蓝牙设备连接
     */
    public static void disconnect() {
        System.out.println("断开蓝牙设备连接");
        try {
            bluetoothSocket.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public  void selectCommand(byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public void printText(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public String printTwoData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }
    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public String printTwoRightData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        // 计算文字左侧的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength -4;
        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(leftText);
        sb.append(":");
        sb.append(rightText);
        return sb.toString();
    }
    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH);
        }
        int leftTextLength = getBytesLength(leftText);
        int middleTextLength = getBytesLength(middleText);
        int rightTextLength = getBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }
    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }
}
