package com.midcore.util;

public class LoggerFactory {
	
	public static Logger getLogger(Class<?> target) {
		return new Logger(target);
	}

}
