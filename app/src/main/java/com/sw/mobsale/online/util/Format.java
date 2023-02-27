package com.sw.mobsale.online.util;


import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 工具类
 */
public class Format {

	private static SimpleDateFormat sd;

	private static SimpleDateFormat st = new SimpleDateFormat("HH:mm:ss");

	private static SimpleDateFormat sdt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private static String quantity = "#";
	/**
	* 格式化数字
	 * @param d  d
	* @param len 指定小数保留位数
	* @return
			*/
	public static String getNumber(double d, int len){
		// 处理double的精确问题
		BigInteger a1 = BigInteger.valueOf(10).pow(len);
		double a2 = d * a1.doubleValue();
		double e = (Math.round(a2)) / a1.doubleValue();
		// 格式化
		String formatStr = "#0";
		if (len > 0) {
			formatStr += ".";
		}
		for (int i = 0; i < len; i++) {
			formatStr += "0";
		}
		return new DecimalFormat(formatStr).format(e);
	}
}