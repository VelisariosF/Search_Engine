package com.webApplication.Search_Engine;

import org.jsoup.Jsoup;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This class implements the web crawler
 */
public class WebCrawler implements Runnable{
    //This queue contains the links of the crawled sites
    protected  static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    //This set contains the links of the crawled sites
    protected  static Set<String>  marked = new HashSet<>() ;

    //This represents a the content of the source code of a web page
    private String pageSourceCode = null;

    //This set contains the previously crawled links
    protected static HashSet<String> helper;

    //This variable represents the current crawled url
    protected  String crawledUrl = null;

    //This variable represents the url of the last crawled site from a previous crawling session
    private static String endPage;
    protected static int NUM_OF_SITES_TO_BE_CRAWLED;
    //this is used to know of if crawling should start from the beginning
    protected static boolean readFromStartPage;
    //this variable is used to know when the threads must stop crawling
    //crawling stops when the desired number of sites have been crawled
    protected static volatile boolean stop = false;
    //these variables represent the patterns of the tags that contain the hyperlinks to
    //other web pages
    private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_A_HREF_TAG_PATTERN =
            "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

    private static final Pattern patternTag = Pattern.compile(HTML_A_TAG_PATTERN),
            patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);

    //this reader is used to open a stream to the web site that is being crawled
    private BufferedReader br = null;
    //is used to get the contents of a web site line by line and build
    private StringBuilder sb = null;
    private Matcher matcherTag, matcherLink;



    /*
     * The crawler runs bfs algorithm
     * */
    @Override
    public void run() {
        while (!stop) {

                //get a link from the queue and start crawling the respective web site
                crawledUrl = queue.poll();
                 //TODO add thread.sleep to avoid ssl exception
            /*     try{
                     Thread.sleep(2000);
                 }catch (InterruptedException e){
                     e.printStackTrace();
                 }*/

                crawlBFS();

                Thread.yield();

        }


    }

    public synchronized void crawlBFS(){
        try {
           //start crawling

                br = new BufferedReader(new InputStreamReader(new URL(crawledUrl).openStream()));
                sb = new StringBuilder();
                while ((pageSourceCode = br.readLine()) != null) {
                    sb.append(pageSourceCode);
                }
                pageSourceCode = sb.toString();

                matcherTag = patternTag.matcher(pageSourceCode);
                //try to find patterns, that represent hyperlinks, inside the web
                //page source code
                while (matcherTag.find()) {
                    String href = matcherTag.group(1); // href

                    matcherLink = patternLink.matcher(href);

                    while (matcherLink.find()) {
                        //when you find them check if the link contained has been crawled before
                        //if not then add it to the queue for later crawling
                        String link = matcherLink.group(1); // link
                        link = HtmlLink.replaceInvalidChar(link);
                        if (!queue.contains(link))
                            queue.add(link);
                    }
                }




            br.close();


            //if the crawler started from the last crawled page then we must check if the crawled
            //web page was crawled in a previous crawling session
            if (!readFromStartPage) {
                //if started from the last crawled page then check if the web page link is
                //equal to the last crawled page link(endPage) and also check if it is contained to
                //the helper set
                //helper set contains the links of all the previous crawling sessions
                if (!crawledUrl.equals(endPage) && !helper.contains(crawledUrl) && !stop) {
                    marked.add(crawledUrl);


                    System.out.println("Crawling: " + crawledUrl);
                    if(marked.size() == NUM_OF_SITES_TO_BE_CRAWLED) {
                        stop = true;
                    }

                }
            }else {
                if(!stop){
                    marked.add(crawledUrl);


                    System.out.println("Crawling: " + crawledUrl);
                    if(marked.size() == NUM_OF_SITES_TO_BE_CRAWLED) {
                        stop = true;
                    }
                }
            }




        } catch (MalformedURLException e) {
            //if the url could not be crawled get another url from the queue
            crawledUrl = queue.poll();

        } catch (IOException e) {
            //if the url could not be crawled get another url from the queue
            crawledUrl = queue.poll();

        }

    }








    public static Set<String> getMarked() {
        return marked;
    }
   //This method initializes the data for the crawler
    public  static void initData(String root, int numOfSitesToCrawl, boolean readFromStartPage, int threads){
        WebCrawler.stop = false;


        WebCrawler.readFromStartPage = readFromStartPage;
        WebCrawler.NUM_OF_SITES_TO_BE_CRAWLED = numOfSitesToCrawl;
        if(!readFromStartPage){
            helper = FilesHandler.getHelper();
            if(FilesHandler.getLastCrawledSite() == ""){
                endPage = "";
            }else {
                endPage = FilesHandler.getLastCrawledSite();
            }
        }else {
            FilesHandler.deleteAllFiles();
        }



        queue.clear();
        queue.add(root);
        marked.clear();

    }
}