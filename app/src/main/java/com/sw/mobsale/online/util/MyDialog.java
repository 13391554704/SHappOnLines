package com.sw.mobsale.online.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sw.mobsale.online.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/9/5 0005
 */
public class MyDialog extends Dialog{

    private TextView tvTitle,tvMessage;
    private EditText etDebt;
    private Boolean isShow;
    //弹出框动画
    private GifImageView myGif;
    private Boolean isGif;
    private RelativeLayout rlGif;
    private LinearLayout llTeet,llBottom;
    private TextView tvCancel,tvSubmit;
    private Context context;
    private String title,message;
    private String confirm;
    private String cancel;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {
        void doConfirm(String result);
        void doConfirm();
        void doCancel();
    }

    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context,String title,String message,String confirm, String cancel,Boolean isShow,Boolean isGif) {
        super(context,R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.message = message;
        this.confirm = confirm;
        this.cancel = cancel;
        this.isShow = isShow;
        this.isGif = isGif;
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_address_my);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.dialog_title);
        tvMessage = (TextView) findViewById(R.id.dialog_message);
        etDebt = (EditText) findViewById(R.id.dialog_et_debt);
        tvCancel = (TextView) findViewById(R.id.dialog_cancel);
        tvSubmit = (TextView) findViewById(R.id.dialog_submit);
        if (isShow){
            etDebt.setVisibility(View.VISIBLE);
        }
        myGif = (GifImageView) findViewById(R.id.main_pb);
        rlGif = (RelativeLayout) findViewById(R.id.dialog_rl_pb_tv);
        llTeet = (LinearLayout)findViewById(R.id.dialog_ll_tv_et);
        llBottom = (LinearLayout) findViewById(R.id.dialog_bottom);
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = new GifDrawable(context.getResources(), R.drawable.near_loading2);
            // gif1加载一个动态图gif
            myGif .setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isGif){
            rlGif.setVisibility(View.VISIBLE);
            llTeet.setVisibility(View.GONE);
            llBottom.setVisibility(View.GONE);
        }
        tvTitle.setText(title);
        tvMessage.setText(message);
        tvCancel.setText(cancel);
        tvSubmit.setText(confirm);
        tvSubmit.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());


        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.8
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.dialog_submit:
                    if (isShow){
                        clickListenerInterface.doConfirm(etDebt.getText().toString());
                    }else {
                        clickListenerInterface.doConfirm();
                    }
                    break;
                case R.id.dialog_cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }

    }

}
