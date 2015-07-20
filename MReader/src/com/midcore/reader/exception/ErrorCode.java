package com.midcore.reader.exception;

import com.midcore.reader.R;

public enum ErrorCode {

	FILE_NOT_EXIST(1, R.string.error_file_not_exist),
	
	BOOK_LOAD_FAIL(2, R.string.error_load_book);
	
	private int code;
	private int message;
	
	private ErrorCode(int code, int message){
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	
	public int getMessage() {
		return message;
	}
}
