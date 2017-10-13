package com.search.words.directories.error;

import java.io.Serializable;

/**
 * This class is used to append the error response in case of exceptions
 * @author IB1583 - Nandu Yenagandula
 *
 */
public class ErrorResponse implements Serializable{
	
	/**
	 * @serialField
	 */
	private static final long serialVersionUID = -5055276311010330826L;
	private int errorCode;
	private String message;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + errorCode + ", message=" + message
				+ "]";
	}
}