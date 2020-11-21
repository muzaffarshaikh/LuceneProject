package ninja;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexCreation {

    public static void main(String[] args) {

        Instant start = Instant.now();

        //Input Folder (Files)
        String DOCUMENT_DIRECTORY = "C:\\IR\\Exc7";

        File Folder = new File(DOCUMENT_DIRECTORY);
        int fileCount = Folder.list().length;

        System.out.println("Indexing " + fileCount + " Files...");
        System.out.println("Please Wait...");

        //Output folder (Index)
        String INDEX_DIRECTORY = "C:\\IR\\CA\\labworkIndex";

        //Input Path Variable
        final Path DOCUMENT_DIRECTORY_PATH = Paths.get(DOCUMENT_DIRECTORY);

        try {

            //org.apache.lucene.store.Directory instance
            //FSDirectory implementation store index files in the file system.
            Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

            //analyzer with the stop words
            //builds TokenStreams which analyzes text. Extracting index terms from text. 
            //ENGLISH_STOP_WORDS_SET
            Analyzer analyzer;
            analyzer = new Analyzer() {
                @Override
                protected Analyzer.TokenStreamComponents createComponents(String string) {

                    //Tokenization --> StandardTokenizer
                    TokenStreamComponents ts = new TokenStreamComponents(new StandardTokenizer());

                    //transforming all tokens into lowercase
                    ts = new TokenStreamComponents(ts.getSource(), new LowerCaseFilter(ts.getTokenStream()));

                    // StopWord Removal
                    ts = new TokenStreamComponents(ts.getSource(), new StopFilter(ts.getTokenStream(), EnglishAnalyzer.ENGLISH_STOP_WORDS_SET));

                    // Stemming
                    ts = new TokenStreamComponents(ts.getSource(), new PorterStemFilter(ts.getTokenStream()));
                    // ts = new TokenStreamComponents( ts.getTokenizer(), new KStemFilter( ts.getTokenStream() ) );

                    return ts;

                }
            };

            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);

            //Its recursive method to iterate all files and directories
            indexAllDocuments(writer, DOCUMENT_DIRECTORY_PATH);

            writer.close();
        } catch (IOException e) {
        }

        Instant end = Instant.now();
        System.out.println("\nIndex Created.");

        Duration interval = Duration.between(start, end);

        System.out.println("Time Taken : " + interval.getSeconds() + " Seconds");

    }

    static void indexDocument(IndexWriter writer, Path file, long lastModified) throws IOException {
        
        FieldType fieldTypeText = new FieldType();
        fieldTypeText.setIndexOptions( IndexOptions.DOCS_AND_FREQS_AND_POSITIONS );
        fieldTypeText.setStoreTermVectors( true );
        fieldTypeText.setStoreTermVectorPositions( true );
        fieldTypeText.setTokenized( true );
        fieldTypeText.setStored( true );
        fieldTypeText.freeze();
        
        
        try (InputStream stream = Files.newInputStream(file)) {
            //Create lucene Document
            Document doc = new Document();

            
            
            
            doc.add(new Field("path", file.toString(), fieldTypeText));
                    //new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new Field("modified", lastModified + "", fieldTypeText));
            doc.add(new Field("contents", new String(Files.readAllBytes(file)), fieldTypeText));
            ///doc.add(new LongPoint("modified", lastModified));
            //doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));

            //Updates a document by first deleting the document(s) 
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }

    static void indexAllDocuments(final IndexWriter writer, Path path) throws IOException {

        if (Files.isDirectory(path)) {

            //Iterate through files in directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        //Index this file
                        indexDocument(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException ioe) {
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            //Index this file
            indexDocument(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }
}
