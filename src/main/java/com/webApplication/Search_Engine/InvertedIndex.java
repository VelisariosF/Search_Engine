package com.webApplication.Search_Engine;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the inverted index
 */
public class InvertedIndex  {
    /*
    * invertedIndexData is the index
    * String : term
    * PostingList: term's posting list
    * keySet(): lexicon
    * valueSet(): posting lists
    * */
    protected static HashMap<String, PostingList> invertedIndexData = new HashMap<>();

    //a thread safe queue, that contains all the terms of a specific document, used by threads
    protected static LinkedBlockingQueue<TermDocIdPair> termDocIdPairs = new LinkedBlockingQueue<>();

    //private static int postingListUpperBound;

    //number of all the documents of the collection
    private static int NUMBER_OF_DOCUMENTS;

    //number of threads that are used
    private static int numOfThreads = 3;

    //this represents of the id of the last document that the index read
    private static int lastParsedDocId;

    //document names
    private static ArrayList<String> documentNames;

    /*
    * BuildInvertedIndex-InMemory
    * rebuildIndex : if the crawler starts crawling from the beginning then we must rebuild the index
    * so in this case rebuildIndex == true.
    * lastDocIdRead : id of the lasted parsed document by the index
    *
     */
    public static void BuildInvertedIndex_InMemory(boolean rebuildIndex){
        //initialize the number of the already parsed documents
        documentNames = FilesHandler.getDocs();

        NUMBER_OF_DOCUMENTS = documentNames.size();


       // if readFromStartPage == true then build index from the beginning
        if(rebuildIndex){
            invertedIndexData.clear();
            lastParsedDocId = 0;
        } else {
            invertedIndexData = FilesHandler.loadIndexFromFile();
            lastParsedDocId = FilesHandler.getLastParsedDocId() + 1;
        }

        termDocIdPairs.clear();
        //apply two passes to the collection
        firstPass();
        termDocIdPairs.clear();

        secondPass();
        termDocIdPairs.clear();
        //After the second pass has been completed save the id of the last document parsed by the index.
        FilesHandler.saveLastParsedDocIdToMetaDataFile(lastParsedDocId);

        //when the index is built calculate the length of each document
        QueryProcessor.calculateLengthOfDocuments();

        //save the index data to the data file
        FilesHandler.saveIndexToFile(invertedIndexData);


    }

    //This method implements the first read of the collection
    private static void firstPass(){
        /*
         * 1st read of collection D.
         *
         */

        for(int i = lastParsedDocId; i < NUMBER_OF_DOCUMENTS; i++) {
            int documentId = i;

            if (FilesHandler.getDocumentWords(FilesHandler.getParentDirectory() + documentNames.get(documentId)).size() == 0) {
                continue;
            }
            //initialize the data for the job
                IndexInsertJob.initData(documentId, FilesHandler.getDocumentWords(FilesHandler.getParentDirectory() + documentNames.get(documentId)));
                //create an insertion job
                IndexInsertJob indexInsertJob = new IndexInsertJob();
                //create threads to process the job
                for (int j = 0; j < numOfThreads; j++) {
                    new Thread(indexInsertJob).start();
                }
                while (!IndexInsertJob.stop) {
                }

        }

    }

    //This method implements the second read of the collection
    private static void secondPass(){
        /*
        * 2nd pass of the collection where every (d,ftd) for every term is calculated
        * */
        int documentId = 0;
        for(int i = lastParsedDocId; i < NUMBER_OF_DOCUMENTS; i++) {
            documentId = i;
            if (FilesHandler.getDocumentWords(FilesHandler.getParentDirectory() + documentNames.get(documentId)).size() == 0) {
                continue;
            }
                //initialize the data for the job
                IndexCalcDataJob.initData(documentId, FilesHandler.getDocumentWords(FilesHandler.getParentDirectory() + documentNames.get(documentId)));
                IndexCalcDataJob.stop = false;
                //create a data calculation job

                IndexCalcDataJob indexCalcDataJob = new IndexCalcDataJob();
                //create threads to process the job
                for (int j = 0; j < numOfThreads; j++) {
                    new Thread(indexCalcDataJob).start();
                }
                while (!IndexCalcDataJob.stop) {
                }

            }


        //After the second pass has been completed save the id of the last document parsed by the index.
        lastParsedDocId = documentId;


    }



    public static HashMap<String, PostingList> getInvertedIndexData(){
        return  invertedIndexData;
    }


   //TODO delete afterwards
   public static void printData(){
       System.out.println("Lexicon               Posting List");

        for(Map.Entry<String, PostingList> entry : invertedIndexData.entrySet()){

           System.out.print(entry.getKey() + "            --------->  [" + entry.getValue().getNumOfDocsThatContainCurrentWord()
            + ":");
           PostingList postingList = entry.getValue();
           HashMap<Integer, Integer> data = postingList.getDocIdFtdPairs();
           for(Map.Entry<Integer, Integer> entry1 : data.entrySet()){
               System.out.print("(d" + (entry1.getKey()) + ", " + entry1.getValue() + ")");
           }
            System.out.println("]");


        }



   }

    public static void setInvertedIndexData(HashMap<String, PostingList> invertedIndexData) {
        InvertedIndex.invertedIndexData = invertedIndexData;
    }



    /**
     * This class implements  a pair (term, docId)
     * term : a collection term
     * docId: document that the term belongs to
     * This class is used by the index's threads
     */
    static class TermDocIdPair{
         int docId;
         String term;
        public TermDocIdPair(String term, int docId){
            this.term = term;
            this.docId = docId;
        }

        public int getDocId(){
            return this.docId;
        }

        public String getTerm(){
            return this.term;
        }
   }



}
