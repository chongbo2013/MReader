package com.midcore.reader.exception;

public class BookException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ErrorCode errorCode;
	
	public BookException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public BookException(ErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
	
	public BookException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
