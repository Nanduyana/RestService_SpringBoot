package com.search.words.directories.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This Custom Exception is referred when we dont pass the correct path 
 * string
 * 
 * @author Nandu Yenagandula
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileReadingException extends RuntimeException {

	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 6283128132527494669L;
	
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public FileReadingException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

}
