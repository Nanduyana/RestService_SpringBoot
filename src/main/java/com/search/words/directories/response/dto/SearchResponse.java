package com.search.words.directories.response.dto;

import java.io.Serializable;
import java.util.List;

public class SearchResponse implements Serializable {

	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 3830714094565652569L;
	private List<String> word;
	private List<String> values;

	public List<String> getWord() {
		return word;
	}

	public void setWord(List<String> word) {
		this.word = word;
	} 

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "SearchResponse [word=" + word + ", values=" + values + "]";
	}

}
