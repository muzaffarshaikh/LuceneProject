package indexing_json;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.apache.lucene.index.MultiTerms;

public class PostingsListJson {

    public static void main( String[] args ) throws IOException {

            Scanner sc = new Scanner(System.in);
            String pathIndex = "C:\\Lucene\\Index\\index_redditJokes";

            // Let's just retrieve the posting list for the term in the "body" field
            String field = "body";
            System.out.println("Enter Term : ");
            
            String term = sc.nextLine();
            term = term.toLowerCase();

            Directory dir = FSDirectory.open( new File( pathIndex ).toPath() );
            IndexReader index = DirectoryReader.open( dir );

            // we also print out external ID
            Set<String> fieldset = new HashSet<>();
            fieldset.add( "id" );

            // The following line reads the posting list of the term in a specific index field.
            // You need to encode the term into a BytesRef object,
            //Lucene uses this class to represent terms that are encoded as UTF8 bytes in the index.
            // which is the internal representation of a term used by Lucene.
            System.out.printf( "%-10s%-15s%-10s%-20s\n", "DOC_ID", "DOCNO_JSON", "FREQ", "POSITIONS" );
            PostingsEnum posting = MultiTerms.getTermPostingsEnum( index, field, new BytesRef( term ), PostingsEnum.POSITIONS );
            if ( posting != null ) { // if the term does not appear in any document, the posting object may be null
                int id;
                // Each time you call posting.nextDoc(), it moves the cursor of the posting list to the next position
                // and returns the docid of the current entry (document). Note that this is an internal Lucene docid.
                // It returns PostingsEnum.NO_MORE_DOCS if you have reached the end of the posting list.
                while ( ( id = posting.nextDoc() ) != PostingsEnum.NO_MORE_DOCS ) {
                    String docno = index.document( id, fieldset ).get( "id" );
                    int freq = posting.freq(); // get the frequency of the term in the current document
                    System.out.printf( "%-10d%-15s%-10d", id, docno, freq );
                    for ( int i = 0; i < freq; i++ ) {
                        // Get the next occurrence position of the term in the current document.
                        // Note that you need to make sure by yourself that you at most call this function freq() times.
                        System.out.print( ( i > 0 ? "," : "" ) + posting.nextPosition() );
                    }
                    System.out.println();
                }
            }

            index.close();
            dir.close();
    }

}
