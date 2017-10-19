package com.search.words.directories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.search.words.directories.interfaces.SearchDirectories;
import com.search.words.directories.service.exception.DirectoryNotFoundException;
import com.search.words.directories.service.exception.FileReadingException;

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
	public Map<String,List<String>> search(String path,String wordToSearch,String wordRegExp,String fileExtension,Map<String,List<String>> listOfFileswithWords) throws FileReadingException{
		log.info("fileExtension:: {}--> path {}--> wordRegExp {}--->wordToSearch  {}--->listOfFileswithWords {} ", fileExtension, path, wordRegExp, wordToSearch,listOfFileswithWords);
		List<String> wordSearchedWithPath = listOfFileswithWords.get(wordToSearch);
		if(wordSearchedWithPath==null){
			wordSearchedWithPath = new ArrayList<>();
		}
		log.info("Thread Name :: {}",Thread.currentThread().getName());
		File filesDirectory = new File(path); // directory = target directory.
		if (filesDirectory.exists()) // Directory exists then proceed.
		{log.debug("Directory Name :: {} ",filesDirectory.listFiles());
		if(filesDirectory.listFiles().length>0){		
		for (File file : filesDirectory.listFiles()){
						String fileName = file.getName();
						log.info("here file.toPath() filePath {} fileName{}",file.toPath(),fileName );
						String fileAbsolutePath = file.getAbsolutePath();
						if (file.isDirectory()) {
							log.info("Available in listOfFileswithWords size : {}",listOfFileswithWords.size());
								 	search(fileAbsolutePath,wordToSearch,wordRegExp,fileExtension,listOfFileswithWords);//recursive call for checking the subdirectories
						}
						if (!file.isFile()) continue;
						try (FileInputStream inputStream = new FileInputStream(fileAbsolutePath);){
							long fileSize = getFileSize(file);
							log.info("File Name {}, File Size {} ",fileName,fileSize);
							if(fileName.substring(fileName.lastIndexOf('.') + 1).equals(fileExtension) && fileName.lastIndexOf('.') != -1){
								//if((fileSize/(1024*1024))>100){// Enable if you really want to read a file more than 100MB checking if file is > 100 MB then read chunks of the file and check if the search string exist or not
									log.debug("File is more than 100MB {} ",fileName);
									int mb= 1024*80;// if we want to load 80 mb file at a time
									byte[] buffer = new byte[mb];//80MB being read at a time
						    		int read = 0;
						    		log.debug("Available listOfFileswithWords : {} ", listOfFileswithWords);
						    		
						    		while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
						    			log.debug("word to search {}", wordToSearch);
						    			String buffered=new String(buffer);
						    			if(buffered.contains(wordToSearch)){
						    					log.debug("Adding {} to list ",fileAbsolutePath);
						    					wordSearchedWithPath.add(fileAbsolutePath);
						    					listOfFileswithWords.put(wordToSearch, wordSearchedWithPath);
						    					log.debug("total size of the map is :: {} ", listOfFileswithWords.size());
						    					log.debug("Iterating ----------- > {} ",listOfFileswithWords);
							                		break;
							                }
						    			int availableBytes = inputStream.available();
						    			log.debug("Next available bytes :: {}",availableBytes);
						    			if(availableBytes<mb){
						    				buffer = new byte[availableBytes];// this is to make sure we dont fill the array with default values if we dont have enough bytes to read
						    			}
						    			log.debug("Bytes still to be read {} ",(fileSize-read)); // do not enable this unless you are using the small file to test with 
						    			log.debug("Next buffer size to read :: {} ",buffer.length);// enable this only for testing with small files
						    		}
							//}
							log.info("list added to map ",listOfFileswithWords);
							}
					}catch (IOException e) {
							log.error("Problem occured while processing file :: {}", e.getMessage());
							throw new FileReadingException("Problem occured while processing file");
						}catch(UncheckedIOException ue){
							log.error("UncheckedIOException occured :: {}", ue.getMessage());
						}catch(Exception ex){
							log.error("Exception occured :: {}", ex.getMessage());
						}
		}
		}else{log.debug("No Files in the directory {} ",filesDirectory.getName());}
		}else {
			log.info("\n Directory doesn't exist.");
			throw new DirectoryNotFoundException("Directory Does not Exist, please provide a valid Directory");
		}
		log.info("list of words that are added with paths ---> {}", listOfFileswithWords);
		
		return listOfFileswithWords;
	}
	
	private static long getFileSize(File file) throws IOException {
		return FileChannel.open(Paths.get(file.getAbsolutePath())).size();
	}
}
