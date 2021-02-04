package com.webApplication.Search_Engine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class implements the calculation of the data for the terms of the index
 */
public class IndexCalcDataJob implements Runnable{
    //this is used to stop each thread when the data calculation of every document term is done
    protected static volatile boolean stop = false;

    @Override
    public void run() {
        //each thread inserts a term into the lexicon
            while (!stop){
                  calculateTermsData();
            }
    }

    private synchronized void calculateTermsData(){
        HashMap<Integer, Integer> data = null;
        String collectionTerm = null;
        int documentId = -1;
        //each thread retrieves a term from the queue
        synchronized (InvertedIndex.termDocIdPairs){
            if(InvertedIndex.termDocIdPairs.size() != 0 && InvertedIndex.termDocIdPairs.peek() != null){
                InvertedIndex.TermDocIdPair termDocIdPair = InvertedIndex.termDocIdPairs.poll();
                //get a term from the queue
                collectionTerm = termDocIdPair.term;
                //get the term's document id
                documentId = termDocIdPair.docId;
                if(InvertedIndex.invertedIndexData.get(collectionTerm).getDocIdFtdPairs() != null){
                    data = InvertedIndex.invertedIndexData.get(collectionTerm).getDocIdFtdPairs();

                }

            }else
                return;


        }

        if (data != null) {
            //if the terms posting list size is zero it means that this is the first time we
            //encountered this term so we add one to its appearances in this document
            if (data.size() == 0) {
                data.put(documentId, 1);
                if(InvertedIndex.termDocIdPairs.size() == 0)
                    stop = true;

            } else {
                //if the terms posting list size is not zero it means that we have found this term before
                if (data.get(documentId) != null) {
                    //if the (docId, ftd), with docId == documentId, exists then we add one to the term's
                    //appearances in this doc
                    int newApps = data.get(documentId) + 1;
                    data.replace(documentId, newApps);
                    if(InvertedIndex.termDocIdPairs.size() == 0)
                        stop = true;
                } else {
                    //if the (docId, ftd) with docId == documentId does not exist
                    //then we create a new (docId, ftd), with docId == documentId, pair
                    data.put(documentId, 1);
                    if(InvertedIndex.termDocIdPairs.size() == 0)
                        stop = true;
                }
            }


        } else {
            //do nothing
        }


    }




    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(int documentId, ArrayList<String> docTerms){
        //initialize stop variable to false
        stop = false;
        //clear the queue
        //InvertedIndex.termDocIdPairs.clear();
        //insert all the document's, with id == documentId, into the queue
        for(String collectionTerm : docTerms){

            InvertedIndex.termDocIdPairs.add(new InvertedIndex.TermDocIdPair(collectionTerm, documentId));
        }


    }

}
