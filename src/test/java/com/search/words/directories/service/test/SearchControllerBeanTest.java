package com.search.words.directories.service.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.search.words.directories.SearchApplication;
import com.search.words.directories.error.ErrorResponse;
import com.search.words.directories.exception.handler.ExceptionControllerAdvice;
import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.request.dto.SearchRequest;
import com.search.words.directories.service.SearchRestService;
import com.search.words.directories.service.exception.ValueNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SearchApplication.class})
@WebAppConfiguration
@ComponentScan(basePackages={"com.search.words"})
@PropertySource("application.properties")
public class SearchControllerBeanTest{
	
	@Mock
	Environment env;
	
	@Mock
	SearchDirectories searchDirectories;
	
	@InjectMocks
    private SearchApplication app = new SearchApplication();
	
	@InjectMocks
    private SearchRestService searchRestService;
	
	@InjectMocks
	private ExceptionControllerAdvice exceptionAdvice;
	
	//PathNotFoundException
	
	
	/*@Test
	public void testMain(){
		app.main(new String[0]);
	}*/
	
	@Test
	public void testExceptionAdvice(){
		ResponseEntity<ErrorResponse> genericExceptionHandler = exceptionAdvice.genericExceptionHandler(new Exception(""));
		ErrorResponse body = genericExceptionHandler.getBody();
		Assert.assertEquals(500,body.getErrorCode());
		Assert.assertEquals("Please Contact your Administrator",body.getMessage());
	}
	
	@Test
	public void testRestService(){
		//searchRestService.getAllSearches(search);
		Assert.assertNotNull(searchRestService);
	}
	
	@Test
	public void testRestMethods(){
		SearchRequest searchRequest=new SearchRequest();
		searchRequest.setWord("Nandu");
		searchRequest.setPath("D:\\thread");
		searchRequest.setRegExp("_");
		searchRequest.setFileExtension("txt");
		searchRestService.getAllSearches(searchRequest);
	}
	
	@Test(expected=ValueNotFoundException.class)
	public void testWordException(){
		SearchRequest searchRequest=new SearchRequest();
		searchRequest.setWord(null);
		searchRequest.setPath("D:\\thread");
		searchRequest.setRegExp("_");
		searchRequest.setFileExtension("txt");
		searchRestService.getAllSearches(searchRequest);
	}
	
	@Test(expected=ValueNotFoundException.class)
	public void testWordNull(){
		SearchRequest searchRequest=new SearchRequest();
		searchRequest.setWord(null);
		searchRestService.getAllSearches(searchRequest);
	}
	
	@Test
	public void testExceptionMessage(){
		SearchRequest searchRequest=new SearchRequest();
		searchRequest.setWord(null);
		try{
		searchRestService.getAllSearches(searchRequest);
		}catch(ValueNotFoundException vnf){
			Assert.assertEquals("Word to Search is Required", vnf.getErrorMessage());
		}
		
	}
	
/*	@Test(expected=ValueNotFoundException.class)
	public void testValueRequiredexceptionHandler(){
		searchRestService.valueRequiredexceptionHandler(new ValueNotFoundException(""));
	}*/
	
	@Test
	public void testValueRequiredexceptionHandlerErrorCode(){
			ResponseEntity<ErrorResponse> valueRequiredexceptionHandler = searchRestService.valueRequiredexceptionHandler(new ValueNotFoundException("Bad Request"));
			ErrorResponse body = valueRequiredexceptionHandler.getBody();
			Assert.assertEquals(400, body.getErrorCode());
			Assert.assertEquals("Bad Request", body.getMessage());
			System.err.println(body.toString());
	}
	
	
}
