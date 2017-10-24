package com.search.words.directories.rest.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.search.words.directories.lucene.service.LuceneSearchService;
import com.search.words.directories.request.dto.SearchRequest;
import com.search.words.directories.service.exception.FileReadingException;
import com.search.words.directories.service.exception.ValueNotFoundException;

@RestController
public class LuceneSearchController {

	public static final Logger log = LoggerFactory.getLogger(LuceneSearchController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private LuceneSearchService luceneSearchService;
	
	/**
	 * This method is used to search with Lucene 
	 * @param search
	 * @return
	 * @throws FileReadingException
	 */
	@RequestMapping(value = "/lucene/search" ,method = RequestMethod.POST, produces = "application/json", consumes= "application/json")
	public Map<String,List<String>> luceneSearch(
			@RequestBody SearchRequest search) throws FileReadingException {
		
		log.info("inside lucene search");
		
		Map<String,List<String>> finalWordSearch = new HashMap<>();
		List<File> listOfFiles = new ArrayList<>();
		List<Map<String,List<String>>> finalListOfValues=new ArrayList<>();
		
		String word = search.getWord();
		String path = search.getPath();
		String regExp = search.getRegExp();
		String fileExtension = search.getFileExtension();
		
		log.info("word {}, path {}, regExp {}, fileExtension {}",word,path,regExp,fileExtension);
		//Here path and regExp is optional if not provided it will take the default values from application.properties file.
		if(word!=null && !word.isEmpty()){
			log.debug("Word Obtained from Request :: {}", word);
			
			regExp = (regExp==null) ? env.getProperty("reg.exp.search"): regExp; 
			log.info("regExp Obtained from Request :: {}", regExp);
			fileExtension= (fileExtension==null) ? env.getProperty("search.file.extension"): fileExtension;
			
			log.debug("Going to Search in File Extension :: {}", fileExtension);
			
			path = (path==null) ? env.getProperty("path.to.search"):path;
			log.debug("wordPath Obtained from Request :: {}", path);
			
			String pathValidated=(path==null) ? env.getProperty("path.to.search"):path;
			File parentDirectory=new File(pathValidated);
			
			List<File> listSubfolders = listSubfolders(pathValidated,listOfFiles);
			listSubfolders.add(parentDirectory);
			
			for(int i=0;i<listSubfolders.size();i++){
				luceneSearchService.createIndex(listSubfolders.get(i).getAbsolutePath());
			}
			
			String[] words=word.split(regExp);
			for(String wordToSearch:words){
				finalListOfValues.add(luceneSearchService.searchIndex(wordToSearch));
			}
			
			for(Map<String,List<String>> map:finalListOfValues){
				Set<Entry<String,List<String>>> entrySet = map.entrySet();
				for(Entry<String,List<String>> entry :entrySet){
					if(finalWordSearch.containsKey(entry.getKey())){
						List<String> list = finalWordSearch.get(entry.getKey());
						list.addAll(entry.getValue());
					}else{
						finalWordSearch.put(entry.getKey(), entry.getValue());
					}
				}
				
			}
		}else{
			throw new ValueNotFoundException("Word to Search is Required");
		}
	return finalWordSearch;
	}
	
	public List<File> listSubfolders(String directoryName, List<File> files) {
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isDirectory()) {
	        	files.add(file);
	        	log.info("Adding Directory :: {}",files);
	        	listSubfolders(file.getAbsolutePath(), files);
	        }
	    }
	    return files;
	}
	
}
