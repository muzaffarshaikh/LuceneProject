package boolean_model;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TopDocs;

public class LuceneBooleanQueryDemo {

   Searcher searcher;

   public static void main(String[] args) {
      LuceneBooleanQueryDemo tester;
      try {
         tester = new LuceneBooleanQueryDemo();
         //tester.searchUsingBooleanQuery("CrowdLogging","Auto","distributed");
         tester.searchUsingBooleanQuery("Keisha","Latrina","");
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }
//Keisha, Latrina and Natella
   private void searchUsingBooleanQuery(String searchQuery1,
      String searchQuery2,String searchQuery3)throws IOException, ParseException {
       
      searcher = new Searcher();
      
      long startTime = System.currentTimeMillis();
    
      Term term1 = new Term("contents", searchQuery1);
      Query query1 = new TermQuery(term1);
      Term term2 = new Term("contents", searchQuery2);
      Query query2 = new PrefixQuery(term2);     
      Term term3 = new Term("contents", searchQuery3);
      Query query3 = new TermQuery(term3);
              
      
     BooleanQuery query = new BooleanQuery.Builder()
        .add(query1, BooleanClause.Occur.MUST) 
        .add(query2, BooleanClause.Occur.MUST) 
      //  .add(query3, BooleanClause.Occur.SHOULD)
//        .setMinimumNumberShouldMatch(1)   
        .build();

      TopDocs hits = searcher.search(query);
      long endTime = System.currentTimeMillis();

      System.out.println(hits.totalHits +
            " documents found. Time :" + (endTime - startTime) + "ms");
      for(ScoreDoc scoreDoc : hits.scoreDocs) {
         Document doc = searcher.getDocument(scoreDoc);
         System.out.println("File: "+ doc.get("path"));
      }
      searcher.close();
   }
}