package com.search.words.directories.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.request.dto.SearchRequest;
import com.search.words.directories.response.dto.SearchResponse;
import com.search.words.directories.service.exception.DirectoryNotFoundException;
import com.search.words.directories.service.exception.FileReadingException;
import com.search.words.directories.service.exception.ValueNotFoundException;
/**
 * This is the Rest Controller
 * @author IB1583
 *
 */
@RestController
public class SearchRestService {

	public static final Logger log = LoggerFactory.getLogger(SearchRestService.class);

	@Autowired
	SearchDirectories searchDirectories;

	@Autowired
	private Environment env;

	/**
	 * This Rest Service is used to find out the words in the given Path (application.properties)
	 * @param word
	 * @return
	 * @throws IOException  
	 * @throws DirectoryNotFoundException 
	 */
	@RequestMapping(value = "/search/rest/directory" ,method = RequestMethod.POST, produces = "application/json", consumes= "application/json")
	public SearchResponse getAllSearches(
			@RequestBody SearchRequest search) throws FileReadingException {
		
		Map<String,List<String>> finalWordSearch = new HashMap<>();
		String word = search.getWord();
		String path = search.getPath();
		String regExp = search.getRegExp();
		String fileExtension = search.getFileExtension();
		
		log.info("word {}, path {}, regExp {}, fileExtension {}",word,path,regExp,fileExtension);
		//Here path and regExp is optional if not provided it will take the default values from application.properties file.
		if(word!=null && !word.isEmpty()){
			log.debug("Word Obtained from Request :: {}", word);
			path = (path==null) ? env.getProperty("path.to.search"):path;
			log.debug("wordPath Obtained from Request :: {}", path);
			regExp = (regExp==null) ? env.getProperty("reg.exp.search"): regExp; 
			log.info("regExp Obtained from Request :: {}", regExp);
			fileExtension= (fileExtension==null) ? env.getProperty("search.file.extension"): fileExtension;
			
			log.debug("Going to Search in File Extension :: {}", fileExtension);
			
			String[] words=word.split(regExp);
			for(String wordToSearch:words){
				searchDirectories.search(path, wordToSearch,regExp,fileExtension,finalWordSearch);
			}
		}else{
			throw new ValueNotFoundException("Word to Search is Required");
		}
		SearchResponse searchResponse=new SearchResponse();
		List<String> wordsSearched=new ArrayList<>();
		List<String> listOfWords=new ArrayList<>();
		
		finalWordSearch.entrySet().forEach(entry -> {
			listOfWords.add(entry.getKey());
			wordsSearched.addAll(entry.getValue());
		}); 
		searchResponse.setWord(listOfWords);
		searchResponse.setValues(wordsSearched);
		
		log.debug("List of Keys :: {} List of Values {} Response View , {}",searchResponse.getWord(), searchResponse.getValues(),searchResponse.toString());
		log.info("i am completing the request");
		return searchResponse;
	}
	
}
