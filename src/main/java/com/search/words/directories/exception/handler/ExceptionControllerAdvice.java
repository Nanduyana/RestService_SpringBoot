package com.search.words.directories.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.search.words.directories.error.ErrorResponse;
/**
 * 
 * @author IB1583 - Nandu Yenagandula
 * @since OCT 2017
 */
@ControllerAdvice
public class ExceptionControllerAdvice {
	
	/**
	 * this exceptionHandler is defined to handle any unexpected Exceptions
	 * @param Exception
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR .value());
		error.setMessage("Please Contact your Administrator");
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	
}
