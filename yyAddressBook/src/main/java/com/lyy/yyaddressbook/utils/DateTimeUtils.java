package com.lyy.yyaddressbook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

	private static final int MILLISECOND_OF_DAY = 24 * 60 * 60 * 1000;

	public static String getDateFromMillisecond(long millisecond) {
		long dToday = System.currentTimeMillis() / MILLISECOND_OF_DAY;
		long dTargetDay = millisecond / MILLISECOND_OF_DAY;
		String date = "";
		String time = new SimpleDateFormat("H:mm")
				.format(new Date(millisecond));

		switch ((int) (dToday - dTargetDay)) {
		case 0:
			date = "今天";
			break;
		case 1:
			date = "昨天";
			break;
		case 2:
			date = "前天";
			break;
		default:
			date = new SimpleDateFormat("M-d").format(new Date(millisecond));

		}

		return date + "  " + time;
	}

}
