package com.sw.mobsale.online.util;

import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 请求
 */
public class MyThread {
    Context context;
    private static MyThread myThread;
    String result;
    /**
     * 构造函数
     * @param context   上下文对象
     */
    private MyThread(Context context) {
        this.context = context;
    }

    /**
     * 获取本类对象实例
     * @param context   上下文对象
     * @return
     */
    public static MyThread getInstance(Context context) {
        if(myThread == null) {
            myThread = new MyThread(context);
        }
        return myThread;
    }

    /**
     * 登录
     */
    public void LoginThread(final Handler handler,final String name,final String userPwd,final String phoneCode){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String postData = MyUtils.getInstance(context).getLoginPostData(name,userPwd,phoneCode,"");
                    Log.d("TAG","login -->" + postData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.LOGIN_PATH,postData);
                    SendMessage(handler,Constant.USER_LOGIN,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * verifyCode
     */
    public void VerifyCodeThread(final Handler handler,final String name,final String userPwd,final String phoneCode,final String code){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String postData = MyUtils.getInstance(context).getLoginPostData(name,userPwd,phoneCode,code);
                    Log.d("TAG","code->"+postData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.VERIFY_CODE_PATH,postData);
                    SendMessage(handler,Constant.VERIFY_CODE,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 退出
     */
    public void BackThread(final Handler handler,final String name,final String userPwd,final String phoneCode){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String postData = MyUtils.getInstance(context).getLoginPostData(name,userPwd,phoneCode,"");
                    Log.d("TAG","down->"+postData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.BACK_PATH,postData);
                    SendMessage(handler,Constant.USER_BACK,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 强制下线
     * @param handler handler
     */
    public void ForceDownThread(final Handler handler,final String name,final String userPwd,final String phoneCode){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData =  MyUtils.getInstance(context).getLoginPostData(name,userPwd,phoneCode,"");
                    Log.d("TAG","force down->"+getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.FORCE_BACK_PATH,getMetSendData);
                    SendMessage(handler,Constant.FORCE_DOWN,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * main 信息
     * @param handler handler
     * @param path url
     * @param flag flag
     * @param oldCar old car
     * @param newCar new car
     * @param oldWorker old worker
     * @param newWorker new worker
     */
    public void CarInfoThread(final Handler handler,final String path,final int flag,final String oldCar,final String newCar,final String oldWorker,final String newWorker){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String postData = MyUtils.getInstance(context).getInfoPostData(oldCar,newCar,oldWorker,newWorker);
                    Log.d("TAG","Main car ->" + postData);
                    result = HttpUtils.getIntance(context).sendPostHttp(path,postData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 路线信息
     * @param handler
     */
    public void RouteThread(final Handler handler,final String path){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getSalePostData();
                    Log.d("TAG","route->"+getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(path,getMetSendData);
                    SendMessage(handler,Constant.ROUTE_LINE,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取零售信息
     * @param handler
     * @param path
     * @param itemTypeId
     */
    public void SaleTypeAllThread(final Handler handler, final String path, final String itemTypeId,final String itemTypeOwnId, final String status){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getSaleTypePostData(itemTypeId,itemTypeOwnId,status);
                    Log.d("TAG"," retail sale->" + getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(path,getMetSendData);
                    SendMessage(handler,Constant.LOAD_SALE,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }

    /**
     * 根据路线订单头信息
     * loadDetail --订单来源 平台  自营
     * @param handler handler
     * @param route 路线
     */
    public void OrderThread(final Handler handler,final String route){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getOrderPostData(route,"","","","");
                    Log.d("TAG",getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.LOAD_ORDER_PATH,getMetSendData);
                    SendMessage(handler,Constant.LOAD_ORDER,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 订单明细 -> 服务器
     * carload
     * @param handler
     * @param orderNum
     */
    public void OrderDetailThread(final Handler handler,final String path,final String orderNum,final String orderId,final String dataFrom){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getOrderPostData("","D",orderNum,orderId,dataFrom);
                Log.d("TAG",getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(path,getMetSendData);
                    SendMessage(handler,Constant.ORDER_DETAIL,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 订单头信息
     * loadDetail
     * @param handler handler
     * @param flag flag message标识
     * @param route 路线
     * @param status 状态
     * @param orderNum 订单号
     * @param orderId 订单id
     * @param dataFrom 订单来源
     */
    public void OrderThread(final Handler handler,final int flag,final String route,final String status,final String orderNum,final String orderId,final String dataFrom){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getOrderPostData(route,status,orderNum,orderId,dataFrom);
                    Log.d("TAG",getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.LOAD_ORDER_ROUTE_PATH_ONLINE,getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 加入后台零售
     * 服务器->后台表
     * @param handler
     * @param qty 数量
     * @param flag message 标识
     * @param id 单品代码
     * @param status 状态
     */
    public void ChooseShopCarThread(final Handler handler,final String qty,final int flag, final String id,final String status){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getShopCarPostData(id,qty,status,"","");
                Log.d("TAG","shop_car" + getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.LOAD_SALE_PATH_RETAIL,getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 装车完毕，出发 loading
     * @param handler
     */
    public void StartThread(final Handler handler,final String status){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getOrderPostData("", status,"","","");
                Log.d("TAG", getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.LOAD_ORDER_ROUTE_PATH_ONLINE, getMetSendData);
                    SendMessage(handler,Constant.ORDER_STATUS_Y,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 零售销售
     * @param handler handler
     * @param qty 数量
     * @param flag message 标识
     * @param id 单品代码
     * @param status 状态
     * @param buyercode 买家代码
     * @param buyername 买家名称
     * @param receiveaddress 买家地址
     */
    public void ConfirmOrderThread(final Handler handler,final String qty,final int flag, final String id,final String status,final String transNo,final String orderId,final String buyercode, final String buyername, final String receiveaddress,final String provinceareaid,final String cityareaid,final String countyareaid,final String mobileno){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getShopCarPostData(id,qty,status,transNo,orderId,buyercode,buyername,receiveaddress,provinceareaid,cityareaid,countyareaid,mobileno);
                Log.d("TAG","shop_car" + getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.SALE_PATH_RETAIL,getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     *
     * @param handler
     * @param qty
     * @param flag
     * @param id
     * @param status
     * @param transNo
     * @param orderId
     */
    public void RetailThread(final Handler handler,final String qty,final int flag, final String id,final String status,final String transNo,final String orderId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getShopCarPostData(id,qty,status,transNo,orderId);
                Log.d("TAG","shop_car" + getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.SALE_PATH_RETAIL,getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    /**
     * 地址
     * @param handler handler
     * @param parentareacode parentareacode
     * @param areaattribute areaattribute P---省 R---市 C---区
     * @param parentareaid parentareaid
     */
    public void AddressThread(final Handler handler, final String status, final String parentareacode, final String areaattribute, final String parentareaid, final int flag){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String postData = MyUtils.getInstance(context).getAddresstData(status,parentareacode,areaattribute,parentareaid);
                    Log.d("TAG","address -->" + postData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.SALE_PATH_RETAIL,postData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取支付方式  pay
     * @param handler
     * @param flag
     * @param payId
     */
    public void PayUrlThread(final Handler handler,final int flag,final String orderNo,final String orderFrom,final String payId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getOrderPay(orderNo,orderFrom,payId,"","");
                    Log.d("TAG","payModel->"+getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.PAY_PATH,getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 支付订单 pay
     * @param handler
     * @param orderNo
     * @param payId
     */
    public void PayThread(final Handler handler,final int flag,final String orderNo,final String orderFrom,final String payId,final String orderType,final String status,final String cardId){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getOrderPay(orderNo,orderFrom,payId,status,cardId);
                Log.d("TAG", getMetSendData);
                String path = Constant.PAY_PATH_ORDER + orderType;
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(path, getMetSendData);
                    SendMessage(handler, flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 打印 detail
     * @param handler
     * @param orderNo
     * @param payId
     */
    public void PrintThread(final Handler handler,final String orderNo,final String orderFrom,final String payId,final String orderType,final String status){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getOrderPay(orderNo,orderFrom,payId,status,"");
                Log.d("TAG", getMetSendData);
                String path = Constant.PAY_PATH_ORDER + orderType;
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(path, getMetSendData);
                    SendMessage(handler,Constant.DATA_PRINT,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 打印  storemanager 销售单据
     * @param handler
     * @param orderNo
     * @param payId
     */
    public void QueryDataThread(final Handler handler,final String orderNo,final String orderFrom,final String payId,final String status,final int flag){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String getMetSendData = MyUtils.getInstance(context).getOrderPay(orderNo,orderFrom,payId,status,"");
                Log.d("TAG", getMetSendData);
                try {
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.DATA_PRINT_PATH_ALL, getMetSendData);
                    SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 交班信息
     * @param handler
     */
    public void ConnectThread(final Handler handler,final int flag,final String status){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String getMetSendData = MyUtils.getInstance(context).getConnect(status);
                    Log.d("TAG","route->"+getMetSendData);
                    result = HttpUtils.getIntance(context).sendPostHttp(Constant.CONNECT_PATH,getMetSendData);
                   SendMessage(handler,flag,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发消息
     * @param handler handler
     * @param flag 消息标识
     *  @param res 结果
     */
    public void SendMessage(Handler handler,int flag,String res){
        Message message = new Message();
        message.what = flag;
        Bundle bundle = new Bundle();
        bundle.putString("result",res);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}