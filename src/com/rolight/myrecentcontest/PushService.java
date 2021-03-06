package com.rolight.myrecentcontest;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class PushService extends Service {
	// 获取消息线程
	private MessageThread messageThread = null;
	// 通知栏消息显示
	private int messageNotificationID = 1000;
	private Notification messageNotification = null;
	private NotificationManager messageNotificationManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		messageNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		messageThread = new MessageThread();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		messageThread.start();
		ContestDataDownloader cdn = new ContestDataDownloader(
				"http://contests.acmicpc.info/contests.json");
		cdn.downloadData();
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressLint("NewApi")
	public class MessageThread extends Thread {
		public boolean isRunning = true;

		@SuppressLint("NewApi")
		public void run() {
			ArrayList<String> serverMessage = getServerMessage();
			System.out.println("小别二");
			if (serverMessage == null)
				return;
			System.out.println("小瘪三");
			Iterator<String> it = serverMessage.iterator();
			System.out.println("小憋死" + serverMessage.toString() + "小"
					+ serverMessage.size());
			if(serverMessage.size() == 0) return;
			while (it.hasNext()) {
				String tmp = it.next();
				if (tmp == null) {
					System.out.println("我靠这怎么没有");
					return;
				}
				String[] str = tmp.split("@");
				messageNotification = new Notification.Builder(PushService.this)
						.setContentTitle(str[0]).setContentText(str[1]).build();
				messageNotificationManager.notify(messageNotificationID,
						messageNotification);
				messageNotificationID++;
			}
		}
	}

	public ArrayList<String> getServerMessage() {
		
		String rawstr = cdn.getData();

		if (rawstr == null || "Error!".equals(rawstr)) {
			return null;
		}
		DataAnalysiser das = new DataAnalysiser(rawstr);

		ArrayList<HashMap<String, String>> text = das.getData();
		ArrayList<GregorianCalendar> timelst = das.getTimeList();
		GregorianCalendar nowtime = new GregorianCalendar();
		Iterator<HashMap<String, String>> it1 = text.iterator();
		Iterator<GregorianCalendar> it2 = timelst.iterator();
		ArrayList<String> ret = new ArrayList<String>();
		// 前3个6个9个小时，半个小时各提醒一次.
		while (it1.hasNext() && it2.hasNext()) {
			HashMap<String, String> item = it1.next();
			GregorianCalendar stime = it2.next();
			long d = nowtime.getTimeInMillis() - stime.getTimeInMillis();
			// 不会超过36个小时提醒
			if (d < 0 || d > 3600 * 24 * 1000)
				continue;
			if (d == 30 * 60 * 1000) {
				ret.add("半个小时之后将会有在" + item.get("oj") + "上的比赛@"
						+ item.get("name"));
			} else if (d <= 3600 * 12 * 1000 || d <= 3600 * 4 * 1000) {
				ret.add("近期将会有在" + item.get("oj") + "上的比赛@" + item.get("time")
						+ " 于" + item.get("start_time"));
			}
		}
		return ret;
	}

	public void onDestory() {
		messageThread.isRunning = false;
		super.onDestroy();
	}
}
