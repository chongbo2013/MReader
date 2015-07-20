package com.midcore.util;

import android.util.Log;

public class Logger {
	
	final Class<?> target;
	
	final String tag;
	
	Logger(Class<?> target) {
		this.target = target;
		tag = target.getSimpleName();
	}

	public void debug(String msg) {
		Log.d(tag, msg);
	}
	
	public void error(String msg) {
		Log.e(tag, msg);
	}
	
	public void error(String msg, Throwable throwable) {
		Log.e(tag, msg, throwable);
	}

}
