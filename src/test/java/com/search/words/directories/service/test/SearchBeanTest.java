package com.search.words.directories.service.test;


import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
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
import com.search.words.directories.service.exception.DirectoryNotFoundException;

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
	
	URL url;
	File file;
	
	
	/**
	 * this setup class is used to test the classes by putting the required files in test/resources folder
	 */
	@Before
	public void setup(){
		url = this.getClass().getResource("/");
		file = new File(url.getFile());
	}
	
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
		Map<String, List<String>> search = searchDirectories.search("D:\\thread", "Nandu","_","txt",finalMap);
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
			Assert.assertEquals(null, e.getMessage());
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
	@Test(expected=DirectoryNotFoundException.class)
	public void testDirectoryDoesNotExist() {
		Map<String, List<String>> finalMap=new HashMap<>();
		searchDirectories.search("D:\\adjfadj", "Nandu",",","txt",finalMap);
	}
	
	@Test
	public void testMessageDirectoryDoesNotExist() {
		Map<String, List<String>> finalMap=new HashMap<>();
		try{
				searchDirectories.search("D:\\adjfadj", "Nandu",",","txt",finalMap);
		}catch(DirectoryNotFoundException dnfe){
			Assert.assertEquals("Directory Does not Exist, please provide a valid Directory", dnfe.getMessage());
		}
	}
	
}
