package com.webApplication.Search_Engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class is responsible for the calculation of the length of each document
 */
public class LengthOfDocsCalculator implements Runnable{
    protected static volatile boolean stop = false;


    @Override
    public void run() {
        while (!stop){
            calculateLengthOfDocs();
        }

    }
    //This method is used by the threads in order to caclculate the length of the documents
    public synchronized void calculateLengthOfDocs(){
        String lexiconTerm = null;
        //get a lexicon term from the queue
        synchronized (QueryHelper.indexTerms){
            if(QueryHelper.indexTerms.size() != 0 && QueryHelper.indexTerms.peek() != null){
                lexiconTerm = QueryHelper.indexTerms.poll();
            }else
                return;
        }
        //get the posting list of the lexicon term
        PostingList lexiconTermPostingList = InvertedIndex.invertedIndexData.get(lexiconTerm);

            int numOfDocsThatContainCurrentWord = lexiconTermPostingList.getNumOfDocsThatContainCurrentWord();
            double idft = Math.log(1 + (QueryProcessor.NUMBER_OF_DOCUMENTS / numOfDocsThatContainCurrentWord));
            HashMap<Integer, Integer> docIdFtdPairs = lexiconTermPostingList.getDocIdFtdPairs();
            for(Map.Entry<Integer, Integer> docIdFtdPair : docIdFtdPairs.entrySet()){

                int docId = docIdFtdPair.getKey();
                int ftd = docIdFtdPair.getValue();
                double TFtd = 1 + Math.log(ftd);
                double Wtd = TFtd * idft;
                double Ld = QueryProcessor.docIdLdPairs.get(docId);
                Ld = Ld + Math.pow(Wtd, 2);
                QueryProcessor.docIdLdPairs.replace(docId, Ld);
            }

        synchronized (this){
            if(QueryHelper.indexTerms.size() == 0){
                stop = true;
            }
        }

    }


    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(HashMap<String, PostingList> indexData){
        stop = false;
        QueryHelper.indexTerms.clear();
        for(String lexiconTerm : indexData.keySet()){
            QueryHelper.indexTerms.add(lexiconTerm);

        }

    }
}
