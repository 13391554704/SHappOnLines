package com.sw.mobsale.online.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据
 */
public class MyUtils {
    public static MyUtils myUtils;
    Context context;
    Gson gson;
    public String userCode,userName,phoneNum,phoneCode,time,name,password,appId,sk,classes;
    public static MyUtils getInstance(Context context) {
        if( myUtils== null) {
            myUtils = new MyUtils(context);
        }
        return myUtils;
    }
    /**
     * 构造函数
     * @param context   上下文对象
     */
    private MyUtils(Context context) {
       this.context = context;
        gson = new Gson();
    }

    /**
     * login
     * @param userCode 用户名
     * @param userPwd 密码
     * @param phoneCode 设备号
     * @param code 验证码
     * @return result 登录
     */
    public String getLoginPostData(String userCode,String userPwd,String phoneCode,String code){
        String result = "";
        try {
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("password", userPwd);
            map.put("terminalid", phoneCode);
            map.put("pinnumber",code);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //        result = gson.toJson(listData);
        return result;
    }
    /**
     * mainFragment
     * @param oldcarnumid 绑定前车号
     * @param newcarnumid 绑定后车号
     * @param oldcardriverid 装车前司机
     * @param newcardriverid 装车完成后司机
     * @return result   登录信息
     */
    public String getInfoPostData(String oldcarnumid,String newcarnumid,String oldcardriverid,String newcardriverid){
        String result = "";
        try {
            getSpfXml();
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("flightsno",classes);
            map.put("password", password);
            map.put("terminalid", phoneCode);
            map.put("oldcarnumid", oldcarnumid);
            map.put("newcarnumid", newcarnumid);
            map.put("oldcardriverid", oldcardriverid);
            map.put("newcardriverid", newcardriverid);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //        result = gson.toJson(listData);
        return result;
    }


        /**
         * 无参数连接后台提交数据
         * @return result
         */
    public String getSalePostData(){
        String result = "";
        try {
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     * 地址  用于选择省市区
     * @param status 传递status
     * @param parentareacode 父级code
     * @param areaattribute  名称
     * @param parentareaid 父级id
     * @return result
     */
    public String getAddresstData(String status,String parentareacode,String areaattribute,String parentareaid){
        String result = "";
        try {
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("status",status);
            map.put("parentareacode",parentareacode);
            map.put("areaattribute",areaattribute);
            map.put("parentareaid", parentareaid);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     * 零售装车，，零售销售
     * @param id 商品标识id
     * @param qty 商品数量
     * @param status status
     * @param transNo 单号
     * @param buyercode 买家编号
     * @param buyername 买家名称
     * @param receiveaddress 买家地址
     * @param provinceareaid 省id
     * @param cityareaid 市id
     * @param countyareaid 县区id
     * @param mobileno 买家联系方式
     * @return result
     */
    public String getShopCarPostData(String id,String qty,String status,String transNo,String orderId,String buyercode,String buyername,String receiveaddress,String provinceareaid,String cityareaid,String countyareaid,String mobileno){
        String result = "";
        try {
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("itemretailid",id);
            map.put("qty",qty);
            map.put("status",status);
            map.put("transno",transNo);
            map.put("headid",orderId);
            map.put("buyercode",buyercode);
            map.put("buyername",buyername);
            map.put("receiveaddress",receiveaddress);
            map.put("provinceareaid",provinceareaid);
            map.put("cityareaid",cityareaid);
            map.put("countyareaid",countyareaid);
            map.put("mobileno", mobileno);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     *
      * @param id 商品标识id
     * @param qty 商品数量
     * @param status status
     * @param transNo 单号
     * @param orderId 单号id
     * @return
     */
    public String getShopCarPostData(String id,String qty,String status,String transNo,String orderId){
        String result = "";
        try {
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("itemretailid",id);
            map.put("qty",qty);
            map.put("status",status);
            map.put("transno",transNo);
            map.put("headid",orderId);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;

    }

    /**
     * 订单 N -网络去  O--到本地  A--头信息 D--明细 Y-- 出发销售   K--删除   空  现在查
     * @param route 路线信息
     * @param status status
     * @param orderNo 单号
     * @param orderId 单号标识id
     * @param datasource 来源
     * @return result
     */
    public String getOrderPostData(String route,String status,String orderNo,String orderId,String datasource){
        String result = "";
        try{
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("linecode",route);
            map.put("status",status);
            map.put("orderno",orderNo);
            map.put("orderid",orderId);
            map.put("datasource",datasource);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     * 支付打印
     * @param orderNo 单号
     * @param datasource 单号来源
     * @param paymentId 支付code
     * @param status status
     * @return result
     */
    public String getOrderPay(String orderNo,String datasource,String paymentId,String status,String cardId){
        String result = "";
        try{
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("orderno",orderNo);
            map.put("datasource",datasource);
            map.put("paymentid",paymentId);
            map.put("status",status);
            map.put("cardid",cardId);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }


    /**
     *  零售产品信息  S-> status- a   retail
     *               A->  status -a status -d status -l   loadsale
     * @param itemTypeId 平台产品id标识
     * @param itemTypeOwnId 自营产品id标识
     * @param status status
     * @return result
     */
    public String getSaleTypePostData(String itemTypeId,String itemTypeOwnId,String status){
        String result = "";
        try{
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("itemtypeid",itemTypeId);
            map.put("itemtypeidown",itemTypeOwnId);
            map.put("status",status);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     * 交班
     * @param status status
     * @return result
     */
    public String getConnect(String status){
        String result = "";
        try{
            List<Map<String,String>> listData = new ArrayList<Map<String,String>>();
            Map<String,String> map = new HashMap<String,String>();
            getSpfXml();
            map.put("usercode",userCode);
//            map.put("appid","1");
            map.put("terminalid", phoneCode);
            map.put("flightsno",classes);
            map.put("status",status);
            listData.add(map);
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //result = gson.toJson(listData);
        return result;
    }

    /**
     * SharedPreferences
     */
    public void getSpfXml(){
        SharedPreferences spf = context.getSharedPreferences("user",Context.MODE_PRIVATE);
       // name = spf.getString("name","");
        password = spf.getString("password","");
        userCode =spf.getString("userCode","");
       // userName = spf.getString("terminalName", "");
        //phoneNum = spf.getString("terminalMobile","");
        phoneCode = spf.getString("phoneCode","");
        classes = spf.getString("classes","");
//        appId = spf.getString("appId","");
//        sk = spf.getString("sk","");
    }
}
