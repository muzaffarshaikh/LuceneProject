package boolean_model;

import path_constants.Constants;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanQuery;

public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;
   // String pathIndex =  "C:\\IR\\CA\\MovieReviews\\Index";
    Analyzer analyzer = new Analyzer() {
        @Override
        protected Analyzer.TokenStreamComponents createComponents(String fieldName) {
            Analyzer.TokenStreamComponents ts = new Analyzer.TokenStreamComponents(new StandardTokenizer());
            ts = new Analyzer.TokenStreamComponents(ts.getSource(), new LowerCaseFilter(ts.getTokenStream()));
            return ts;
        }
    };

    IndexReader index ;
    public Searcher() throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(Constants.INDEX_PATH_MOVIES).toPath());
        index = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(index);
        String field = "contents"; 
        queryParser = new QueryParser(field, analyzer);
    }

    public TopDocs search(Query query)
            throws IOException, ParseException {
        return indexSearcher.search(query, 50);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        index.close();
    }
}