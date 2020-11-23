/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexing_json;

import path_constants.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author KillerMech
 */
//Data set can be obtained on https://www.kaggle.com/averkij/reddit-jokes-dataset
public class Index {

    public Index() throws FileNotFoundException, IOException, ParseException {
        Directory dir = null;

        //org.apache.lucene.store.Directory instance
        //FSDirectory implementation store index files in the file system.
        dir = FSDirectory.open(new File(Constants.INDEX_PATH_REDDIT).toPath());

        //analyzer with the stop words
        //builds TokenStreams which analyzes text. Extracting index terms from text. 
        //ENGLISH_STOP_WORDS_SET
        Analyzer analyzer;
        analyzer = new Analyzer() {

            @Override
            protected TokenStreamComponents createComponents(String fieldName) {

                //tokenization (Lucene's StandardTokenizer is suitable for most text retrieval occasions)
                TokenStreamComponents ts = new TokenStreamComponents(new StandardTokenizer());

                //transforming all tokens into lowercased ones (recommended for the majority of the problems)
                ts = new TokenStreamComponents(ts.getSource(), new LowerCaseFilter(ts.getTokenStream()));
                //whether to remove stop words
                // Uncomment the following line to remove stop words
                // ts = new TokenStreamComponents( ts.getTokenizer(), new StopFilter( ts.getTokenStream(), StandardAnalyzer.ENGLISH_STOP_WORDS_SET ) );
                //whether to apply stemming, Krovetz or Porter stemmer
                // ts = new TokenStreamComponents( ts.getTokenizer(), new KStemFilter( ts.getTokenStream() ) );
                // ts = new TokenStreamComponents( ts.getTokenizer(), new PorterStemFilter( ts.getTokenStream() ) );
                return ts;
            }
        };

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // Note that IndexWriterConfig.OpenMode.CREATE will override the original index in the folder
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter ixwriter = null;
        ixwriter = new IndexWriter(dir, config);

        // This is the field setting for metadata field.
        FieldType fieldTypeMetadata = new FieldType();
        fieldTypeMetadata.setOmitNorms(true);
        fieldTypeMetadata.setIndexOptions(IndexOptions.DOCS);
        fieldTypeMetadata.setStored(true);
        fieldTypeMetadata.setTokenized(false);
        fieldTypeMetadata.freeze();

        // This is the field setting for normal text field.
        FieldType fieldTypeText = new FieldType();
        fieldTypeText.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        fieldTypeText.setStoreTermVectors(true);
        fieldTypeText.setStoreTermVectorPositions(true);
        fieldTypeText.setTokenized(true);
        fieldTypeText.setStored(true);
        fieldTypeText.freeze();

        // You need to iteratively read each document from the corpus file,
        // create a Document object for the parsed document, and add that
        // Document object by calling addDocument().
        InputStream instream = null;

        instream = new FileInputStream(Constants.FILE_PATH_REDDIT);

        instream.close();

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(new FileReader(Constants.FILE_PATH_REDDIT));
        //JSONArray jsonArray = (JSONArray)obj.get(null);

        Iterator i = jsonArray.iterator();
        System.out.println("Indexing...");
        System.out.println("Please Wait...");
        int jCounter = 0;
        while (i.hasNext()) {
            jCounter++;
            JSONObject slide = (JSONObject) i.next();

            //  String title = (String)slide.get("body");
            //  System.out.println(title);
            // Create a Document object
            Document d = new Document();
            // Add each field to the document with the appropriate field type options
            d.add(new Field("id", (String) slide.get("id"), fieldTypeMetadata));
            d.add(new Field("body", (String) slide.get("body"), fieldTypeText));
            d.add(new Field("score", (long) slide.get("score") + "", fieldTypeText));
            d.add(new Field("title", (String) slide.get("title"), fieldTypeText));
            //    d.add( new Field( "text", text, fieldTypeText ) );
            // Add the document to index.
            ixwriter.addDocument(d);
        } System.out.println("Indexed "+jCounter+" JSON Objects");
        // remember to close both the index writer and the directory
        ixwriter.close();
        dir.close();

    }
}
