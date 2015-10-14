package com.lyy.yyaddressbook.local;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountManager {

	private static final String FILE_NAME = "account";

	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private static final String KEY_FIRST_BOOT = "key_first_boot";

	public AccountManager(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public boolean isFirstBoot() {
		return preferences.getBoolean(KEY_FIRST_BOOT, true);
	}

	public void setFirstBoot(boolean isFirstBoot) {
		editor.putBoolean(KEY_FIRST_BOOT, isFirstBoot);
		editor.commit();
	}
}
