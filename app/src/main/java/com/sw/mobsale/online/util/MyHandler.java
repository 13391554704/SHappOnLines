package com.sw.mobsale.online.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.sw.mobsale.online.ui.MainActivity;

/**
 * Created by sxs
 */
public class MyHandler extends Handler{
    Context context;
    private static MyHandler handler;
    String result;
    /**
     * 构造函数
     * @param context   上下文对象
     */
    public MyHandler(Context context) {
        this.context = context;
    }

    /**
     * 获取本类对象实例
     * @param context   上下文对象
     * @return
     */
    public static MyHandler getInstance(Context context) {
        if(handler == null) {
            handler = new MyHandler(context);
        }
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 111:
                
                Toast.makeText(context, "网络不给力!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(context, MainActivity.class);
              //  it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("network","false");
                context.startActivity(it);
            break;
        }
    }
}
