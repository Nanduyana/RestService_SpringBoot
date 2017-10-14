package com.search.words.directories.exception.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.search.words.directories.error.ErrorResponse;
import com.search.words.directories.service.SearchRestService;
import com.search.words.directories.service.exception.DirectoryNotFoundException;
import com.search.words.directories.service.exception.FileReadingException;
import com.search.words.directories.service.exception.ValueNotFoundException;
/**
 * 
 * @author IB1583 - Nandu Yenagandula
 * @since OCT 2017
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler{
	
	
	public static final Logger log = LoggerFactory.getLogger(SearchRestService.class);

	/**
	 * this exceptionHandler is defined to handle any unexpected Exceptions
	 * @param Exception
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR .value());
		error.setMessage("Please Contact your Administrator");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ValueNotFoundException.class)
	public ResponseEntity<ErrorResponse> valueRequiredexceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	@ExceptionHandler(DirectoryNotFoundException.class)
	public ResponseEntity<ErrorResponse> directoryNotFoundexceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.NOT_FOUND.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	@ExceptionHandler({IllegalArgumentException.class})
		public ResponseEntity<ErrorResponse> handleBadRequests(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage("Argument Not Allowed");
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({FileReadingException.class})
		public ResponseEntity<ErrorResponse> handleIOExceptionRequests(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}


