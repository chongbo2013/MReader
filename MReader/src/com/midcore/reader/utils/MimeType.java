package com.midcore.reader.utils;

public enum MimeType {
	
	txt,
	epub,
	umd;
	
	public static MimeType valueOfMimeType(String name) {
		if (name == null) return null;
		if (name.startsWith("text/") || name.startsWith("txt/")) {
			return txt;
		} else if (name.contains("/epub")) {
			return epub;
		} else if (name.contentEquals("/umd")) {
			return umd;
		}
		return null;
	}
	
}
