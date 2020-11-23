package indexing_text;

import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Main {

    public static void main(String[] args) throws Exception {
        //Create lucene searcher. It search over a single IndexReader.

        Searcher search = new Searcher();
        IndexSearcher searcher = search.getSearcher();
        //searcher.setSimilarity(new BM25Similarity());

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("Enter term to search : ");
            String term = sc.nextLine();

            //Search indexed contents using search term
            TopDocs foundDocs = search.searchInContent(term, searcher);

            //Total found documents
            System.out.println("Total Results :: " + foundDocs.totalHits);

            //printing out the path of files which have searched term
            for (ScoreDoc sd : foundDocs.scoreDocs) {
                Document d = searcher.doc(sd.doc);
                System.out.println("Path : " + d.get("path") + ", Score : " + sd.score);
            }
        }
    }
}
