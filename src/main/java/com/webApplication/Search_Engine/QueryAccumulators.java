package com.webApplication.Search_Engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This  class  implements a job for the query processor
 *  This specific job is used to calculate the accumulators for the query processor
 */
public class QueryAccumulators implements Runnable{


    protected static volatile boolean stop = false;

    @Override
    public void run() {

            while (!stop){
                calculateTheAccumulators();

            }
    }

    //This method is used by each thread in order to calculate the docsAccumulators
    public synchronized void calculateTheAccumulators(){

        //for every term of the Tq (unique query terms set)
        String queryTerm = null;
        //every thread retrieves a term from the queue
        synchronized (QueryProcessor.queryTermsQueue){
            if(QueryProcessor.queryTermsQueue.size() != 0 && QueryProcessor.queryTermsQueue.peek() != null){
                queryTerm = QueryProcessor.queryTermsQueue.poll();
            }else
                return;
        }


            //get the term
            //String queryTerm = queryTermEntry.getKey();
            //if query term exists in index
            if(QueryProcessor.indexData.containsKey(queryTerm)){
                //calculate its weight
                //get terms posting list
                PostingList termPostingList = QueryProcessor.indexData.get(queryTerm);
                //get the number of docs that contain this term
                int numOfDocsThatContainCurrentWord = termPostingList.getNumOfDocsThatContainCurrentWord();
                //calculate its idft
                double IDft = Math.log(1 + (QueryProcessor.NUMBER_OF_DOCUMENTS / numOfDocsThatContainCurrentWord));
                //term's weight in this query
                double Wtq = 0.0;
                //feedback is not provided then calculate its weight
                if(!QueryProcessor.feedBackProvided){
                    int termAppsInQuery;
                    synchronized (this){
                        //get the appearances of the term in the query
                        termAppsInQuery = QueryProcessor.queryTerms.get(queryTerm);
                    }

                    //calculate the tftq
                    double TFtq = 1 + Math.log(termAppsInQuery);
                    Wtq = TFtq * IDft;
                }else if(QueryProcessor.feedBackProvided){
                    //get terms position in the lexicon (this is used as a helper to get the value of the specific
                    //coordinate from the newQueryVector)
                    int termPosInLexicon = termPostingList.getTermPosInLexicon();
                    //if feedback is provided then get the term's weight from the new query vector
                    //TODO should query vector be the same a query vector?
                    Wtq = QueryProcessor.newQueryVector.get(termPosInLexicon);
                }
                synchronized (this){
                    //update the Lq
                    QueryProcessor.Lq = QueryProcessor.Lq + Math.pow(Wtq, 2);
                }

                for (HashMap.Entry<Integer, Integer> docIdFtdPairEntry : termPostingList.getDocIdFtdPairs().entrySet()) {
                    //for every postingListEntry = (d, Ftd)
                    //get the id of the document
                    int docId = docIdFtdPairEntry.getKey();
                    //get the term's appearances in this document
                    int ftd = docIdFtdPairEntry.getValue();
                    synchronized (this){
                        //if the accumulator for the document found in (d, Ftd) pair
                        // does not exist then create it
                        if (QueryProcessor.docsAccumulators.get(docId) == null) {
                            QueryProcessor.docsAccumulators.put(docId, 0.0);
                        }

                    }
                    //calculate TFtd = 1 + ln(ftd)
                    double TFtd = 1 + Math.log(ftd);

                    //calculate term's weight int the document
                    double Wtd = TFtd * IDft;
                    synchronized (this){
                        //update the Ld
                        QueryProcessor.Ld = QueryProcessor.Ld + Math.pow(Wtd, 2);

                        if(!stop){
                            //append the new Wtq * Wtd result to the specific accumulator
                            QueryProcessor.docsAccumulators.put(docId, QueryProcessor.docsAccumulators.get(docId) + (Wtq * Wtd));
                            if(QueryProcessor.queryTermsQueue.size() == 0){
                                stop = true;

                            }
                        }

                    }

                }

            } else{
                //if query term does not exists in index
                //then its weight is zero thus we don't have to add anything to the accumulator
            }



    }
    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(HashMap<String, Integer> queryTerms){
        stop = false;
        QueryProcessor.Lq = 0.0;
        QueryProcessor.Ld = 0.0;
        QueryProcessor.docsAccumulators.clear();
        QueryProcessor.queryTermsQueue.clear();
        for(String term : queryTerms.keySet()){
            QueryProcessor.queryTermsQueue.add(term);
        }
    }


    public static ConcurrentHashMap<Integer, Double> getDocsAccumulators() {
        return QueryProcessor.docsAccumulators;
    }
}
