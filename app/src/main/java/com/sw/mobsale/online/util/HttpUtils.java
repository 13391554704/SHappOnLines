package com.sw.mobsale.online.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.sw.mobsale.online.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


/**
 * Http
 */
public class HttpUtils {
    public static  HttpUtils httpUtils;
    Context context;
    public HttpUtils(Context context) {
        this.context = context;
    }

    public static  HttpUtils getIntance(Context context){
        if (httpUtils == null){
            httpUtils = new HttpUtils(context);
        }
        return  httpUtils;
    }


    /**
     * 连接服务器
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String sendPostHttp(String path,Map<String,String>data) throws Exception{
        String result;
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setRequestProperty("Content-type", "application/x-java-serialized-object");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.connect();
        ObjectOutputStream outp = new ObjectOutputStream(con.getOutputStream());
        outp.writeObject(data);
        outp.flush();
        outp.close();
        int code = con.getResponseCode();
        Log.d("TAG",code+"");
        InputStream in = con.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        result = bufferRes.toString();
        in.close();
        return result;
    }

    /**
     * 连接服务器
     * @param path
     * @param data
     * @return  
     * @throws Exception  throws Exception
     */
    public String sendPostHttp(String path,String data)  {
        String result = "";
        try {
            URL url = new URL(path);
            Log.d("TAG",url+"");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(3000);
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded ");//默认
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.connect();
            String sk = "sk82cf128c81b84e7980e878613789f3bd";
            int appid = 1;
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("formdata", data);
            map.put("appid", appid);
            String snForParams = HttpHelper.generateSnForParams(appid, sk, map);
            //Log.d("TAG","appid="+appid+"&formdata="+java.net.URLEncoder.encode(data, "utf-8")+"&snForParams="+snForParams);
            con.getOutputStream().write(("appid="+appid+"&formdata="+java.net.URLEncoder.encode(data, "utf-8")+"&snForParams="+snForParams).getBytes("utf-8"));
            InputStream in = con.getInputStream();
            String conEncoding = con.getContentEncoding();
         //   long conLength = con.getContentLengthLong();
            int code = con.getResponseCode();
            Log.d("TAG",code+"");
            InputStreamReader isr = null;
            if ("gzip".equals(conEncoding)) {
                isr = new InputStreamReader(new GZIPInputStream(in), "utf-8");
            } else {
                isr = new InputStreamReader(in, "utf-8");
            }
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            result = sb.toString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("err",e.toString());
        }
        return result;
    }

    /**
     * 连接服务器
     * @param path
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> LinePostHttp(String path,Map<String,String>tt) throws Exception{
        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(3000);
        con.setRequestProperty("Content-type", "application/x-java-serialized-object");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.connect();
        ObjectOutputStream outp = new ObjectOutputStream(con.getOutputStream());
        outp.writeObject(tt);
        outp.flush();
        outp.close();
        InputStream in = con.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        String result = bufferRes.toString();
        in.close();
        JSONArray array = new JSONArray(result);
        int k = 1;
        for (int i = 0; i < array.length(); i++) {
            String customerName ="";
            JSONObject jo = array.getJSONObject(i);
            Log.d("TAG",jo.toString());
           if(jo.get("HONORIFICTITLE").equals("") || "null".equals(jo.get("HONORIFICTITLE").toString())){
                continue;
           }else{
               customerName=(String) jo.get("HONORIFICTITLE");
               Map<String ,Object>map = new HashMap<String,Object>();
               map.put("route",customerName);
               data.add(map);
           }
        }
        return data;
    }
}
