package com.sw.mobsale.online.util;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Administrator on 2017/8/18 0018
 */
public class ScreenManager {
    //activity任务栈管理
    private static Stack<Activity> activityStack;
    private static ScreenManager instance;
    private  ScreenManager(){
    }
    public static ScreenManager getScreenManager(){
        if(instance==null){
            instance=new ScreenManager();
        }
        return instance;
    }
    //退出栈顶Activity
    public void popActivity(Activity activity){
        if(activity!=null){
            activity.finish();
            activityStack.remove(activity);
            activity=null;
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    //退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls){
        try {
            for (Activity activity : activityStack) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
//        while(true){
//            Activity activity=currentActivity();
//            if(activity==null){
//                break;
//            }
//            if(activity.getClass().equals(cls) ){
//                break;
//            }
//            popActivity(activity);
//        }
    }
}
