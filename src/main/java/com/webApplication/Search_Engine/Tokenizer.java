package com.webApplication.Search_Engine;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


import org.jsoup.*;

/**
 * This class implements the tokenizer
 */
public class Tokenizer {
    //this set contains the stopwords
    private static final HashSet<String> stopwords = new HashSet<>();
    //private static String[] queryTerms;
    private final static String STOPWORDS_FILE_PATH = "/home/velisarios/Desktop/DATA/apache-tomcat-8.5.61/bin/SearchEngineData/stopwords.txt";
    //This method is used to extract  and return the text from a web page source code
    public static String extractText(Reader reader) {
        StringBuilder sb = new StringBuilder();
        try{

            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String textOnly = null;
        if(sb != null){
            textOnly = Jsoup.parse(sb.toString()).text();
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
                queryTerms[i] = Tokenizer.tokenize(queryTerms[i]);
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
        s = s.replaceAll("[^a-zA-Z]", " ")
                .toLowerCase()
                .replace("\n", " ")
                .trim();

        if(isStopWord(s))
            return " ";
        else{
            //TODO stem the term(create a function called stemTheTerm)
            // s = stemTheTerm(s);
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
            queryTerms[i] = Tokenizer.tokenize(queryTerms[i]);

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
    //This method is used to initialize the stopWords set
    public static void initStopWords(){
        try{
            File file = new File(STOPWORDS_FILE_PATH);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()){
                stopwords.add(scanner.next());
            }
            scanner.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean isStopWord(String word){
       return stopwords.contains(word);
    }

    //TODO delete afterwards
    //this method returns the queue that are contained in the queryTerms pairs
   public static LinkedBlockingQueue<String> getQueueQueryTerms(HashMap<String, Integer> queryTerms){
       LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
       for(String term : queryTerms.keySet()){
           queue.add(term);
       }

       return queue;
   }


}
