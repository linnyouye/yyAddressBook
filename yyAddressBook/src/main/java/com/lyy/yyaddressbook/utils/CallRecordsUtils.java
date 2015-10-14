package com.lyy.yyaddressbook.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.lyy.yyaddressbook.entity.CallRecord;

public class CallRecordsUtils {

	private static final String TAG = "lyy-CallRecordUtils";
	private static final boolean D = false;

	private static final int RECORD_COUNT = 100;

	public static List<CallRecord> getCallRecordList(Context context) {

		List<CallRecord> list = new ArrayList<CallRecord>();

		String[] queryColumns = null;
		String locationColumn = getLocationColumnName();

		if (D)
			Log.i(TAG, "locationColumn:" + locationColumn);

		if (locationColumn == null) {
			queryColumns = new String[] { CallLog.Calls._ID,
					CallLog.Calls.NUMBER, CallLog.Calls.DATE,
					CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME,
					CallLog.Calls.TYPE, };
		} else {
			queryColumns = new String[] { CallLog.Calls._ID,
					CallLog.Calls.NUMBER, CallLog.Calls.DATE,
					CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME,
					CallLog.Calls.TYPE, locationColumn };
		}

		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, queryColumns, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		if (cursor == null) {
			return list;
		}

		for (int i = 0; cursor.moveToNext() && i < RECORD_COUNT; i++) {
			CallRecord callRecord = new CallRecord();
			callRecord.id = cursor.getString(0);
			callRecord.number = cursor.getString(1);
			callRecord.date = cursor.getLong(2);
			callRecord.duration = cursor.getLong(3);
			callRecord.name = cursor.getString(4);
			callRecord.type = cursor.getInt(5);
			if (locationColumn != null) {
				callRecord.location = cursor.getString(6);
			} else {
				callRecord.location = "";
			}
			list.add(callRecord);
			if (D)
				Log.i(TAG, callRecord.toString());
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}

		return list;
	}

	public static String getLocationColumnName() {

		try {
			Class<?> clz = CallLog.Calls.class;
			Field field = clz.getDeclaredField("GEOCODED_LOCATION");
			field.setAccessible(true);
			String locationColumnName = (String) field.get(new CallLog.Calls());
			return locationColumnName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
