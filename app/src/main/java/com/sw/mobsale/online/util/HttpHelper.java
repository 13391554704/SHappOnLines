package com.sw.mobsale.online.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Sxs
 */
public class HttpHelper {
    public HttpHelper() {
    }

    public static String generateSnForSk(int appid, String sk) throws Exception {
        return generateSnForParams(appid, sk, new HashMap());
    }

    public static String generateSnForParams(int appid, String sk, Map<String, Object> params) throws Exception {
        ArrayList keys = new ArrayList();
        Iterator text = params.keySet().iterator();

        while(text.hasNext()) {
            String builder = (String)text.next();
            keys.add(builder);
        }

        Collections.sort(keys);
        StringBuilder var9 = new StringBuilder();
        Iterator md = keys.iterator();

        String var10;
        while(md.hasNext()) {
            var10 = (String)md.next();
            //String array = (String)params.get(var10);
            String array = String.valueOf(params.get(var10));
            array = URLEncoder.encode(array, "utf-8");
            var9.append(var10);
            var9.append("=");
            var9.append(array);
            var9.append("&");
        }

        if(var9.length() > 0) {
            var9.deleteCharAt(var9.length() - 1);
        }

        var10 = var9.toString();
        var10 = var10 + sk;
        var10 = URLEncoder.encode(var10, "utf-8");
        MessageDigest var11 = MessageDigest.getInstance("MD5");
        byte[] var12 = var11.digest(var10.getBytes());
        var9 = new StringBuilder();

        for(int i = 0; i < var12.length; ++i) {
            var9.append(Integer.toHexString(var12[i] & 255 | 256).substring(1, 3));
        }

        return var9.toString();
    }
}
