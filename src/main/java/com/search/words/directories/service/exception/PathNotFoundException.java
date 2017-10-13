package com.search.words.directories.service.exception;

/**
 * This Custom Exception is referred when we dont pass the correct path 
 * string
 * 
 * @author IB1583
 *
 */
public class PathNotFoundException extends RuntimeException {

	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 6283128132527494669L;
	
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public PathNotFoundException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

}
