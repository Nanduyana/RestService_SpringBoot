package com.search.words.directories.interfaces;

import java.util.List;
import java.util.Map;

import com.search.words.directories.service.exception.DirectoryNotFoundException;
import com.search.words.directories.service.exception.PathNotFoundException;

/**
 * 
 * @author IB1583 - Nandu Yenagandula
 * @since OCT-2017
 */
public interface SearchDirectories {

	/**
	 * 
	 * @param path
	 * @param wordToSearch
	 * @return
	 */
	public Map<String,List<String>> search(String path,String wordToSearch,String wordRegExp,String fileExtension,Map<String,List<String>> finalListOfWordsWithPaths)throws DirectoryNotFoundException,PathNotFoundException;
	
}
