package com.sw.mobsale.online.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sw.mobsale.online.ui.LoginActivity;
import com.sw.mobsale.online.ui.MainActivity;
import com.sw.mobsale.online.ui.VerifyCodeActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * json解析
 */
public class ResultLog {
   public static ResultLog log;
    Context context;
    public static ResultLog getInstance(Context context) {
        if( log== null) {
            log = new ResultLog(context);
        }
        return log;
    }
    /**
     * 构造函数
     * @param context   上下文对象
     */
    private ResultLog(Context context) {
        this.context = context;
    }

    /**
     * 判断界面
     * @param context activity
     * @param userName 用户名
     * @param userPwd 密码
     * @param code 验证码
     */
    public void ConfirmLog(Activity context,String userName,String userPwd,String code){
        Log.d("TAG","login->"+"login");
        //判断帐号密码
        if (userName == null || userName.trim().equals("")) {
            Toast.makeText(context, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPwd == null || userPwd.trim().equals("")) {
            Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (userPwd.length()< 1 ){
            Toast.makeText(context, "密码不能少于6位", Toast.LENGTH_LONG).show();
            return;
        }
        if (code == null ){
            Toast.makeText(context, "验证码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
         /*if (!code.equalsIgnoreCase(CodeUtils.getInstance().code)){
              Toast.makeText(context, "验证码错误", Toast.LENGTH_LONG).show();
              return;
         }*/
    }
    /**
     * login
     * @param result json
     * @param context activity
     * @param userName 用户名
     * @param userPwd 密码
     * @param phoneCode 设备号
     */
    public void LoginLog(String result, Activity context,String userName,String userPwd,String phoneCode){
        try {
            JSONObject object = new JSONObject(result);
            String message = object.getString("errorMessage");
            if (message.equals("Login")){
                JSONArray array = object.getJSONArray("rows");
                JSONObject jo = array.getJSONObject(0);
                if (jo.get("usercode").equals("") || "null".equals(jo.get("usercode").toString())) {
                    return;
                }
                if (jo.get("terminalname").equals("") || "null".equals(jo.get("terminalname").toString())) {
                    return;
                }
                if (jo.get("terminalmobile").equals("") || "null".equals(jo.get("terminalmobile").toString())) {
                    return;
                }
                if (jo.get("sellerid").equals("") || "null".equals(jo.get("sellerid").toString())) {
                    return;
                }
                if (jo.get("sellercode").equals("") || "null".equals(jo.get("sellercode").toString())) {
                    return;
                }
                if (jo.get("sellername").equals("") || "null".equals(jo.get("sellername").toString())) {
                    return;
                }
                if (jo.get("appid").equals("") || "null".equals(jo.get("appid").toString())) {
                    return;
                }
                String terminalName = (String) jo.get("terminalname");
                String terminalMobile = (String) jo.get("terminalmobile");
                String classes = jo.getString("flightsno");
                //String sk = jo.getString("sk");
                //String appId = (String) jo.get("appid");
                SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("password",userPwd);
                editor.putString("phoneCode",phoneCode);
                editor.putString("userCode",userName);
                editor.putString("classes",classes);
                Log.d("TAG","classes->"+classes);
                //editor.putString("sk",sk);
                //editor.putString("terminalName",terminalName);
                //editor.putString("terminalMobile",terminalMobile);
                //editor.putString("appId",appId);
                editor.commit();
                context.finish();
                Toast.makeText(context, "验证通过，登录主页面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }else if (message.equals("Illegal")) {
                Toast.makeText(context, "用户名密码错误，请重新输入", Toast.LENGTH_SHORT).show();
            }else if (message.equals("Over")) {
                Toast.makeText(context, "终端数量已达到上限，无法完成此设备注册", Toast.LENGTH_SHORT).show();
            } else if (message.equals("NewTerminal")) {
                Toast.makeText(context, "验证通过，登录授权页面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, VerifyCodeActivity.class);
                intent.putExtra("userCode", userName);
                intent.putExtra("password", userPwd);
                intent.putExtra("phoneCode",phoneCode);
                context.startActivity(intent);
                context.finish();
            }else if(message.equals("OnlineErr")){
                Toast.makeText(context, "账户已在另一设备上登陆", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * down
     * @param result json
     * @param context activity
     */
    public void DownLog(String result,Activity context){
        try {
            JSONObject object = new JSONObject(result);
            String message = object.getString("errorMessage");
            if (message.equals("offLine")) {
                back(context);
            } else {
                Toast.makeText(context, "退出失败", Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出
     * @param context activity
     */
    public void back(Activity context){
        SharedPreferences spf = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.remove("userCode");
        editor.remove("password");
//        editor.remove("sk");
         editor.remove("classes");
        editor.clear();
        editor.commit();
        Toast.makeText(context, "成功退出", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        context.finish();
    }
    /**
     * errorMessage 为 ok 时，跳转界面
      * @param result json  context activity
     * @param
     */
    public void FinishLog(String result){
        try {
            JSONObject object = new JSONObject(result);
            String state = object.getString("errorMessage");
            if ("OK".equals(state)) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
               // context.finish();
            }else  if ("err".equals(state)){
                Toast.makeText(context,"请确定装车!",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * code
     * @param result json
     * @param context activity
     * @param userName 用户名
     * @param userPwd 密码
     * @param phoneCode 设备号
     */
    public void CodeLog(String result, Activity context,String userName,String userPwd,String phoneCode){
        try {
            JSONObject object = new JSONObject(result);
            String state = object.getString("errorMessage");
        if (state.equals("Login")) {
            JSONArray array = object.getJSONArray("rows");
            JSONObject jo = array.getJSONObject(0);
            String classes = jo.getString("flightsno");
            SharedPreferences spf = context.getSharedPreferences("user",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spf.edit();
            editor.putString("userCode", userName);
            editor.putString("passwrod",userPwd);
            editor.putString("phoneCode",phoneCode);
            editor.putString("classes",classes);
            editor.commit();
            Toast.makeText(context, "验证通过，登录默认页面", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            context.finish();
        } else if (state.equals("pinnumErr")) {
            Toast.makeText(context, "授权码错误,请重新输入", Toast.LENGTH_SHORT).show();

        } else if (state.equals("OnlineErr")) {
            Toast.makeText(context, "账户已在另一设备上登陆", Toast.LENGTH_SHORT).show();
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 车辆信息
     * @param result json
     * @return List 车辆信息
     */
    public List<Map<String, Object>> CarNo(String result){
        List<Map<String,Object>>carNo = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("carnum").equals("") || "null".equals(jo.get("carnum").toString())) {
                    continue;
                } else {
                    String carnum = (String) jo.get("carnum");
                    String carid = (String) jo.get("id");
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("carnum",carnum);
                    map.put("id",carid);
                    carNo.add(map);
                }
            }
            Log.d("TAG","car->"+carNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  carNo;
    }

    /**
     * 司机信息
     * @param result json
     * @return 司机信息
     */
    public List<Map<String, Object>> Worker(String result){
        List<Map<String,Object>>carNo = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String drivername = (String) jo.get("drivername");
                String id = (String) jo.get("id");
                String sellerid = (String) jo.get("sellerid");
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("drivername",drivername);
                map.put("id",id);
                map.put("sellerid",sellerid);
                carNo.add(map);
            }
            Log.d("TAG","car->"+carNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  carNo;
    }
    /**
     * 订单头 距离
     * @param result result
     * @return 订单头信息
     */
    public ArrayList<Map<String, Object>> RouteStore(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            //if (!getErrorPath(object)){}
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String storeId = jo.getString("buyercode");
                String store = jo.getString("buyername");
                String address = jo.getString("addr");//receiveaddress
                String phoneNum = jo.getString("mobileno");
                String unit = "m";
                double distance = 0.0;
//                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//                List<Address> addresses = geocoder.getFromLocationName(address, 5);
//                if (addresses.size() > 0) {
//                    double Latitude = addresses.get(0).getLatitude();
//                    double Longitude = addresses.get(0).getLongitude();
//                    Log.d("TAG", "lat->" + Latitude);
//                    Log.d("TAG", "lon->" + Longitude);
//                    SharedPreferences spf = context.getSharedPreferences("loc", Context.MODE_PRIVATE);
//                    String la = spf.getString("lat", "1");
//                    String lo = spf.getString("lon", "1");
//                    Log.d("TAG", "lo->" + lo);
//                    Log.d("TAG", "la->" + la);
//                    double lat = Double.valueOf(la);
//                    double lon = Double.valueOf(lo);
//                    LatLng llng = new LatLng(lat, lon);
//                    LatLng lng = new LatLng(Latitude, Longitude);
//                    distance = DistanceUtil.getDistance(llng, lng);
//                    String d = String.valueOf(distance);
//                    if (d.contains(".")) {
//                        int dis = d.indexOf(".");
//                        d = d.substring(0, dis);
//                        Log.d("TAG", "dis->" + d);
//                    }
//                    if (d.length() > 3) {
//                        Double dis = Double.parseDouble(d);
//                        distance = dis / 1000;
//                        unit = "km";
//                    }
//                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("customerName", store);
                map.put("address", address);
                map.put("distance", String.valueOf(distance));
                map.put("unit",unit);
                map.put("phone", phoneNum);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * 订单头 距离
     * @param result result
     * @return 订单头信息
     */
    public ArrayList<Map<String, Object>> OrderLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            String total = object.getString("total");
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String orderNo = (String) jo.get("orderno");
                String customerName = (String) jo.get("buyername");
                String date = (String) jo.get("orderdate");
                String address = (String) jo.get("receiveaddress");
                String amt = (String) jo.get("ordersumamt");
                String payType = jo.getString("iscashondelivery");
                String fromCode = jo.getString("datasource");
                String fromName = jo.getString("sourceremark");
                Double distance = 0.0;
                String unit = "m";
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(address,5);
                if (addresses.size()> 0){
                    double Latitude = addresses.get(0).getLatitude();
                    double Longitude = addresses.get(0).getLongitude();
                    Log.d("TAG","lat->"+Latitude);
                    Log.d("TAG","lon->"+Longitude);
                    SharedPreferences spf = context.getSharedPreferences("loc",Context.MODE_PRIVATE);
                    String la = spf.getString("lat","1");
                    String lo = spf.getString("lon","1");
                    Log.d("TAG","lo->"+lo);
                    Log.d("TAG","la->"+la);
                    double lat = Double.valueOf(la);
                    double lon = Double.valueOf(lo);
                    LatLng llng = new LatLng(lat,lon);
                    LatLng lng = new LatLng(Latitude,Longitude);
                    distance = DistanceUtil.getDistance(llng,lng);
                    String d =  String.valueOf(distance);
                    if (d.contains(".")){
                        int dis = d.indexOf(".");
                        d = d.substring(0,dis);
                        Log.d("TAG","dis->"+d);
                    }
                    if(d.length() > 3){
                        Double dis = Double.parseDouble(d);
                        distance = dis / 1000;
                        unit = "km";
                    }else{
                        distance = Double.parseDouble(d);
                    }
                }
                if (payType == null||("").equals(payType)){
                    payType ="";
                }else{
                    if (("N").equals(payType)){
                        payType = "已付款";
                    }else{
                        payType = "货到付款";
                    }
                }
                //String note = (String) jo.get("remark");
                String orderDate = date.substring(0, 10);
                String orderTime = date.substring(11, 19);
                int a = amt.indexOf(".") + 3;
                amt = amt.substring(0, a);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("name", customerName);
                map.put("fromCode",fromCode);
                map.put("fromName",fromName);
                map.put("address", address);
                map.put("date", orderDate);
                map.put("time", orderTime);
                map.put("note", "无损坏");
                map.put("number", orderNo);
                map.put("amt", amt);
                map.put("distance",distance.toString());//
                map.put("unit",unit);
                map.put("payType",payType);
                map.put("total",total);
                orderNum.add(map);
               //geoCoder.destroy();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * 返回输入地址的经纬度坐标
     * key lng(经度),lat(纬度)
     */
    public LatLng getGeocoderLatitude(String address){
        BufferedReader in = null;
        LatLng ll = null;
        String KEY_1 = "xwngXjOeiqjwwGZ62Rt4aKviAqoxmrGL";//7d9fbeb43e975cd1e9477a7e5d5e192a
        try {
            //将地址转换成utf-8的16进制
            address = URLEncoder.encode(address, "UTF-8");
            URL tirc = new URL("http://api.map.baidu.com/geocoder?address="+ address +"&output=json&key="+ KEY_1);
            in = new BufferedReader(new InputStreamReader(tirc.openStream(),"UTF-8"));
            String res;
            StringBuilder sb = new StringBuilder("");
            while((res = in.readLine())!=null){
                sb.append(res.trim());
            }
            String str = sb.toString();
            //StringUtils.isNotEmpty(str)
            if(!("").equals(str)){
                int lngStart = str.indexOf("lng\":");
                int lngEnd = str.indexOf(",\"lat");
                int latEnd = str.indexOf("},\"precise");
                if(lngStart > 0 && lngEnd > 0 && latEnd > 0){
                    String lng = str.substring(lngStart+5, lngEnd);
                    String lat = str.substring(lngEnd+7, latEnd);
                    Log.d("TAG","LL->"+lat+"ll->"+lng);
                    ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    return ll;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ll;
    }
    /**
     * 订单头 无距离
     * @param result result
     * @return 订单头信息
     */
    public ArrayList<Map<String, Object>> OrderNoLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            String total = object.getString("total");
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String orderId = jo.getString("headid");
                String orderNo = (String) jo.get("orderno");
                String customerName = (String) jo.get("buyername");
                String date = (String) jo.get("orderdate");
                String address = (String) jo.get("receiveaddress");
                String amt = (String) jo.get("ordersumamt");
                String payType = jo.getString("iscashondelivery");
                String fromCode = jo.getString("datasource");
                String fromName = jo.getString("sourceremark");
                String unit = "m";
                if (payType == null||("").equals(payType)){
                    payType ="";
                }else{
                    if (("N").equals(payType)){
                        payType = "已付款";
                    }else{
                        payType = "货到付款";
                    }
                }
                //String note = (String) jo.get("remark");
                String orderDate = date.substring(0, 10);
                String orderTime = date.substring(11, 19);
                int a = amt.indexOf(".") + 3;
                amt = amt.substring(0, a);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("name", customerName);
                map.put("fromCode",fromCode);
                map.put("fromName",fromName);
                map.put("address", address);
                map.put("date", orderDate);
                map.put("time", orderTime);
                map.put("note", payType);
                map.put("id",orderId);
                map.put("number", orderNo);
                map.put("amt", amt);
                map.put("distance","0");
                map.put("unit",unit);
                map.put("payType",payType);
                map.put("total",total);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * loading 订单头
     * @param result result
     * @return 订单头信息
     */
    public ArrayList<Map<String, Object>> LoadingLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String orderId = jo.getString("headid");
                String orderNo = (String) jo.get("orderno");
                String customerName = (String) jo.get("buyername");
                String date = (String) jo.get("orderdate");
                String address = (String) jo.get("receiveaddress");
                String amt = (String) jo.get("ordersumamt");
                String payType = jo.getString("iscashondelivery");
                String orderQty = jo.getString("orderqty");
                String detailQty = jo.getString("detailqty");
                String fromCode = jo.getString("datasource");
                String fromName = jo.getString("sourceremark");
                if (!("0").equals(detailQty)){
                detailQty = detailQty.substring(0,detailQty.indexOf("."));
                }
                if (payType == null||("").equals(payType)){
                    payType ="";
                }else{
                    if (("N").equals(payType)){
                        payType = "已付款";
                    }else{
                        payType = "货到付款";
                    }
                }
                //String note = (String) jo.get("remark");
                String orderDate = date.substring(0, 10);
                String orderTime = date.substring(11, 19);
                int a = amt.indexOf(".") + 3;
                amt = amt.substring(0, a);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("name", customerName);
                map.put("fromCode",fromCode);
                map.put("fromName",fromName);
                map.put("address", address);
                map.put("date", orderDate);
                map.put("time", orderTime);
                map.put("note", payType);
                map.put("id",orderId);
                map.put("number", orderNo);
                map.put("amt", amt);
//                map.put("distance","0");
//                map.put("unit","m");
                map.put("payType",payType);
                map.put("orderQty",orderQty);
                map.put("detailQty",detailQty);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }


    /**
     * 销售信息
     * @param result result
     * @return  销售信息 list
     */
    public ArrayList<Map<String, Object>> SaleAllLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            int k = 1;
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String orderNo = (String) jo.get("orderno");
                String customerName = (String) jo.get("buyername");
                String date = (String) jo.get("orderdate");
                String address = (String) jo.get("receiveaddress");
                String amt = (String) jo.get("ordersumamt");
                String payType = jo.getString("iscashondelivery");
                String saleType = jo.get("saletype").toString();
                String fromCode = jo.getString("datasource");
                String fromName = jo.getString("sourceremark");
                if (customerName == null ||("").equals(customerName)){
                    customerName = "零售客户";
                }
                if (address == null ||("").equals(address)){
                    address = "零售地址";
                }
                if (payType == null||("").equals(payType)){
                    payType ="";
                }else{
                    if (("N").equals(payType)){
                        payType = "已付款";
                    }else{
                        payType = "货到付款";
                    }
                }
                //String note = (String) jo.get("remark");
                String orderDate = date.substring(0, 10);
                String orderTime = date.substring(11, 19);
                int a = amt.indexOf(".") + 3;
                amt = amt.substring(0, a);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("name", customerName);
                map.put("fromCode",fromCode);
                map.put("fromName",fromName);
                map.put("address", address);
                map.put("date", orderDate);
                map.put("time", orderTime);
                map.put("note",payType);
                map.put("number", orderNo);
                map.put("amt", amt);
                map.put("distance","0");
                map.put("unit","m");
                map.put("payType",payType);
                map.put("saleType",saleType);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * 订单明细
     * @param result json
     * @return 订单明细 list
     */
    public List<Map<String,Object>> OrderDetailLog(String result){
        Log.d("TAG", "detail result->" + result);
        List<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("itemcode").equals("") || "null".equals(jo.get("itemcode").toString())) {
                    continue;
                }
                if (jo.get("itemname").equals("") || "null".equals(jo.get("itemname").toString())) {
                    continue;
                }
                if (jo.get("itemspec").equals("") || "null".equals(jo.get("itemspec").toString())) {
                    continue;
                }
                if (jo.get("price").equals("") || "null".equals(jo.get("price").toString())) {
                    continue;
                }
                if (jo.get("unitdesc").equals("") || "null".equals(jo.get("unitdesc").toString())) {
                    continue;
                }
                if (jo.get("itemurl").equals("") || "null".equals(jo.get("itemurl").toString())) {
                    continue;
                }
                String itemCode = (String) jo.get("itemcode");
                String itemName = (String) jo.get("itemname");
                String itemUrl = (String) jo.get("itemurl");
                String itemSpec = (String) jo.get("itemspec");
                String qty = (String) jo.get("qtysum");
                String itemPrice = (String) jo.get("price");
                String unitcode = (String) jo.get("unitcode");
                String unitName = (String) jo.get("unitdesc");
                String sumQty = (String) jo.get("sumqty");
                String sumAmt = (String) jo.get("sumamt");
                int a =qty.indexOf(".");
                qty = qty.substring(0,a);
                int b =itemPrice.indexOf(".") + 3;
                itemPrice = itemPrice.substring(0,b);
                if (sumQty == null ||("").equals(sumQty)){
                    sumQty ="";
                }else {
                    int c = sumQty.indexOf(".");
                    sumQty = sumQty.substring(0, c);
                }
                if (sumAmt == null ||("").equals(sumAmt)){
                    sumAmt ="";
                }else {
                    int d = sumAmt.indexOf(".") + 3;
                    sumAmt = sumAmt.substring(0, d);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg",Constant.image1[i % 5]);
                map.put("url",itemUrl);
                map.put("itemCode", itemCode);
                map.put("name", itemName);
                map.put("info", itemSpec);
                map.put("unitName", unitName);
                map.put("number", qty);
                map.put("price", itemPrice);
                map.put("sumQty", sumQty);
                map.put("sumAmt", sumAmt);
                dataLists.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  dataLists;
    }

    /**
     * 零售销售明细
     * @param result json
     * @return 零售销售明细 list
     */
    public List<Map<String,Object>> RetailDetailLog(String result){
        Log.d("TAG", "detail result->" + result);
        List<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("itemcode").equals("") || "null".equals(jo.get("itemcode").toString())) {
                    continue;
                }
                if (jo.get("itemname").equals("") || "null".equals(jo.get("itemname").toString())) {
                    continue;
                }
                if (jo.get("itemspec").equals("") || "null".equals(jo.get("itemspec").toString())) {
                    continue;
                }
                if (jo.get("price").equals("") || "null".equals(jo.get("price").toString())) {
                    continue;
                }
                if (jo.get("unitdesc").equals("") || "null".equals(jo.get("unitdesc").toString())) {
                    continue;
                }
                if (jo.get("itemurl").equals("") || "null".equals(jo.get("itemurl").toString())) {
                    continue;
                }
                String itemCode = (String) jo.get("itemcode");
                String id = (String) jo.get("itemretailid");
                String itemName = (String) jo.get("itemname");
                String itemUrl = (String) jo.get("itemurl");
                String itemSpec = (String) jo.get("itemspec");
                String qty = (String) jo.get("qtysum");
                String number = (String) jo.get("qty");
                String itemPrice = (String) jo.get("price");
                String unitcode = (String) jo.get("unitcode");
                String unitName = (String) jo.get("unitdesc");
                String sumQty = (String) jo.get("sumqty");
                String sumAmt = (String) jo.get("sumamt");
                int a =qty.indexOf(".");
                qty = qty.substring(0,a);
                int f =number.indexOf(".");
                number = number.substring(0,f);
                int b =itemPrice.indexOf(".") + 3;
                itemPrice = itemPrice.substring(0,b);
                if (sumQty == null ||("").equals(sumQty)){
                    sumQty ="";
                }else {
                    int c = sumQty.indexOf(".");
                    sumQty = sumQty.substring(0, c);
                }
                if (sumAmt == null ||("").equals(sumAmt)){
                    sumAmt ="";
                }else {
                    int d = sumAmt.indexOf(".") + 3;
                    sumAmt = sumAmt.substring(0, d);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg",Constant.image1[i % 5]);
                map.put("id",id);
                map.put("url",itemUrl);
                map.put("itemCode", itemCode);
                map.put("name", itemName);
                map.put("info", itemSpec);
                map.put("unitName", unitName);
                map.put("number", qty);
                map.put("saleNum", number);
                map.put("price", itemPrice);
                map.put("sumQty", sumQty);
                map.put("sumAmt", sumAmt);
                dataLists.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  dataLists;
    }

    /**
     * 路线
     * @param result json
     * @return 路线 list
     */
    public List<Map<String,Object>> getRoute(String result){
        List<Map<String,Object>> leftLists = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            int k = 1;
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("linename").equals("") || "null".equals(jo.get("linename").toString())) {
                    continue;
                }
                String customerName = (String) jo.get("linename");
                String customerCode =(String)  jo.get("linecode");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("route", customerName);
                map.put("routeCode",customerCode);
                leftLists.add(map);

            }
            Log.d("TAG","left->"+ leftLists.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return leftLists;
    }

    /**
     * 零售分类
     * @param result json
     * @return 零售分类 list
     */
    public List<Map<String,Object>> getItemType(String result){
        List<Map<String,Object>> leftLists = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String type = jo.getString("typeab");
                if ("P".equals(type)) {
                    String id = (String) jo.get("id");
                    String itemtypename = (String) jo.get("itemtypename");
                    String itemtypecode = (String) jo.get("itemtypecode");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", id);
                    map.put("typeCode", itemtypecode);
                    map.put("typeName", itemtypename);
                    leftLists.add(map);
                }
            }
            Log.d("TAG","left->"+ leftLists.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return leftLists;
    }

    /**
     * 零售分类
     * @param result json
     * @return 零售分类 list
     */
    public List<Map<String,Object>> getItemOwnType(String result){
        List<Map<String,Object>> leftLists = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String type = jo.getString("typeab");
                if ("O".equals(type)) {
                    String id = (String) jo.get("id");
                    String itemtypename = (String) jo.get("itemtypename");
                    String itemtypecode = (String) jo.get("itemtypecode");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", id);
                    map.put("typeCode", itemtypecode);
                    map.put("typeName", itemtypename);
                    leftLists.add(map);
                }
            }
            Log.d("TAG","left->"+ leftLists.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return leftLists;
    }

    /**
     * 零售 服务器/后台
     * @param result result
     * @return list  零售产品列表
     */
    public ArrayList<Map<String,Object>> getLoadSale(String result){
        ArrayList<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();
        Log.d("TAG","result->"+result);
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("itemcode").equals("") || "null".equals(jo.get("itemcode").toString())) {
                    continue;
                }  if (jo.get("itemname").equals("") || "null".equals(jo.get("itemname").toString())) {
                    continue;
                }  if (jo.get("itemspec").equals("") || "null".equals(jo.get("itemspec").toString())) {
                    continue;
                }  if (jo.get("price").equals("") || "null".equals(jo.get("price").toString())) {
                    continue;
                } if (jo.get("unitdesc").equals("") || "null".equals(jo.get("unitdesc").toString())) {
                    continue;
                } if (jo.get("itemurl").equals("") || "null".equals(jo.get("itemurl").toString())) {
                    continue;
                }
                String id = jo.getString("itemretailid");
                String itemCode = (String) jo.get("itemcode");
                String itemName = (String) jo.get("itemname");
                String itemSpec = (String) jo.get("itemspec");
                String unitCode = (String) jo.get("unitcode");
                String unitName = (String) jo.get("unitdesc");
                String itemPrice = (String) jo.get("price");
                String itemTypeCode = (String)jo.get("itemtypecode");
                String itemTypeName = (String)jo.get("itemtypename");
                String itemWeight = (String)jo.get("itemweight");
                String itemCount = jo.getString("itemcount");
                if (itemCount ==null || ("").equals(itemCount)){
                    itemCount ="0";
                }
                String  number = jo.getString("qty");
                String  sumQty = jo.getString("sumqty");
                String  sumAmt = jo.getString("sumamt");
                if (number == null ||("").equals(number)){
                    number = "";
                }else{
                    int n = number.indexOf(".");
                    number = number.substring(0,n);
                }
                int b =itemPrice.indexOf(".") + 3;
                itemPrice = itemPrice.substring(0,b);
                if (sumQty == null ||("").equals(sumQty)){
                    sumQty = "0";
                }else {
                    int qty = sumQty.indexOf(".");
                    sumQty = sumQty.substring(0, qty);
                }
                if (sumAmt == null ||("").equals(sumAmt)){
                    sumAmt = "0";
                }else {
                    int amt = sumAmt.indexOf(".") + 3;
                    sumAmt = sumAmt.substring(0, amt);
                }
                String url = jo.getString("itemurl");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id",id);
                map.put("bg", Constant.image1[i % 5]);
                map.put("url",url);
                map.put("itemCode", itemCode);
                map.put("name", itemName);
                map.put("info", itemSpec);
                map.put("unitCode", unitCode);
                map.put("unitName", unitName);
                map.put("price", itemPrice);
                map.put("itemWeight", itemWeight);
                map.put("itemTypeCode", itemTypeCode);
                map.put("itemTypeName", itemTypeName);
                map.put("itemCount",itemCount);
                map.put("number",number);
                map.put("sumQty",sumQty);
                map.put("sumAmt",sumAmt);
                dataLists.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataLists;
    }

    /**
     *  打印数据
     * @param result json
     * @return list 打印数据
     */
    public List<Map<String,Object>> getDataPrint(String result){
        List<Map<String,Object>> dataLists = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (jo.get("itemcode").equals("") || "null".equals(jo.get("itemcode").toString())) {
                    continue;
                }
                if (jo.get("itemname").equals("") || "null".equals(jo.get("itemname").toString())) {
                    continue;
                }
                if (jo.get("itemspec").equals("") || "null".equals(jo.get("itemspec").toString())) {
                    continue;
                }
                if (jo.get("qty").equals("") || "null".equals(jo.get("qty").toString())) {
                    continue;
                }
                if (jo.get("price").equals("") || "null".equals(jo.get("price").toString())) {
                    continue;
                }
                if (jo.get("unitdesc").equals("") || "null".equals(jo.get("unitdesc").toString())) {
                    continue;
                }
                if (jo.get("qtysum").equals("") || "null".equals(jo.get("qtysum").toString())) {
                    continue;
                }
                if (jo.get("itemurl").equals("") || "null".equals(jo.get("itemurl").toString())) {
                    continue;
                }
                String itemCode = (String) jo.get("itemcode");
                String itemName = (String) jo.get("itemname");
                String itemSpec = (String) jo.get("itemspec");
                String unitName = (String) jo.get("unitdesc");
                String qty = (String) jo.get("qty");
                String itemPrice = (String) jo.get("price");
                int a = qty.indexOf(".");
                qty = qty.substring(0, a);
                int b = itemPrice.indexOf(".") + 3;
                itemPrice = itemPrice.substring(0, b);
                double itemamt = Double.parseDouble(qty ) * Double.parseDouble(itemPrice);
                String itemAmt = String.valueOf(itemamt);
                String url = jo.getString("itemurl");
                String qtySum = jo.getString("qtysum");
                int c = qtySum.indexOf(".");
                qtySum = qtySum.substring(0, c);
                String amt = jo.getString("ordersumamt");
                int d = amt.indexOf(".") + 3;
                amt = amt.substring(0, d);
                String name = jo.getString("buyername");
                String address = jo.getString("receiveaddress");
                String phone = jo.getString("mobileno");
                String date = jo.getString("orderdate");
                String pay = jo.getString("iscashondelivery");
                String model = "";
                if (("N").equals(pay)) {
                    pay = "已付款";
                    model = "线上支付";
                } else {
                    pay = "货到付款";
                     model = jo.getString("paymentname");
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bg", Constant.image1[i % 5]);
                map.put("url", url);
                map.put("itemCode", itemCode);
                map.put("name", itemName);
                map.put("info", itemSpec);
                map.put("unitName", unitName);
                map.put("number", qty);
                map.put("qtySum", qtySum);
                map.put("amt", amt);
                map.put("store", name);
                map.put("price", itemPrice);
                map.put("itemAmt",itemAmt);
                map.put("address", address);
                map.put("phone", phone);
                map.put("pay", pay);
                map.put("model", model);
                map.put("date",date);
                dataLists.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  dataLists;
    }

    /**
     * 交班信息
     * @param result result
     * @return
     */
    public ArrayList<Map<String, Object>> ConnLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String sellercode = (String) jo.get("sellercode");
                String sellername = (String) jo.get("sellername");
                String usercode = (String) jo.get("usercode");
                String terminalname = (String) jo.get("terminalname");
                String terminalmobile = (String) jo.get("terminalmobile");
                String drivername = (String) jo.get("drivername");
                String saleamt = jo.getString("saleamt");
                String ordernum = jo.getString("ordernum");
                String carnum = jo.getString("carnum");
                String orderallnum = jo.getString("orderallnum");
                String retailnum = jo.getString("retailnum");
                String retailitemnum = jo.getString("retailitemnum");
                String allitemnum = jo.getString("allitemnum");
                String flightsseq = jo.getString("flightsseq");
                if (("0").equals(allitemnum)){
                    allitemnum = "0";
                }else{
                    int  a = allitemnum.indexOf(".");
                    allitemnum = allitemnum.substring(0, a);
                }
                if (("0").equals(retailitemnum)){
                    retailitemnum = "0";
                }else {
                    int b = retailitemnum.indexOf(".");
                    retailitemnum = retailitemnum.substring(0, b);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sellername", sellername);
                map.put("terminalname", terminalname);
                map.put("terminalmobile", terminalmobile);
                map.put("drivername",drivername);
                map.put("saleamt", saleamt);
                map.put("ordernum",ordernum);
                map.put("carnum",carnum);
                map.put("orderallnum", orderallnum);
                map.put("retailnum", retailnum);
                map.put("retailitemnum",retailitemnum);
                map.put("allitemnum",allitemnum);
                map.put("flightsseq",flightsseq);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * 支付方式详情
     * @param result result
     * @return list 支付方式详情
     */
    public ArrayList<Map<String, Object>> AddressLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String id = (String) jo.get("id");
                String areaattribute = (String) jo.get("areaattribute");
                String areacode = (String) jo.get("areacode");
                String areadesc = (String) jo.get("areadesc");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id",id);
                map.put("bute",areaattribute);
                map.put("code",areacode);
                map.put("name",areadesc);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }
    /**
     * 支付方式详情
     * @param result result
     * @return list 支付方式详情
     */
    public ArrayList<Map<String, Object>> ModelLog(String result){
        ArrayList<Map<String,Object>>orderNum = new ArrayList<Map<String,Object>>();
        try {
            JSONObject object = new JSONObject(result);
          //  if (!getErrorPath(object)){}
            JSONArray array = object.getJSONArray("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                String paymentid = (String) jo.get("paymentid");
                String paymentcode = (String) jo.get("paymentcode");
                String paymentname = (String) jo.get("paymentname");
                String saleamt = jo.getString("saleamt");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("model",paymentname);
                map.put("money",saleamt);
                orderNum.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  orderNum;
    }

    /**
     * 错误路径
     * @param object object json
     */
    public boolean getErrorPath( JSONObject object){
        try {
            String error = object.getString("errorMessage");
            Log.d("TAG",error);
            if ("offLine".equals(error)){
                Toast.makeText(context,"非法路径!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 服务器无响应
     * @param result 服务器返回数据
     */
    public boolean getNoRes(String result){
        if (("").equals(result)) {
            Toast.makeText(context,"服务器解析失败，请稍后重试!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 强制下线
     */
    public void getDialog(final Activity activity){
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("警告!")
//                .setMessage("您的账户已在另一台设备登录,请确定是本人操作!")
//                .setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences spf = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = spf.edit();
//                        editor.remove("userCode");
//                        editor.remove("password");
//                        editor.clear();
//                        editor.commit();
//                        Intent intent = new Intent(context,LoginActivity.class);
//                        activity.startActivity(intent);
//                        activity.finish();
//                    }
//                })
//                .setCancelable(false)
//                .create().show();
        MyDialog confirmDialog = new MyDialog(activity, "警告","您的账户已在另一台设备登录,请确定是本人操作!?", "退出登录","确定",false,false);
        confirmDialog.setCancelable(false);
        confirmDialog.show();
        confirmDialog.setClicklistener(new MyDialog.ClickListenerInterface() {
            @Override
            public void doConfirm(String result) {

            }

            @Override
            public void doConfirm() {
                back(activity);
            }
            @Override
            public void doCancel() {
                back(activity);
            }
        });
    }
}
