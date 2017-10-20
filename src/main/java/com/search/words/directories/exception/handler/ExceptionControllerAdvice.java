package com.search.words.directories.exception.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.search.words.directories.error.ErrorResponse;
import com.search.words.directories.rest.service.SearchRestService;
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
	
	/**
	 * this method handles if no input is given in the request 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ValueNotFoundException.class)
	public ResponseEntity<ErrorResponse> valueRequiredexceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	/**
	 * This method handles exception thrown when the provided directory for search is not available
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DirectoryNotFoundException.class)
	public ResponseEntity<ErrorResponse> directoryNotFoundexceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.NOT_FOUND.value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}
	
	/**
	 * this exception handler is for Illegal Argument Exception for any inputs wrongly given
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({IllegalArgumentException.class})
		public ResponseEntity<ErrorResponse> handleBadRequests(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage("Argument Not Allowed");
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * This exception handler handles the exceptions if there is any problem in reading the file
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({FileReadingException.class})
		public ResponseEntity<ErrorResponse> handleIOExceptionRequests(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * this overridden method handles if the request Json is not Parsable
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage("Not Able to Parse Json, please check the input provided");
		super.handleHttpMessageNotReadable(ex, headers, status, request);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * This exception handler which is overridden handles if Request Method is Not Supported
	 */
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED .value());
		error.setMessage("Please check the Request Method Type ");
		super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
		return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	/**
	 * This exception handler method which is overridden handles MediaType if not Supported by the service
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST .value());
		error.setMessage("Sorry, MediaType Not Supported");
		super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}


