package com.search.words.directories.service.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.request.dto.SearchRequest;
import com.search.words.directories.service.SearchRestService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = SearchRestService.class, secure = false)
public class SearchControllerTest {
	
	//private MockMvc mockMvc;
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SearchDirectories searchDirectories; 
	
	/*	@LocalServerPort
		private int port;
	 */
	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();
	
	@Test
	public void greenRoute() {
		SearchRequest searchRequest=new SearchRequest();
		searchRequest.setWord("NanduSrDeveloper");
		
		HttpEntity<SearchRequest> entity = new HttpEntity<SearchRequest>(searchRequest, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/search/rest/directory"),
				HttpMethod.POST, entity, String.class);
		String body = response.getBody();
		System.out.println(body);
		//String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);
		//Assert.assertTrue(actual.contains("/search/rest/directory"));
	}
	
	private String createURLWithPort(String uri) {
		return "http://localhost:8190" + uri;
	}
}
