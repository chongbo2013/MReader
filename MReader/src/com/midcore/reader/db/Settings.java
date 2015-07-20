package com.midcore.reader.db;

import java.util.HashMap;

import android.content.Context;

public final class Settings {
	
	private final static HashMap<String, String> sValueCache = new HashMap<String, String>();
	
	public synchronized static String getString(Context context, String key) {
		if (sValueCache.containsKey(key)) {
			return sValueCache.get(key);
		}
		String value = BookDBHelper.get(context).getSettingsValue(key);
		if (value != null) {
			sValueCache.put(key, value);
		}
		return value;
	}
	
	public synchronized static String getString(Context context, String key, String def) {
		if (sValueCache.containsKey(key)) {
			return sValueCache.get(key);
		}
		String value = BookDBHelper.get(context).getSettingsValue(key);
		if (value != null) {
			sValueCache.put(key, value);
		}
		return value == null ? def : value;
	}
	
	public synchronized static boolean putString(Context context, String key, String value) {
		BookDBHelper.get(context).addOrUpdateSettingsValue(key, value);
		sValueCache.put(key, value);
		return true;
	}
	
	public synchronized static int getInt(Context context, String key, int def) {
		String value = getString(context, key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return def;
	}
	
	public synchronized static boolean putInt(Context context, String key, int value) {
		return putString(context, key, String.valueOf(value));
	}
	
	public synchronized static boolean getBoolean(Context context, String key, boolean def) {
		String value = getString(context, key);
		if (value != null) {
			try {
				return Boolean.valueOf(value);
			} catch (Exception e) {
			}
		}
		return def;
	}
	
	public synchronized static boolean putBoolean(Context context, String key, boolean value) {
		return putString(context, key, String.valueOf(value));
	}
	
}
