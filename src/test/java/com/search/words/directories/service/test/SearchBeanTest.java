package com.search.words.directories.service.test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.search.words.directories.SearchApplication;
import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.service.SearchRestService;
import com.search.words.directories.service.exception.PathNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SearchApplication.class})
@WebAppConfiguration
@ComponentScan(basePackages={"com.search.words"})
@PropertySource("application.properties")
public class SearchBeanTest{
	
	@Autowired
	Environment env;
	
	@Autowired
	SearchDirectories searchDirectories;
	
	@InjectMocks
    private SearchApplication app = new SearchApplication();
	
	@InjectMocks
    private SearchRestService searchRestService;
	
	
	/*@Test
	public void testMain(){
		app.main(new String[0]);
	}*/

	/**
	 * this test case is for GreenRoute.
	 * to search the word in the given path with regExp "_"
	 */
	@Test
	public void testSearch() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search(env.getProperty("path.to.search"), "Nandu","_","txt",finalMap);
		Assert.assertEquals(1,search.size());
	}
	
	//@Test(expected=UncheckedIOException.class)
	public void testUncheckedIOException() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search("C:\\testIOE", "Nandu","_","mkv",finalMap);
	}
	
	/**
	 * this test case is used to test if word is provided for searching or not
	 */
	@Test
	public void testSearchWithNull() {
		Map<String, List<String>> finalMap=new HashMap<>();
		try{
			searchDirectories.search(env.getProperty("path.to.search"), null, "_","txt",finalMap);
		}catch(Exception e){
			Assert.assertEquals("Word is Required to Search", e.getMessage());
		}
	}
	
	/**
	 * this test case is for not providing the path - this is exceptional case
	 * in case path is not provided during the request the default value from application.properties is picked
	 */
	@Test
	public void testSearchPathNotExist() {
		Map<String, List<String>> finalMap=new HashMap<>();
		try{
			searchDirectories.search(null, "Nandu","_","txt",finalMap);
		}catch(Exception e){
			Assert.assertEquals("Path is Required to Search", e.getMessage());
		}
	}
	
	/**
	 * this test case is for checking the directory without any files
	 */
	@Test
	public void testSearchFileNotExist() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search("D:\\EmptyFolder", "Nandu","_","txt",finalMap);
		Assert.assertEquals(0, search.size());
	}
	
	/**
	 * this test case is for sql 
	 */
	@Test
	public void testSQLFileExtension() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search("D:\\git-springboot-push", "Nandu","_","sql",finalMap);
		Assert.assertEquals(1, search.size());
	}
	
	/**
	 * this test case is for regexp and wordSeparator
	 */
	@Test
	public void testWordSeparatorFileExtension() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search("D:\\thread", "Nandu",",","txt",finalMap);
		Assert.assertEquals(1, search.size());
	}
	
	/**
	 * this test case is for directory doesnt exist
	 */
	@Test
	public void testDirectoryDoesNotExist() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search("D:\\adjfadj", "Nandu",",","txt",finalMap);
		Assert.assertEquals(null, search);
	}
	
	
	@Test(expected=PathNotFoundException.class)
	public void testPathNotFoundException() {
		Map<String, List<String>> finalMap=new HashMap<>();
		Map<String, List<String>> search = searchDirectories.search(null, "Nandu",",","txt",finalMap);
	}
	
	
	@Test
	public void testPathNotFoundExceptionMessage() {
		Map<String, List<String>> finalMap=new HashMap<>();
		try{
			searchDirectories.search(null, "Nandu",",","txt",finalMap);
		}catch(PathNotFoundException pne){
			Assert.assertEquals("Path is Required to Search", pne.getErrorMessage());
		}
	}
	
	@Test
	public void testPathNotFoundExceptionTwo() {
		Map<String, List<String>> finalMap=new HashMap<>();
		try{
		searchDirectories.search(null, "Nandu",",","txt",finalMap);
		}catch(PathNotFoundException pne){
			Assert.assertEquals("Path is Required to Search", pne.getErrorMessage());
		}
	}
	
	//PathNotFoundException
	
	
}
