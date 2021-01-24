package com.webApplication.Search_Engine;

/*
 * This  class  implements a job for the query processor
 * This specific job is used to calculate the query vector for the query processor
 * */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This  class  implements a job for the query processor
 * This specific job is used to calculate the vector of the query for the query processor
 */
public class QueryVectorJob implements Runnable{

    protected static volatile boolean stop = false;

    @Override
    public void run() {

            while (!stop){
                calculateQueryVector();
                Thread.yield();
            }


    }

    //This method is used to calculate the queryVector
    //each thread calculates the weight of a query's term in the query
    public synchronized void calculateQueryVector(){

        String lexiconTerm = null;
        //get a lexicon term from the queue
        synchronized (QueryProcessor.queueIndexTerms){

            if(QueryProcessor.queueIndexTerms.size() != 0 && QueryProcessor.queueIndexTerms.peek() != null){
                lexiconTerm = QueryProcessor.queueIndexTerms.poll();
            }else
                return;
        }
            //get the posting list of the lexicon term
            PostingList lexiconTermPostingList = QueryProcessor.indexData.get(lexiconTerm);
            //get its position from the posting list
            int termsPosInLexicon = lexiconTermPostingList.getTermPosInLexicon();
            //terms weight in the query
            double Wtq;
            //if term exists in query
            if(QueryProcessor.queryTerms.containsKey(lexiconTerm)){
                //calculate term's weight in query
                //get nt
                int numOfDocsThatContainCurrentTerm = lexiconTermPostingList.getNumOfDocsThatContainCurrentWord();
                //get ftq : term's apps in query
                int termAppsInQuery = QueryProcessor.queryTerms.get(lexiconTerm);
                double TFtq = 1 + Math.log(termAppsInQuery);
                double IDft = Math.log(1 + (QueryProcessor.NUMBER_OF_DOCUMENTS / numOfDocsThatContainCurrentTerm));
                Wtq = TFtq * IDft;
            }else{
                //if term does not exist in query then add zero in that vector pos the Wtq == 0.0
                Wtq = 0.0;
            }

        synchronized (this){
            //update the query vector by adding the Wtq in the specific coordinate == termPosInLexicon
            // queryVector.add(Wtq);
            QueryProcessor.queryVector.set(termsPosInLexicon, Wtq);
            if(QueryProcessor.queueIndexTerms.size() == 0){
                stop = true;
            }
        }



    }


    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(HashMap<String, PostingList> indexData){
        stop = false;
        QueryProcessor.queryVector.clear();
        QueryProcessor.queueIndexTerms.clear();

        for(int i = 0 ; i < QueryProcessor.vectorDimensions; i++){

       QueryProcessor.queryVector.add(0.0);
        }

        for(String lexiconTerm : indexData.keySet()){
            QueryProcessor.queueIndexTerms.add(lexiconTerm);

        }
    }
    //This returns the vectors after they are calculated
    public static ArrayList<Double> getQueryVector() {
        return QueryProcessor.queryVector;
    }
}
