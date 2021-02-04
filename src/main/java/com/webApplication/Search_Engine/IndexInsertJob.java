package com.webApplication.Search_Engine;

import java.util.ArrayList;

/**
 *This class implements the insertion of the terms for the index
 */
public class IndexInsertJob implements Runnable {
    //this is used to stop each thread when the insertion every document terms is done
    protected static volatile boolean stop = false;

    //this variable is the pos of each term in the lexicon
    private static volatile int termPos = 0;

    @Override
    public void run() {
        //each thread inserts a term into the lexicon
        while (!stop) {
                insertTerm();
        }
    }


    private synchronized void insertTerm() {
        String collectionTerm = null;
        int documentId = -1;
        //each thread retrieves a term from the queue
        synchronized (InvertedIndex.termDocIdPairs) {
            if (InvertedIndex.termDocIdPairs.size() != 0 && InvertedIndex.termDocIdPairs.peek() != null) {
                InvertedIndex.TermDocIdPair termDocIdPair = InvertedIndex.termDocIdPairs.poll();
                //get a term from the queue
                collectionTerm = termDocIdPair.term;
                //get the term's document id
                documentId = termDocIdPair.docId;
            } else
                return;

        }


        //if this term does not exist in the index
        if (!InvertedIndex.invertedIndexData.containsKey(collectionTerm)) {
            //  synchronized (this){
            //create pt
            PostingList currentPostingList = new PostingList(termPos);
            termPos++;
            //add this docId to its posting list data
            currentPostingList.addDocsIdThatContainsCurrentWord(documentId);
            //insert the terms into the index
            InvertedIndex.invertedIndexData.put(collectionTerm, currentPostingList);
            //set the number of documents that contain this current term
            InvertedIndex.invertedIndexData.get(collectionTerm).setNumOfDocsThatContainCurrentWord();
            //if there is no other term inside the queue it means that all the document's terms are inserted so
            // set stop to true
            if (InvertedIndex.termDocIdPairs.size() == 0) {
                stop = true;
            }
        } else {
            //if the term exists inside the index then add this document's id to its posting list
            InvertedIndex.invertedIndexData.get(collectionTerm).addDocsIdThatContainsCurrentWord(documentId);
            //and set the new number of documents that contain this term
            InvertedIndex.invertedIndexData.get(collectionTerm).setNumOfDocsThatContainCurrentWord();
            //if there is no other term inside the queue it means that all the document's terms are inserted so
            // set stop to true
            if (InvertedIndex.termDocIdPairs.size() == 0) {
                stop = true;
            }
        }
    }

    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(int documentId, ArrayList<String> docTerms) {
        //initialize stop variable to false
        stop = false;
        //clear the queue
        //InvertedIndex.termDocIdPairs.clear();
        //insert all the document's, with id == documentId, into the queue

        for (String collectionTerm : docTerms) {
            InvertedIndex.termDocIdPairs.add(new InvertedIndex.TermDocIdPair(collectionTerm, documentId));

        }


    }


}