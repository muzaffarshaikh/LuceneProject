/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexing_json;

import path_constants.Constants;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author KillerMech
 */
public class Runtime {
    public static void main(String[] args) {        
        // First, open the directory
        Directory dir = null;
        try {
            dir = FSDirectory.open( new File( Constants.INDEX_PATH_REDDIT ).toPath() );
        } catch (IOException ex) {
            Logger.getLogger(Runtime.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Then, open an IndexReader to access your index
        IndexReader index = null;
        try {
            index = DirectoryReader.open( dir );
        } catch (IOException ex) {
            
            Logger.getLogger(Runtime.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Now, start working with your index using the IndexReader object

        System.out.println("Docs ::: " + index.numDocs());  // just an example; get the number of documents in the index
        
        String field = "title";//Field you want to search on 
        //options 
        //id
        //body
        //score
        //title
        
        //$$$$$$$$$$$$$Position Posting List

        // Just like building an index, we also need an Analyzer to process the query strings
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents( String fieldName ) {
                // Step 1: tokenization (Lucene's StandardTokenizer is suitable for most text retrieval occasions)
                TokenStreamComponents ts = new TokenStreamComponents( new StandardTokenizer() );
                // Step 2: transforming all tokens into lowercased ones (recommended for the majority of the problems)
                ts = new TokenStreamComponents( ts.getSource(), new LowerCaseFilter( ts.getTokenStream() ) );
                // Step 3: whether to remove stop words
                // Uncomment the following line to remove stop words
                // ts = new TokenStreamComponents( ts.getTokenizer(), new StopFilter( ts.getTokenStream(), StandardAnalyzer.ENGLISH_STOP_WORDS_SET ) );
                // Step 4: whether to apply stemming
                // Uncomment the following line to apply Krovetz or Porter stemmer
                // ts = new TokenStreamComponents( ts.getTokenizer(), new KStemFilter( ts.getTokenStream() ) );
                // ts = new TokenStreamComponents( ts.getTokenizer(), new PorterStemFilter( ts.getTokenStream() ) );
                return ts;
            }
        };

        QueryParser parser = new QueryParser( field, analyzer ); // a query parser that transforms a text string into Lucene's query object

        String qstr = "how AND NOT many"; // this is the textual search query
        Query query = null;
        try {
            query = parser.parse( qstr ); // this is Lucene's query object
            // you need to create a Lucene searcher
            IndexSearcher searcher = new IndexSearcher( index );

            // Lucene's default ranking model is VSM, but it has also implemented a wide variety of retrieval models.
            // Tell Lucene to rank results using the BM25 retrieval model.
            // Note that Lucene's implementation of BM25 is somehow different from the one we'll cover in class.
            //searcher.setSimilarity( new BM25Similarity() );
            searcher.setSimilarity( new ClassicSimilarity() );

            int top = 15; // Let's just retrieve the talk 10 results
            TopDocs docs = searcher.search( query, top ); // retrieve the top 10 results; retrieved results are stored in TopDocs

            System.out.printf( "%-10s%-20s%-10s%s\n", "Rank", "DocNo", "Score", "Title" );
            int rank = 1;
            for ( ScoreDoc scoreDoc : docs.scoreDocs ) {
                int docid = scoreDoc.doc;
                double score = scoreDoc.score;
                String docno = LuceneUtils.getDocno( index, "id", docid );
                String title = LuceneUtils.getDocno( index, "title", docid );
                System.out.printf( "%-10d%-20s%-10.4f%s\n", rank, docno, score, title );
                rank++;
            }
            
            
        } catch (ParseException ex) {
            Logger.getLogger(Runtime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Runtime.class.getName()).log(Level.SEVERE, null, ex);
        }
        //$$$$$$$$$$$$$Searching
        //$$$$$$$$$$$$$Searching
        
        
        
        
        
        
        try {
            // Remember to close both the IndexReader and the Directory after use
            index.close();
            dir.close();
        } catch (IOException ex) {
            Logger.getLogger(Runtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
