package com.webApplication.Search_Engine;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * This  class  implements a job for the query processor
 * This specific job is used to calculate the vectors of the top K documents for the query processor
 *
 */
public class QueryTopKVectorsJob implements Runnable{

    protected static volatile boolean stop = false;

    @Override
    public void run() {

            while (!stop){
                calculateTopKdocsVectors();
                Thread.yield();
            }


    }


    //This method is used by each thread in order to calculate the vectors of the
    //top k documents
    public synchronized void calculateTopKdocsVectors() {
        int topKdocId = -7;
        //take the id of a document in order to calculate its vector
        synchronized (QueryProcessor.queueDocIds) {
            if (QueryProcessor.queueDocIds.size() != 0 && QueryProcessor.queueDocIds.peek() != null) {

                topKdocId = QueryProcessor.queueDocIds.poll();


            } else
                return;
        }

            //=====================================================================================\\
            //*every time a topKdoc's vector is calculated


              //if topKdoc's vector has been calculated before then get it from the docsVectors HashMap
              //*the statement: QueryProcessor.docsVectors.get(topKdocId).size() != 0,  exists because at the beginning of
              //QueryProcessor.calculateTopKdocsVectors method the docsVectors is being initialized with
              //the top K docs's vectors and these vectors contain nothing even though the
              // QueryProcessor.docsVectors.containsKey(topKdocId) statement is true
           if (QueryProcessor.docsVectors.containsKey(topKdocId) && QueryProcessor.docsVectors.get(topKdocId).size() != 0) {
                     synchronized (this){
                         QueryProcessor.topKdocsVectors.put(topKdocId, QueryProcessor.docsVectors.get(topKdocId));
                         if(QueryProcessor.queueDocIds.size() == 0){
                             stop = true;
                         }
                    }

                } else {

                    // if topKdoc's  vector hasn't been calculated before then calculate it
                    //for every term of the lexicon calculate its weight and append it to a specific coordinate
                    for (HashMap.Entry<String, PostingList> dataEntry : QueryProcessor.indexData.entrySet()) {
                        //get term's posting list
                        PostingList indexTermPostingList = dataEntry.getValue();

                        int termPosInLexicon = indexTermPostingList.getTermPosInLexicon();
                        //Wtd is the terms weight for the current topKDoc
                        double Wtd;
                        //===========\\
                        //if topKdoc's id exists in the term's posting list
                        //then this doc contains the term so we must calculate the term's weight in this doc
                        if (indexTermPostingList.getDocIdFtdPairs().containsKey(topKdocId)) {

                            //Calculation of Wtd for the term
                            //Get term's nt and calculate IDFt
                            int numOfDocsThatContainCurrentWord = indexTermPostingList.getNumOfDocsThatContainCurrentWord();
                            double IDFt = 1 + Math.log(QueryProcessor.NUMBER_OF_DOCUMENTS / numOfDocsThatContainCurrentWord);

                            //find pair (id, ftd) with id = topKdocId and get ftd
                            //calculate TFtd = 1 + ln(ftd)
                            double termAppsInDoc = indexTermPostingList.getDocIdFtdPairs().get(topKdocId);
                            double TFtd = 1 + Math.log(termAppsInDoc);
                            Wtd = TFtd * IDFt;


                        } else {
                            //if tpokdoc's id doesn't exist in the term's posting list
                            //it means that this doc does not contain this term
                            //thus the term's weight Wtd, for this doc, is zero.
                            Wtd = 0.0;
                        }

                        //---------  Update the topKdocsVectors  ------
                        //topKdocVectors.get(topKdocId) : is the vector that belongs to the document with
                        //id == topKdocId
                        //in the specific coordinate append the Wtd of the term we got from the lexicon
                        //*this coordinate belongs to the term we got from the lexicon
                      //  topKdocVectorsEntry.getValue().set(termPosInLexicon, Wtd);
                       synchronized (this){
                            QueryProcessor.topKdocsVectors.get(topKdocId).set(termPosInLexicon, Wtd);
                            if(QueryProcessor.queueDocIds.size() == 0){
                                stop = true;
                            }
                        }

                    }

                    //when all the coordinates of the top K doc vector are assigned with a value it means that the
                    //vector has been calculated
                    // then update docsVectors hashmap by adding a new vector belonged to the doc with id == topKdocId
                  //  QueryProcessor.docsVectors.put(topKdocId, topKdocVectorsEntry.getValue());
                    synchronized (this){
                        QueryProcessor.docsVectors.put(topKdocId, QueryProcessor.topKdocsVectors.get(topKdocId));
                    }


                }





    }

    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(ArrayList<Integer> topKDocs){
        QueryProcessor.prevTopKDocs = new ArrayList<>();
        QueryProcessor.topKdocsVectors.clear();
          QueryProcessor.queueDocIds.clear();
          stop = false;
          QueryProcessor.prevTopKDocs = topKDocs;

        //initialize topKdocsVectors in order to calculate the vectors of top K documents
        for (int id :  QueryProcessor.prevTopKDocs) {
            QueryProcessor.topKdocsVectors.put(id, new ArrayList<>(QueryProcessor.vectorDimensions));
            for (int j = 0; j < QueryProcessor.vectorDimensions; j++) {
                QueryProcessor.topKdocsVectors.get(id).add(0.0);
            }


        }

         for(int id :  QueryProcessor.prevTopKDocs){
             QueryProcessor. queueDocIds.add(id);

         }
    }

    //This returns the vectors after they are calculated
    public static ConcurrentHashMap<Integer, ArrayList<Double>> getTopKdocsVectors() {
        return  QueryProcessor.topKdocsVectors;
    }
}
