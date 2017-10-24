package com.search.words.directories.lucene.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This bean is used to createIndex for the provided directory
 * @author Nandu Yenagandula
 *
 */
@Component("indexCreation")
public class IndexCreation {
	
	public static final String index = "indexDir";
	
	public static final Logger log = LoggerFactory.getLogger(IndexCreation.class);
	
	@Autowired
	Environment env;
	
	/**
	 * this method is used to createIndex in the indexDir 
	 * @param filesDir
	 */
	public void createIndex(String filesDir) {

		log.info("Creating index for dir : "+filesDir);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		try {
			// Store the index in file

			Directory directory = new SimpleFSDirectory(new File(index));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
					MaxFieldLength.UNLIMITED);
			File dir = new File(filesDir);

			File[] files = dir.listFiles();
			
			int i = 1;

			for (File file : files) {
				System.out.println(file.getPath());
				Document doc = new Document();
				
				doc.add(new Field("id",""+i,Field.Store.YES,Field.Index.ANALYZED));

				doc.add(new Field("path", file.getPath(), Field.Store.YES,
						Field.Index.ANALYZED));

				Reader reader = new FileReader(file.getCanonicalPath());

				doc.add(new Field("contents", reader));
				iwriter.addDocument(doc);
				
				i++;
			}

			iwriter.optimize();
			iwriter.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
