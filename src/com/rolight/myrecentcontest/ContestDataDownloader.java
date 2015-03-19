package com.rolight.myrecentcontest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ContestDataDownloader {
	URL url;
	String data;

	public ContestDataDownloader() {
		try {
			url = new URL("http://contests.acmicpc.info/contests.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public ContestDataDownloader(String myurl) {

		try {
			url = new URL(myurl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void downloadData() {
		try {
			String str = "";
			Scanner sin = new Scanner(url.openStream());
			while (sin.hasNext())
				str += sin.next();
			sin.close();
			data = str;
		} catch (IOException e) {
			data = "Error!";
			e.printStackTrace();
		}
	}
	
	public String getData() {
		return data;
	}
}
