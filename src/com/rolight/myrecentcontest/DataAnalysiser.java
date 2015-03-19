package com.rolight.myrecentcontest;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONObject;

public class DataAnalysiser {
	private String RawData;
	public ArrayList<HashMap<String, String>> Data;
	public ArrayList<GregorianCalendar> Stime;

	public DataAnalysiser(String str) {
		RawData = str;
		Analysis();
	}

	private GregorianCalendar parseStr(String str) {
		int y = (str.charAt(0) - '0') * 1000 + (str.charAt(1) - '0') * 100
				+ (str.charAt(2) - '0') * 10 + str.charAt(3) - '0';
		int m = (str.charAt(5) - '0') * 10 + (str.charAt(6) - '0');
		int d = (str.charAt(8) - '0') * 10 + str.charAt(9) - '0';
		int h = (str.charAt(10) - '0') * 10 + str.charAt(11) - '0';
		int M = (str.charAt(13) - '0') * 10 + str.charAt(14) - '0';
		int s = (str.charAt(16) - '0') * 10 + str.charAt(17) - '0';
		return new GregorianCalendar(y, m, d, h, M, s);
	}

	@SuppressLint("SimpleDateFormat")
	private void Analysis() {
		Data = new ArrayList<HashMap<String, String>>();
		Stime = new ArrayList<GregorianCalendar>();
		RawData = RawData.substring(1, RawData.length() - 1) + ",";
		String[] ListStr = RawData.split("\\}\\,");
		for (int i = 0; i < ListStr.length; i++) {
			String nowstr = ListStr[i] + "}";
			JSONObject obj = new JSONObject(nowstr);
			Iterator<?> it = obj.keys();
			HashMap<String, String> nowitem = new HashMap<String, String>();
			String pat = "yyyy年MM月dd日 hh时mm分ss秒";
			SimpleDateFormat sdf = new SimpleDateFormat(pat);
			while (it.hasNext()) {
				String nowkey = (String) it.next();
				String nowval = obj.getString(nowkey);
				if (nowkey.equals("start_time")) {
					GregorianCalendar date = parseStr(nowval);
					nowval = sdf.format(date.getTime());
					Stime.add(date);
				}

				nowitem.put(nowkey, nowval);
			}
			Data.add(nowitem);
		}
	}

	public ArrayList<HashMap<String, String>> getData() {
		return Data;
	}
	
	public ArrayList<GregorianCalendar> getTimeList() {
		return Stime;
	}
}
