package com.niqiu.lib.SharePreference;

import java.lang.reflect.Type;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SharePreferenceSetting {

	public static <U> Object getSp(String file, String key,
			Context context, Type type) {
		SharedPreferences allIteminfo = context.getSharedPreferences(file, 2);
		String var = allIteminfo.getString(key, "");
		if (!TextUtils.isEmpty(var)) {
			Gson gson = new Gson();

			return gson.fromJson(var, type);

		} else
			return null;
	}

	public static <U> Object getSpString(String file, String key,
			Context context) {
		SharedPreferences allIteminfo = context.getSharedPreferences(file, 2);
		String var = allIteminfo.getString(key, "");
		if (!TextUtils.isEmpty(var)) {
			Gson gson = new Gson();
			return gson.fromJson(var, new TypeToken<String>() {
			}.getType());
		} else
			return null;
	}

	public static <U> void setSp(String file, String key, Context context,
			U u) {
		SharedPreferences allIteminfo = context.getSharedPreferences(file, 2);
		// String var = allIteminfo.getString(variable, "");
		Gson gson = new Gson();
		String var = gson.toJson(u);
		allIteminfo.edit().putString(key, var).commit();

	}
	
	public static void cleancahe(String file, String key, Context context)
	{
		SharedPreferences allIteminfo = context.getSharedPreferences(file, 2);
		allIteminfo.edit().putString(key, "").commit();
	}
	

}
