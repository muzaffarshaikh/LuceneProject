package boolean_model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneNGram {

    public static void main(String[] args) throws IOException {
        
        String pathIndex = "C://LuceneData/example_index_lucene";
        Set<String> fieldset = new HashSet<>();
        fieldset.add( "docno" );
        fieldset.add( "title" );
        Directory dir = FSDirectory.open( new File( pathIndex ).toPath() );
        IndexReader index = DirectoryReader.open( dir );
        NGramTokenizer gramTokenizer = new NGramTokenizer(2, 3);
        for ( int docid = 0; docid < index.maxDoc() && docid < 1; docid++ ) {

                Document doc = index.document( docid, fieldset );
                String docno = doc.getField( "docno" ).stringValue();
                String title = doc.getField( "title" ).stringValue();
                Reader reader = new StringReader(docno);
                gramTokenizer.setReader(reader);
                CharTermAttribute charTermAttribute = gramTokenizer.addAttribute(CharTermAttribute.class);
                gramTokenizer.reset();
                 while (gramTokenizer.incrementToken()) {
                    String token = charTermAttribute.toString();
                    System.out.println(token);
                }
                gramTokenizer.end();
                gramTokenizer.close();
            }

        index.close();
        dir.close();
     
    }
}
