package com.webApplication.Search_Engine;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class WebCrawlerHandler {

    //This method assigns in every thread a crawling job
    public static void startCrawling(String startPage, int numOfSitesToCrawl, int numOfThreads, boolean readFromStartPage){

        //Initialize the data for the crawler
        WebCrawler.initData(startPage, numOfSitesToCrawl, readFromStartPage, numOfThreads);
        //Create the threads that will process the crawler
        for(int i = 0; i < numOfThreads; i++){

            new Thread(new WebCrawler()).start();
        }
        while (!WebCrawler.stop){}

        //After the crawling session is over save the data the crawler has collected to
        //disk.

        //Save the links into a file
        FilesHandler.writeToFile(WebCrawler.getMarked(), readFromStartPage);

        //Save the contents of the links to separate files
        FilesHandler.writeDocsToFile(WebCrawler.getMarked(), readFromStartPage);

        //Build the Indexer
        InvertedIndex.BuildInvertedIndex_InMemory(readFromStartPage);


    }


    public static void main(String[] args) throws MalformedURLException {
        //1st argument represents the starting page link
        String startPage = args[0];

        //2nd argument represents the number of web pages to crawl
        int numOfSitesToCrawl = Integer.parseInt(args[1]);


        //3rd argument represents whether the crawler should start crawling from the beginning
        //if 0 then start crawling from the beginning
        //if 1 then start crawling from the last crawled site
        int choice = Integer.parseInt(args[2]);
        boolean readFromStartPage;
        if(choice == 0)
            readFromStartPage = true;
        else
            readFromStartPage = false;


        //4th argument represents the number of threads that will take part at the crawling session
         int numOfThreads = Integer.parseInt(args[3]);


         //Start crawling the web
         startCrawling(startPage, numOfSitesToCrawl, numOfThreads, readFromStartPage);




    }
}
