package com.lyy.yyaddressbook.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

	private static final String CHARSET = "gb2312";

	private static final int CONN_TIMEOUT = 4000;
	private static final int READ_TIMEOUT = 4000;

	public static String httpGet(String urlStr) {

		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String result = null;
		try {
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setConnectTimeout(CONN_TIMEOUT);
			urlConn.setReadTimeout(READ_TIMEOUT);
			urlConn.connect();

			InputStream is = urlConn.getInputStream();

			result = getStringFromInputStream(is, CHARSET);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getStringFromInputStream(InputStream is,
			String charSet) {
		if (is == null) {
			return null;
		}
		StringBuffer sbResult = new StringBuffer();
		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, charSet));
			while ((line = br.readLine()) != null) {
				sbResult.append(line);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return sbResult.toString();
	}
}
