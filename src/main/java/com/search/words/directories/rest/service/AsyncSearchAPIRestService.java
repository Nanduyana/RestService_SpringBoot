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
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.search.words.directories.async.service.AsyncSearchService;

/**
 * This is the Rest Controller
 * @author IB1583
 *
 */
@RestController
public class AsyncSearchAPIRestService {

	public static final Logger log = LoggerFactory.getLogger(AsyncSearchAPIRestService.class);

	@Resource
	private AsyncSearchService searchAPI;

	@Autowired
	private Environment env;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@RequestMapping(value="/search", produces="application/json",method=RequestMethod.GET)
	public Map<String,List<String>> taskExecutor() throws InterruptedException, ExecutionException{
		
		long startTime = System.currentTimeMillis();
		
		List<File> listOfFiles = new ArrayList<>();
		List<File> listSubfolders = listSubfolders(env.getProperty("path.to.search"),listOfFiles);
		
		Map<String,List<String>>  finalMap=new HashMap<>();
		
		String wordToSearch="NanduSrDeveloper";
		
		for(int i=0;i<listSubfolders.size();i++){
			System.out.println("subfolder --> "+listSubfolders.get(i));
		}
		
		Collection<Future<Map<String,List<String>>>> futures = new ArrayList<>();
		
		for(int i=0;i<listSubfolders.size();i++){
			futures.add(searchAPI.searchFiles(wordToSearch,listSubfolders.get(i)));
		}
		
		List<String> list=new ArrayList<>();
		for (Future<Map<String,List<String>>> future : futures) {
	        Map<String, List<String>> map = future.get();
	        if(map.containsKey(wordToSearch)){
	        	list.addAll(map.get(wordToSearch));
	        }
	    }	

		finalMap.put(wordToSearch, list);
		
		long endTime = System.currentTimeMillis();

		long executeTime = endTime - startTime;
		
		log.info("total request time ::" +executeTime);
		
		return finalMap;
	}
	
	public List<File> listSubfolders(String directoryName, List<File> files) {
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isDirectory()) {
	        	files.add(file);
	        	listSubfolders(file.getAbsolutePath(), files);
	        }
	    }
	    return files;
	}
	
}
