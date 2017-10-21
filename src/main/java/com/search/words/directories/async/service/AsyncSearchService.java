package com.search.words.directories.async.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.search.words.directories.service.exception.FileReadingException;

@Service
public class AsyncSearchService {

	public static final Logger log = LoggerFactory.getLogger(AsyncSearchService.class);
	
	@Autowired
	Environment env;
	
	@Async("threadPoolTaskExecutor")
	public Future<Map<String,List<String>>> searchFiles(String wordToSearch,File listSubfolders){
		long startTime = System.currentTimeMillis();
		log.info("thread name is :: {}",Thread.currentThread().getName());
		Map<String,List<String>> finalListMap=new HashMap<>();
		List<String> listOfFilePaths=new ArrayList<>();
		log.info("listSubfolders path :: {}",listSubfolders.getAbsolutePath());
		
			File filesDirectory = new File(listSubfolders.getAbsolutePath()); // directory = target directory.
			
			for (File file : filesDirectory.listFiles()){
				if (!file.isDirectory()) {
							String fileName = file.getName();
							String fileAbsolutePath = file.getAbsolutePath();
							log.info("file name {} and file path {}",fileName,fileAbsolutePath);
							try (FileInputStream inputStream = new FileInputStream(fileAbsolutePath);){
								long fileSize = getFileSize(file);
								log.info("File Name {}, File Size {} ",fileName,fileSize);
							if(fileName.substring(fileName.lastIndexOf('.') + 1).equals(env.getProperty("search.file.extension")) && fileName.lastIndexOf('.') != -1){
									log.debug("File is more than 100MB {} ",fileName);
									int mb= 1024*1024*50;// if we want to load 80 mb file at a time
									byte[] buffer = new byte[mb];//80MB being read at a time
						    		int read = 0;
						    		while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
						    			log.debug("word to search {}", wordToSearch);
						    			String buffered=new String(buffer);
						    			if(buffered.contains(wordToSearch)){
						    					log.debug("Adding {} to list ",fileAbsolutePath);
						    					listOfFilePaths.add(fileAbsolutePath);
						    					finalListMap.put(wordToSearch, listOfFilePaths);
							                	break;
							                }
						    			int availableBytes = inputStream.available();
						    			log.debug("Next available bytes :: {}",availableBytes);
						    			if(availableBytes<mb){
						    				buffer = new byte[availableBytes];// this is to make sure we dont fill the array with default values if we dont have enough bytes to read
						    			}
						    			log.debug("Bytes still to be read {} ",(fileSize-read)); // do not enable this unless you are using the small file to test with 
						    			log.debug("Next buffer size to read :: {} ",buffer.length);// enable this only for testing with small files
						    			buffered=null;
						    		}buffer=null;
							}log.info("free Memory Available in Mbytes --> {} ",Runtime.getRuntime().freeMemory()/(1024*1024));
							}catch (IOException e) {
								log.error("Problem occured while processing file :: {}", e.getMessage());
								throw new FileReadingException("Problem occured while processing file");
							}catch(UncheckedIOException ue){
								log.error("UncheckedIOException occured :: {}", ue.getMessage());
							}catch(Exception ex){
								log.error("Exception occured :: {}", ex.getMessage());
							}
						}
			}
			long endTime=System.currentTimeMillis();
			log.info("total time took for Thread {} to process {} ",Thread.currentThread().getName(), (startTime-endTime));
		return new AsyncResult<Map<String,List<String>>>(finalListMap);
	}
	
	private static long getFileSize(File file) throws IOException {
		return FileChannel.open(Paths.get(file.getAbsolutePath())).size();
	}
	
	
}