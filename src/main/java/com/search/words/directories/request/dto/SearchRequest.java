package com.search.words.directories.request.dto;

import java.io.Serializable;

/**
 * Request object for Searching
 * @author IB1583 - Nandu Yenagandula
 *
 */
public class SearchRequest implements Serializable{
	
	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 6019634077758824885L;
	private String word;
	private String path;
	private String regExp;
	private String fileExtension;
	
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getRegExp() {
		return regExp;
	}
	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
