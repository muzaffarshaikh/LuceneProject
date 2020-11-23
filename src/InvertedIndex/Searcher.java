/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InvertedIndex;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Muzaffar
 */
public class Searcher {

    //directory contains the lucene indexes
    private static final String INDEX_DIR = "C:\\Users\\Muzaffar\\Desktop\\LuceneTest\\Index";

    public IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);

        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    public TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
        //Create search query
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }

}
