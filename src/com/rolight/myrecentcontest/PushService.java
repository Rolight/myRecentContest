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
	// ��ȡ��Ϣ�߳�
	private MessageThread messageThread = null;
	// ֪ͨ����Ϣ��ʾ
	private int messageNotificationID = 1000;
	private Notification messageNotification = null;
	private NotificationManager messageNotificationManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public int OnStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		System.out.println("�����Ѿ�����");
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		System.out.println("���ͷ���������");
		messageNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// �����߳�
		messageThread = new MessageThread();
		messageThread.isRunning = true;
		messageThread.start();
		super.onCreate();
	}
	
	@SuppressLint("NewApi")
	public class MessageThread extends Thread {
		public boolean isRunning = true;

		@SuppressLint("NewApi")
		public void run() {
			while (isRunning) {
				try {
					ArrayList<String> serverMessage = getServerMessage();
					Iterator<String> it = serverMessage.iterator();
					while (it.hasNext()) {
						String[] str = it.next().split("@");
						messageNotification = new Notification
								.Builder(PushService.this)
								.setContentTitle(str[0])
								.setContentText(str[1])
								.build();
						messageNotificationManager.notify(
								messageNotificationID, messageNotification);
						messageNotificationID++;
					}
					Thread.sleep(4 * 3600 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("��������");
				}
			}
		}
	}
	
	public ArrayList<String> getServerMessage() {
		ContestDataDownloader cdn = new ContestDataDownloader();
		cdn.downloadData();
		DataAnalysiser das = new DataAnalysiser(cdn.getData());
		ArrayList<HashMap<String, String>> text = das.getData();
		ArrayList<GregorianCalendar> timelst = das.getTimeList();
		GregorianCalendar nowtime = new GregorianCalendar();
		Iterator<HashMap<String, String>> it1 = text.iterator();
		Iterator<GregorianCalendar> it2 = timelst.iterator();
		ArrayList<String> ret = new ArrayList<String>();
		//ǰ3��6��9��Сʱ�����Сʱ������һ��. 
		while(it1.hasNext() && it2.hasNext()) {
			HashMap<String, String> item = it1.next();
			GregorianCalendar stime = it2.next();
			long d = nowtime.getTimeInMillis() -  stime.getTimeInMillis();
			//���ᳬ��36��Сʱ����
			if(d < 0 || d > 3600 * 24 * 1000) continue;
			if(d == 30 * 60 * 1000) {
				ret.add("���Сʱ֮�󽫻�����" + item.get("oj") + "�ϵı���@" + item.get("name"));
			}
			else if(d <= 3600 * 12 * 1000 || d <= 3600 * 4 * 1000) {
				ret.add("���ڽ�������" + item.get("oj") + "�ϵı���@" + item.get("time") + " ��" + item.get("start_time"));
			}
		}
		return ret;
	}
	
	public void onDestory() {
		messageThread.isRunning = false;
		super.onDestroy();
	}
}
