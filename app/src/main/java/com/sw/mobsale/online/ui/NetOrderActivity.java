package com.sw.mobsale.online.ui;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.sw.mobsale.online.R;
import com.sw.mobsale.online.util.ScreenManager;

public class NetOrderActivity extends BaseActivity {
    private WebView webView;
    private TextView tvTitle;
    private RelativeLayout rlBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_order);
        //activity 栈
        ScreenManager.getScreenManager().pushActivity(this);
        tvTitle = (TextView) findViewById(R.id.main_title);
        tvTitle.setText("双汇微商城");
        rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.net_order_web);
        // 设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 加载需要显示的网页
        webView.loadUrl("http://ststest.shineway-soft.com/public/b2c/main/html/main.html");
        // 设置Web视图
        webView.setWebViewClient(new HelloWebViewClient());
        webView.setWebChromeClient(new HelloWebChromeClient());
    }

    // Web视图
    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }


        @Override
        public void onLoadResource(WebView view, String url) {

            super.onLoadResource(view, url);
        }
    }

    private class HelloWebChromeClient extends WebChromeClient {
        // 处理Alert事件

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            // 构建一个Builder来显示网页中的alert对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(NetOrderActivity.this);
            builder.setTitle("计算1+2的值");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            NetOrderActivity .this.setTitle("可以用onReceivedTitle()方法修改网页标题");
            super.onReceivedTitle(view, title);
        }

// 处理Confirm事件

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NetOrderActivity.this);
            builder.setTitle("删除确认");
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }

            });

            builder.setNeutralButton(android.R.string.cancel, new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }

            });

            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

        // 处理提示事件

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  JsPromptResult result) {
            // 看看默认的效果
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }
    /**
     * 设置返回键  返回webview上一个界面
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    /**
     * 在状态栏显示通知
     */
    private void showNotification(){
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification =new Notification(R.drawable.icon,
                "督导系统", System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间，毫秒

        // 设置通知的事件消息
        CharSequence contentTitle ="督导系统标题"; // 通知栏标题
        CharSequence contentText ="督导系统内容"; // 通知栏内容
        Intent notificationIntent =new Intent(NetOrderActivity.this, NetOrderActivity.class); // 点击该通知后要跳转的Activity
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
       // notification.setLatestEventInfo(this, contentTitle, contentText, contentItent);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")//设置通知栏标题
                .setContentText("测试内容") //<span style="font-family: Arial;">/设置通知栏显示内容</span>
                .setContentIntent(contentItent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.icon);//设置通知小ICON
       // Notification notification = mBuilder.build();
        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }
    //删除通知
    private void clearNotification(){
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
