package com.search.words.directories.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This Custom Exception is referred when we dont pass a value in the query
 * string
 * 
 * @author IB1583
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ValueNotFoundException extends RuntimeException {

	/**
	 * @serialField
	 */
	private static final long serialVersionUID = -7212558017818758333L;
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public ValueNotFoundException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

}
