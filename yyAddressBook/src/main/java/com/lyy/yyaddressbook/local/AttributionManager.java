package com.lyy.yyaddressbook.local;

import com.lyy.yyaddressbook.utils.DESUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class AttributionManager {

	private static final String FILE_NAME = "attribution";
	private static final String PASSWORD = "linyouye";

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private static final int KEY_LENGTH = 7;

	public AttributionManager(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		editor = preferences.edit();
		editor.apply();
	}

	public void addAttribution(String phone, String attribution) {
		if (phone == null || phone.length() < KEY_LENGTH) {
			return;
		} else {
			String key = phone.substring(0, KEY_LENGTH);

			String encryptedKey = DESUtils.encrypt(key);
			String encryptedAttribution = DESUtils.encrypt(attribution);

			// editor.putString(key, attribution);
			editor.putString(encryptedKey, encryptedAttribution);
			editor.commit();
		}
	}

	public String getAttribution(String phone) {
		if (phone == null || phone.length() < KEY_LENGTH) {
			return null;
		} else {
			String key = phone.substring(0, KEY_LENGTH);

			String encryptedAttribution = preferences.getString(
					DESUtils.decrypt(key), null);

			return encryptedAttribution == null ? null : DESUtils
					.decrypt(encryptedAttribution);
		}
	}

}
