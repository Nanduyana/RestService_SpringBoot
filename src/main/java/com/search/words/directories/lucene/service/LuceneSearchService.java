package com.search.words.directories.lucene.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.search.words.directories.lucene.utils.IndexCreation;
import com.search.words.directories.lucene.utils.SearchIndex;
import com.search.words.directories.lucene.utils.UpdateIndex;


/**
 * this service is used to create/search/update the index by making use of Lucene
 * @author Nandu Yenagandula
 *
 */
@Component("luceneSearchService")
public class LuceneSearchService {

public static final Logger log = LoggerFactory.getLogger(LuceneSearchService.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	private IndexCreation indexCreation;
	
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private UpdateIndex updateIndex;
	
	/**
	 * this method is for creating the index
	 * @param filesDir
	 */
	public void createIndex(String filesDir) {
		indexCreation.createIndex(filesDir);
	}
	
	/**
	 * this method is for searching the index
	 * @param searchString
	 * @return
	 */
	public Map<String,List<String>> searchIndex(String searchString) {
		return searchIndex.searchIndex(searchString);
	}
	
	/**
	 * this method is for updating the index
	 * @param newFile
	 */
	public void updateIndex(File newFile) {
		updateIndex.updateIndex(newFile);
	}
	
}
