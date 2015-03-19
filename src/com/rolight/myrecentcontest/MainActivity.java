package com.rolight.myrecentcontest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;

public class MainActivity extends ActionBarActivity {

	private void showList(String rawdata) {
		System.out.println("信息为：" + rawdata);
		if (rawdata == null || rawdata.equals("Error!")) {
			Toast.makeText(getApplicationContext(), "获取信息出错",
					Toast.LENGTH_SHORT).show();
			return;
		}
		DataAnalysiser das = new DataAnalysiser(rawdata);
		ArrayList<HashMap<String, String>> listdata = das.getData();
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listdata,
				R.layout.simple_item, new String[] { "name", "link", "oj",
						"week", "start_time" }, new int[] { R.id.contest_name,
						R.id.link, R.id.oj, R.id.week, R.id.starttime });
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(simpleAdapter);
	}

	private void getData(ContestDataDownloader cdn) {
		cdn.downloadData();
	}

	AlarmManager aManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button button = (Button) findViewById(R.id.button1);
		final ContestDataDownloader cdn = new ContestDataDownloader(
				"http://contests.acmicpc.info/contests.json");
		new Thread() {
			public void run() {
				getData(cdn);
			}
		}.start();

		aManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.this, PushService.class);
		final PendingIntent pi = PendingIntent.getService(MainActivity.this, 0,
				intent, 0);
		aManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 5000, pi);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showList(cdn.data);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
