package com.webApplication.Search_Engine;

import javax.management.ObjectName;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UI {
    private static String root = "https://en.wikipedia.org/wiki/World_War_II";
    private static int numOfSitesToCrawl = 3;
    private static boolean readFromStartPage = true;
    public static void showMenu(){
        System.out.println("Start searching from the beginning : press 1");
        System.out.println("Start searching from the last visited site : press 2");//does not work yet
        System.out.println("Run Query : press 3");
        System.out.println("Exit : press 4");
    }

    public static void main(String[] args){
        WebCrawler webCrawler = new WebCrawler();
        Scanner s = new Scanner(System.in);
        showMenu();
        int answer = s.nextInt();

        do{
            switch(answer){
                case 1:
                    FilesHandler.deleteAllFiles();
                    System.out.println("How many Threads?");
                    int numOfThreads = new Scanner(System.in).nextInt();

                      WebCrawler.initData(root, numOfSitesToCrawl, readFromStartPage, numOfThreads);
                      for(int i = 0; i < numOfThreads; i++){
                        new Thread(new WebCrawler()).start();
                     }
                    while (!WebCrawler.stop){}
                    FilesHandler.writeToFile(WebCrawler.getMarked(), readFromStartPage);
                    FilesHandler.writeDocsToFile(WebCrawler.getMarked(), readFromStartPage);
                    showMenu();
                    answer = s.nextInt();
                    break;
                case 2:

                    if(FilesHandler.getLastCrawledSite() == ""){
                        System.out.println("None crawled sites are found.");
                        showMenu();
                        answer = s.nextInt();
                    }else{

                       // FilesHandler.initHelper();
                        System.out.println("How many Threads?");
                        numOfThreads = new Scanner(System.in).nextInt();
                        WebCrawler.initData(root, numOfSitesToCrawl, false, numOfThreads);
                        for(int i = 0; i < numOfThreads; i++){
                            new Thread(new WebCrawler()).start();
                        }
                        while (!WebCrawler.stop){}
                        FilesHandler.writeToFile(WebCrawler.getMarked(), false);

                        FilesHandler.writeDocsToFile(WebCrawler.getMarked(), false);
                        showMenu();
                        answer = s.nextInt();
                    }

                    break;

                case 3:

                    InvertedIndex.BuildInvertedIndex_InMemory(true);
                    System.out.println("Give a query");
                    Scanner scanner = new Scanner(System.in);
                    String query = scanner.nextLine();
                    QueryProcessor.setQueryProcessorData(query, 10, false);
                    ArrayList<Integer> topKDocs = QueryProcessor.getTopKDocuments();
                    for(int i : topKDocs){
                        System.out.println(i);

                    }

                    System.out.println("Provide feedBack (y/n) ?");
                    Scanner scanner1 = new Scanner(System.in);
                    String answer1 = scanner1.nextLine();
                    while (answer1.equals("y")){
                        System.out.println("Write the idies of the docs that best match your opinion");
                        String best = scanner1.nextLine();
                        String[] bestID = best.split("");
                        ArrayList<Integer> relevantDocs = new ArrayList<>();
                        for(int i = 0; i < bestID.length; i++){
                            relevantDocs.add(Integer.parseInt(bestID[i]));

                        }
                        QueryProcessor.provideFeedBack(topKDocs, relevantDocs);
                        topKDocs = QueryProcessor.getTopKDocuments();
                      //  topKDocs =  queryProcessor.provideFeedBack(topKDocs, relevantDocs);
                        System.out.println("New top docs");
                        for(int i : topKDocs){
                            System.out.println(i);

                        }

                        System.out.println("Provide feedBack (y/n) ?");
                        scanner1 = new Scanner(System.in);
                        answer1 = scanner1.nextLine();
                    }

                    showMenu();
                    answer = new Scanner(System.in).nextInt();


                case 4:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Give a correct answer");
                    showMenu();
                    answer = new Scanner(System.in).nextInt();
            }



        }while(answer != 4);

    }
}
