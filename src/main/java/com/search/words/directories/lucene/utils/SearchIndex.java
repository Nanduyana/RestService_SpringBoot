package com.search.words.directories.lucene.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * this bean is used to search Index for the search String provided 
 * @author Nandu Yenagandula
 *
 */
@Component("searchIndex")
public class SearchIndex {
	
	public static final Logger log = LoggerFactory.getLogger(SearchIndex.class);

	@Autowired
	Environment env;
	
	/**
	 * this method is used to search index based on the searchString provided
	 * @param searchString
	 * @return
	 */
	public Map<String,List<String>> searchIndex(String searchString) {
		Map<String,List<String>> mapOfStringsFound=new HashMap<>();
		List<String> foundPath=new ArrayList<>();
		log.info("Searching.... '" + searchString + "'");

		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(
					IndexCreation.index)), true);
			IndexSearcher searcher = new IndexSearcher(reader);

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);// construct our usual analyzer

			QueryParser qp = new QueryParser(Version.LUCENE_30, "contents",
					analyzer);
			Query query = qp.parse(searchString); // parse the query and construct the Query object

			TopDocs hits = searcher.search(query, 100); // run the query

			if (hits.totalHits == 0) {
				System.out.println("No data found.");
			} else {
				for (int i = 0; i < hits.totalHits; i++) {
					Document doc = searcher.doc(hits.scoreDocs[i].doc); // get the next document
					String id = doc.get("id"); // get its id field
					String url = doc.get("path"); // get its path field
					log.info("Found in :: "+ id +" "+ url);
					foundPath.add(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mapOfStringsFound.put(searchString, foundPath);
		return mapOfStringsFound;
	}

}
