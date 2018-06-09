package sysqa;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;

@Path("/qa")
public class Index {

    private static String dataPath = "/home/paladnix/resources/";
    private static String indexPath = "/home/paladnix/resources/index/";
    private static String[] classifies = {"<unk>", "baby", "car",
                                        "discovery", "entertainment", "essay",
                                        "fashion", "finance", "food", "game",
                                        "history", "military", "regimen",
                                        "society", "sports", "story",
                                        "tech", "travel", "world"};

    public void createIndex() throws IOException{

        int size = classifies.length;
        for(int i = 0; i < size; i++) {
            String indexFile = indexPath + classifies[i];
            IndexWriter indexWriter = null;
            try {
                Directory directory = FSDirectory.open(new File(indexFile));
                Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
                IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
                indexWriter = new IndexWriter(directory, config);

                System.out.println("Create IndexWriter of " + indexFile + " successfully.");

                String filePath = dataPath + classifies[i];
                System.out.println(filePath);
                FileInputStream inputStream = new FileInputStream(filePath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str = null;

                while((str = bufferedReader.readLine()) != null) {
                    String[] QAs = str.split(" ", 2);
                    Document document = new Document();
                    document.add(new Field("Q", QAs[0], Field.Store.YES, Field.Index.ANALYZED));
                    document.add(new Field("A", QAs[1], Field.Store.YES, Field.Index.NOT_ANALYZED));
                    indexWriter.addDocument(document);
                }
                inputStream.close();
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                System.out.println("Complete IndexWriter of " + indexFile + ".");
                try {
                    if(indexWriter != null) {
                        indexWriter.close();
                    }
                } catch( Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String search(String question, String classify) throws Exception {
        IndexReader indexReader = null;
        List<String> answers = new ArrayList<String>();

        String tmp = null;
        String indexFile = indexPath + classify;
        try {
            Directory directory = FSDirectory.open(new File(indexFile));
            indexReader = IndexReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
            QueryParser queryParser = new QueryParser(Version.LUCENE_35, "Q", analyzer);
            Query query = queryParser.parse(question);

            TopDocs topDocs = indexSearcher.search(query, 10);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            for (ScoreDoc scoreDoc : scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                // System.out.println(document.get("A"));
                // answers.add(document.get("A"));
                tmp = document.get("A");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(indexReader != null) {
                    indexReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return answers;
        return tmp;
    }

    public String getClassify(String question) {
        Socket2Classify socket2Classify = new Socket2Classify();
        String classify = Socket2Classify.getClassify(question);
        return classify;
    }


    @GET @Path("/{question}")
    @Produces({"text/plain;charset=UTF-8"})
    public String getAnswer(@PathParam("question") String question) {
        Index index = new Index();
        String classify = index.getClassify(question);
        // List<String> ret = null;
        String ret = null;
        try {
            ret = index.search(question, classify);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        if( !ret.isEmpty()){
            return ret.get(0);
        }
        else {
        */
        return ret;
        // }
    }
/*
    @GET @Path("/{name}")
    public String hello(@PathParam("name") String name){
        return "Hello" + name;
    }
*/
    public static void main(String[] args) {
        Index index = new Index();
        String question = "汽车出险一次,第二年保险费上浮多少";
        String classify = index.getClassify(question);
        System.out.println(question + " " + classify);
        try {
            String ret = index.search(question, classify);
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
