package com.search.words.directories.rest.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.search.words.directories.async.service.AsyncSearchService;
import com.search.words.directories.request.dto.SearchRequest;
import com.search.words.directories.service.exception.ValueNotFoundException;

/**
 * This is the Rest Controller where the request begins from
 * @author Nandu Yenagandula
 *
 */
@RestController
@ComponentScan(basePackages={"com.search.words.*"})
public class AsyncSearchAPIRestService {

	public static final Logger log = LoggerFactory.getLogger(AsyncSearchAPIRestService.class);

	@Autowired
	private AsyncSearchService asyncSearchService;

	@Autowired
	private Environment env;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@RequestMapping(value="/search", produces="application/json", consumes= "application/json",method=RequestMethod.POST)
	public Map<String,List<String>> taskExecutor(@RequestBody SearchRequest search) throws InterruptedException, ExecutionException{
		log.info("free Memory Available in Mbytes at Startup--> {} ",Runtime.getRuntime().freeMemory()/(1024*1024));
		Map<String,List<String>>  finalMap=new HashMap<>();
		long startTime = System.currentTimeMillis();
		List<File> listOfFiles = new ArrayList<>();
		
		log.info("search : {} ",search);
		
		String word = search.getWord();
		log.info("word :: {} ",word);
		String path = search.getPath();
		log.info("path :: {} ",path);
		String regExp = search.getRegExp();
		log.info("regExp :: {} ",regExp);
		String fileExtension = search.getFileExtension();
		log.info("fileExtension :: {} ",fileExtension);
		
		String pathValidated=(path==null) ? env.getProperty("path.to.search"):path;
		File parentDirectory=new File(pathValidated);
		List<File> listSubfolders = listSubfolders(pathValidated,listOfFiles);
		listSubfolders.add(parentDirectory);
		
		Collection<Future<Map<String,List<String>>>> futures = new ArrayList<>();
		for(int i=0;i<listSubfolders.size();i++){
			log.info("subfolder --> {}", listSubfolders.get(i));
		}
		
		if(word!=null && !word.isEmpty()){
			log.debug("Word Obtained from Request :: {}", word);
			regExp = (regExp==null) ? env.getProperty("reg.exp.search"): regExp; 
			log.info("regExp Obtained from Request :: {}", regExp);
			fileExtension= (fileExtension==null) ? env.getProperty("search.file.extension"): fileExtension;
			
			log.debug("Going to Search in File Extension :: {}", fileExtension);
			
			String[] words=word.split(regExp);
			for(String wordToSearch:words){
				log.info("Searching for word :: {}",wordToSearch);
				for(int i=0;i<listSubfolders.size();i++){
					if(listSubfolders.get(i).listFiles()!=null){
						futures.add(asyncSearchService.searchFiles(wordToSearch,listSubfolders.get(i)));
					}
				}
			}
		}else{
			throw new ValueNotFoundException("Word to Search is Required");
		}
		
		for (Future<Map<String,List<String>>> future : futures) {
			Map<String, List<String>> map = future.get();
			Set<Entry<String,List<String>>> entrySet = map.entrySet();
			for(Entry<String,List<String>> entry :entrySet){
				if(finalMap.containsKey(entry.getKey())){
					List<String> list = finalMap.get(entry.getKey());
					list.addAll(entry.getValue());
				}else{
					finalMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;
		log.info("total request time :: {}" ,executeTime);
		return finalMap;
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
