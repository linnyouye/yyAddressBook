package com.lyy.yyaddressbook.utils;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitmapUtils {

	private static final String TAG = "lyy-BitmapUtils";
	private static final boolean D = true;

	public static Bitmap decodeFile(String filePath, int maxByteCount) {

		int sampleSize = (int) Math.sqrt(getBitmapSize(filePath) * 1.0
				/ maxByteCount);

		Options opts = new Options();
		opts.inSampleSize = sampleSize;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(filePath, opts);
	}

	public static int getBitmapSize(String filePath) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		return opts.outWidth * opts.outHeight;
	}

	public static void releaseBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	public static boolean saveBitmap(Bitmap bitmap, String filePath) {
		try {
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
					filePath));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
