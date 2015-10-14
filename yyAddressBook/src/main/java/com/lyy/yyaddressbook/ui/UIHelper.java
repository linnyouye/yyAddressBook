package com.lyy.yyaddressbook.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lyy.yyaddressbook.R;

public class UIHelper {

	public static void call(Context context, String phone) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tmp = tm.getSimSerialNumber();
		if (tmp == null || tmp == "") {
			Toast.makeText(context, "您的设备没有SIM卡，无法打电话", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		String phoneNumber = "tel:" + phone;
		Uri uri = Uri.parse(phoneNumber);
		Intent callIntent = new Intent();
		callIntent.setAction(Intent.ACTION_CALL);
		callIntent.setData(uri);
		context.startActivity(callIntent);
	}

	public static void sendSms(Context context, String phone) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent.putExtra("address", phone);
		sendIntent.putExtra("sms_body", "");
		sendIntent.setType("vnd.android-dir/mms-sms");
		try {
			context.startActivity(sendIntent);
		} catch (Exception e) {
			Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String msg) {
		// Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
		TextView tvMsg = new TextView(context);
		tvMsg.setBackgroundResource(R.drawable.toast_bg);
		tvMsg.setTextSize(18);
		tvMsg.setTextColor(Color.WHITE);
		tvMsg.setPadding(30, 15, 30, 15);
		tvMsg.setText(msg);
		toast.setView(tvMsg);
		toast.show();

	}

	public static void showToast(Context context, int stringId) {
		// Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		showToast(context, context.getResources().getString(stringId));

	}

	/**
	 * 让输入框自动获取焦点，弹出输入法
	 * 
	 * @param input
	 */
	public static void setEditTextFocused(final EditText input) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				InputMethodManager inputManager = (InputMethodManager) input
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, 0);
			}

		}, 200);
	}

	/**
	 * 设置对话框的大小
	 */
	@SuppressWarnings("deprecation")
	public static void setDialogSize(Context context, Dialog dialog) {
		WindowManager wManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wManager.getDefaultDisplay(); // 为锟斤拷取锟斤拷幕锟�锟斤拷

		android.view.WindowManager.LayoutParams paras = dialog.getWindow()
				.getAttributes();
		paras.width = (int) (display.getWidth() * 0.95);

		paras.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(paras);
	}

	public static void addImageBeforeText(final TextView tv, int imgId) {

		ImageGetter imgGetter = new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				Drawable drawable = null;
				drawable = tv.getContext().getResources()
						.getDrawable(Integer.parseInt(source));
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				return drawable;
			}
		};

		StringBuffer sb = new StringBuffer();
		sb.append("<img src=\"").append(imgId).append("\"/>")
				.append(tv.getText().toString());
		Spanned span = Html.fromHtml(sb.toString(), imgGetter, null);
		tv.setText(span);

	}

	public static Spanned markText(String srcText, String markText, String color) {

		return Html.fromHtml(srcText.replace(markText, "<font color ='" + color
				+ "'>" + markText + "</font>"));
	}

	public static Spanned markText(String srcText, String markText) {

		return markText(srcText, markText, "#ff1111");
	}
}
