package com.webApplication.Search_Engine;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryTest {




    public static String getQuery(){
        String query = null;
        try{
            File file = new File("/home/velisarios/Desktop/DATA/apache-tomcat-8.5.61/bin/SearchEngineData/query.txt");
            Scanner s = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            int i = 0;

                while (s.hasNext()){
                    sb.append(s.next());
                    sb.append(" ");
                }

         query = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return query;
    }
    public static void main(String[] args){
        long startIndexing = System.nanoTime();
        InvertedIndex.BuildInvertedIndex_InMemory(true);
        FilesHandler.saveIndexToFile(InvertedIndex.invertedIndexData);
       // InvertedIndex.setInvertedIndexData(FilesHandler.loadIndexFromFile());
        long stopIndexing = System.nanoTime();
        System.out.println("It took : " +(double) (stopIndexing - startIndexing) / 1000000000 + "s");
        System.out.println("Give a query");
        ArrayList<Integer> topKDocs = null;
        String query = getQuery();

if(QueryProcessor.queryIsAcceptable(query)) {
    int c = 0;
    double timeTaken = 0.0, avgTime = 0.0;
    while (c < 1) {
        QueryProcessor.setQueryProcessorData(query, 3, false);
      //  System.out.println("Loop :" + c);

        //=====
        long startQueryProcessing = System.nanoTime();
        topKDocs = QueryProcessor.getTopKDocuments();
        long stopQueryProcessing = System.nanoTime();

   //     System.out.println(FilesHandler.getDocNamesBasedOnIds(topKDocs));
        System.out.println(topKDocs);
        //======
        System.out.println("It took : " + (double) (stopQueryProcessing - startQueryProcessing) / 1000000000 + "s");
        timeTaken = timeTaken + ((double) (stopQueryProcessing - startQueryProcessing) / 1000000000);
        c++;
    }
    avgTime = timeTaken / (double) c;
    double roundOfAvgTime = Math.round(avgTime * 100.0) / 100.0;
    System.out.println("Average time: " + roundOfAvgTime + "s");

}else{
    System.out.println("No relevant documents");
}
       /* System.out.println("Provide feedBack (y/n) ?");
        Scanner scanner1 = new Scanner(System.in);
        String answer1 = scanner1.nextLine();
        while (answer1.equals("y")){
            System.out.println("Choose the most relevant based on you preference");
            Scanner s = new Scanner(System.in);
            int answer = s.nextInt();
            ArrayList<Integer> relevantDocs = new ArrayList<>();
            relevantDocs.add(answer);

            QueryProcessor.provideFeedBack(topKDocs, relevantDocs);
            topKDocs = QueryProcessor.getTopKDocuments();
            System.out.println("New top docs");
            for(int i : topKDocs){
                System.out.println(i);

            }

            System.out.println("Provide feedBack (y/n) ?");
            s = new Scanner(System.in);
            answer1 = scanner1.nextLine();
        }


        int k = 0;
        while (k < 1000){

            ArrayList<Integer> relevantDocs = new ArrayList<>();
            if(topKDocs.size() != 0){
                relevantDocs.add(topKDocs.get(0));
            }
            QueryProcessor.provideFeedBack(topKDocs, relevantDocs);
            topKDocs = QueryProcessor.getTopKDocuments();
            if(topKDocs.size() != 0){

                System.out.println(topKDocs);
               // topKDocs.stream().forEach(id-> System.out.println(id));
            }else
                System.out.println("empty");
            k++;
        }*/



    }
}
