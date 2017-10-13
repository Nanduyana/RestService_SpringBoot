package com.search.words.directories;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.service.exception.PathNotFoundException;
import com.search.words.directories.service.exception.ValueNotFoundException;

/**
 * 
 * @author IB1583 - Nandu Yenagandula
 * @since OCT-2017
 */
@Component("searchDirectories")
public class SearchDirectoriesBean implements SearchDirectories{
	
	public static final Logger log = LoggerFactory.getLogger(SearchDirectoriesBean.class);
	
	@Autowired
	private Environment env;
	
	/**
	 * @param path
	 * @param wordToSearch
	 * @return list of files that return the match with full path of the file
	 */
	public Map<String,List<String>> search(String path,String wordToSearch,String wordRegExp,String fileExtension,Map<String,List<String>> listOfFileswithWords) {
		log.debug("fileExtension:: {}--> path {}--> wordRegExp {}--->wordToSearch  {}--->", fileExtension, path, wordRegExp, wordToSearch);
		
		List<String> wordSearchedWithPath = new ArrayList<>();
		if(path==null){
			log.debug("Path is Required {}",path);
			throw new PathNotFoundException("Path is Required to Search");
		}else if(wordToSearch ==null){
			log.debug("Word is Required {}",wordToSearch);
			throw new ValueNotFoundException("Word is Required to Search");
		}
		
		File filesDirectory = new File(path); // directory = target directory.
		if (filesDirectory.exists()) // Directory exists then proceed.
		{log.debug("Directory Name :: {}--->{}",filesDirectory.listFiles());
		if(filesDirectory.listFiles().length>0){		
		for (File file : filesDirectory.listFiles()){
					try{
						log.debug("here file.toPath() {}",file.toPath());
						String fileName = file.getName();
						String fileAbsolutePath = file.getAbsolutePath();
						if (file.isDirectory()) {
							search(fileAbsolutePath,wordToSearch,wordRegExp,fileExtension,listOfFileswithWords);//recursive call for checking the subdirectories
						}
						if (!file.isFile()) continue;
						if (fileName.substring(fileName.lastIndexOf('.') + 1).equals(fileExtension) && fileName.lastIndexOf('.') != -1) {
							Files.lines(file.toPath()).forEach(line -> {	if(Pattern.compile(wordToSearch,Pattern.CASE_INSENSITIVE).matcher(line).find()){
												log.debug("Adding {} to list :: ",fileAbsolutePath);
												wordSearchedWithPath.add(fileAbsolutePath);
												listOfFileswithWords.put(wordToSearch, wordSearchedWithPath);
												log.debug("iterating ----------- > {} ",listOfFileswithWords);
											 };
										});
								}
						}catch (IOException e) {
							log.error("IOException occured :: {}", e.getMessage());
						}catch(UncheckedIOException uie){
							log.error("UncheckedIOException occured :: {}", uie.getMessage());
						}
				}
		}else{log.info("No Files in the directory {} ",filesDirectory.getName());}
		}else {
			log.info("\n Directory doesn't exist.");
			return null;
		}
		log.debug("list of words that are added with paths ---> {}", listOfFileswithWords);
		
		return listOfFileswithWords;
	}
}
