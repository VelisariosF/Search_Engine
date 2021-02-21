package com.webApplication.Search_Engine;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.jsoup.*;
import opennlp.tools.stemmer.*;

/**
 * This class implements the tokenizer
 */
public class Tokenizer {
    //this set contains the stopwords
    private static final HashSet<String> stopwords = FilesHandler.getStopwords();
    //This method is used to extract  and return the text from a web page source code
    public static String extractText(Reader reader, int documentId) {
        StringBuilder sb = new StringBuilder();
        try{

            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
     br.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        String textOnly = null;
        String documentTitle = null;
        if(sb != null){
            //get the documents title
            documentTitle = Jsoup.parse(sb.toString()).title();
            //save it
            FilesHandler.documentsTitles.put(documentId, documentTitle);
            //get the documents text
            textOnly = Jsoup.parse(sb.toString()).text();
            textOnly = textOnly.replaceAll("[^a-zA-Z]", " ").toLowerCase();
        }else {
            textOnly = "";
        }

        return textOnly;
    }




   //this method tokenizes the given query
   public static String[] tokenizeQuery(String query, boolean blankQuery){
        String[] queryTerms;
        if(!blankQuery){
            queryTerms = query.split("[^a-zA-Z]");
            for(int i = 0; i < queryTerms.length; i++) {
                queryTerms[i] = tokenize(queryTerms[i]);
                if(isStopWord(queryTerms[i])){
                    queryTerms[i] = " ";
                }
            }
        }else{
            return null;
       }

        return queryTerms;

   }

   //This method is used to tokenize a given string
    public static String tokenize(String s){
        s = s.trim().replaceAll("\\p{Punct}"," ")
                .toLowerCase()
                .replaceAll("[^a-zA-Z]", " ");
      /*  String[] tokens = s.split(" ");
        if(tokens.length > 1){
            s = s.substring(0 ,s.indexOf(" "));
        }
        s = s.trim();
*/
        if(isStopWord(s))
            return " ";
        else{
             s = stemTheTerm(s);
            return s;
        }


    }

    //String is the term , Integer is the term frequency at the query
    //This method tokenizes a given query
    public static HashMap<String, Integer> tokenizeQuery(String query){
        query = query.trim();
        String[] queryTerms = query.split(" ");

        HashMap<String, Integer> tokenizedWords = new HashMap<>();
        for(int i = 0; i < queryTerms.length; i++){
            queryTerms[i] = tokenize(queryTerms[i]);

            if(!isStopWord(queryTerms[i])){
                if(!tokenizedWords.containsKey(queryTerms[i])){
                    tokenizedWords.put(queryTerms[i], 1);
                }else{
                    tokenizedWords.put(queryTerms[i], tokenizedWords.get(queryTerms[i]) + 1);
                }

            }

        }
        return tokenizedWords;
    }


    public static boolean isStopWord(String word){
       return stopwords.contains(word);
    }



  //This method is used to stem the term
   public static String stemTheTerm(String term){
        PorterStemmer porterStemmer = new PorterStemmer();
        return porterStemmer.stem(term);
   }

}
