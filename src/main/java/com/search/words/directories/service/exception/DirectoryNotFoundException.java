package com.search.words.directories.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DirectoryNotFoundException extends RuntimeException{
	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 2884379054270115953L;
	
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}
	public DirectoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage=message;
    }
    public DirectoryNotFoundException(String message) {
        super(message);
        this.errorMessage=message;
    }
    public DirectoryNotFoundException(Throwable cause) {
        super(cause);
        this.errorMessage=cause.getMessage();
    }
    
}
