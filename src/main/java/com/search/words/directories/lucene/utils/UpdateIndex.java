package com.search.words.directories.lucene.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This Bean is used to update Index
 * @author Nandu Yenagandula
 *
 */
@Component("updateIndex")
public class UpdateIndex {
	
	public static final Logger log = LoggerFactory.getLogger(UpdateIndex.class);

	@Autowired
	Environment env;
	
	/**
	 * this method is used to Update the index of a file 
	 * @param newFile
	 */
	public void updateIndex(File newFile) {

		System.out.println("Updating index....");

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		try {
			// Store the index in file

			Directory directory = new SimpleFSDirectory(new File(IndexCreation.index));
			IndexWriter iwriter = new IndexWriter(directory, analyzer, false,
					MaxFieldLength.UNLIMITED);

			System.out.println(newFile.getPath());
			Document doc = new Document();
			
			//get max id
			IndexReader iReader = IndexReader.open(FSDirectory.open(new File(IndexCreation.index)), true);
			int i = iReader.maxDoc();
			i++;
			
			doc.add(new Field("id",""+i,Field.Store.YES,Field.Index.ANALYZED));

			doc.add(new Field("path", newFile.getPath(), Field.Store.YES,
					Field.Index.ANALYZED));

			Reader reader = new FileReader(newFile.getCanonicalPath());

			doc.add(new Field("contents", reader));
			iwriter.addDocument(doc);

			iwriter.optimize();
			iwriter.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
