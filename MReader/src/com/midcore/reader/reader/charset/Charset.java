package com.midcore.reader.reader.charset;

public enum Charset {

	UTF8("UTF-8"), 
	UNICODE("Unicode"),
	UTF16BE("UTF-16BE"), 
	UTF16LE("UTF-16LE"), 
	GBK("GBK");
	
	private String mName;
	
	private Charset(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
}
