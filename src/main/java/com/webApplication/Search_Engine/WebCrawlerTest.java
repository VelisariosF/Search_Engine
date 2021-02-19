package com.webApplication.Search_Engine;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebCrawlerTest {
    private static String root = "https://en.wikipedia.org/wiki/World_War_II";
    private static int numOfSitesToCrawl = 3;
    private static boolean readFromStartPage = false;
    private static int numOfThreads = 10;
    private WebCrawler webCrawler = new WebCrawler();
    public static void startCrawling(){
        for(int i = 0; i < numOfThreads; i++){
            new Thread(new WebCrawler()).start();
        }
        while (!WebCrawler.stop){}

    }

    public static void startCrawlingWithExecutors(){
            ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

            for(int i = 0; i < numOfThreads; i++){
              executorService.execute(new WebCrawler());
            }


        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }

    }

    public static void main(String[] args){
        int c = 0;
        double timeTaken = 0.0, avgTime = 0.0;
        while(c < 1){
            System.out.println("Loop :" + c);

            WebCrawler.initData(root, numOfSitesToCrawl, readFromStartPage, numOfThreads);
            long startIndexing = System.nanoTime();
            startCrawling();
            long stopIndexing = System.nanoTime();
            System.out.println("It took : " +(double) (stopIndexing - startIndexing) / 1000000000 + "s");
            timeTaken = timeTaken +( (double) (stopIndexing - startIndexing)/ 1000000000);
            c++;
        }

        FilesHandler.writeToFile(WebCrawler.getMarked(), readFromStartPage);
        FilesHandler.writeDocsToFile(WebCrawler.getMarked(), readFromStartPage);
        //Build the Indexer
        InvertedIndex.BuildInvertedIndex_InMemory(readFromStartPage);

        avgTime = timeTaken / (double) c;
        double roundOfAvgTime = Math.round(avgTime * 100.0) / 100.0;
        System.out.println("Average time: " + roundOfAvgTime + "s");


    }
}
