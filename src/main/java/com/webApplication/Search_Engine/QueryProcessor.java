package com.webApplication.Search_Engine;

import java.util.*;
import java.lang.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
// TODO Fix after this after html page is completed:
// After feedback results , if not a feedback is asked change the feedback variable to false

/**
 * This class implements the query processor
 */
public class QueryProcessor {
    //query : the initial query
    //newQuery : the new query after feedback
    protected static String query, newQuery;



    //The number of top k documents that the user asked for
    protected static int topK;

    //The dimension of the vectors.
    //The dimension is the number of the unique words (num of words contained to the lexicon)
    protected static int vectorDimensions = InvertedIndex.getInvertedIndexData().size();
    //Key is the doc Id
    //Value is the accumulator of the doc
    protected static final int NUMBER_OF_DOCUMENTS = FilesHandler.getDocs().size();

    /*
    * queryTerms : (String, Integer) pairs:
    * String : term
    * Integer : term's appearances in query
    * */

    protected static HashMap<String, Integer> queryTerms = new HashMap<>();


    //indexData : lexicon and posting lists
    protected static HashMap<String, PostingList> indexData = InvertedIndex.getInvertedIndexData();
    //accumulators used for the top k query
    protected static ConcurrentHashMap<Integer, Double> docsAccumulators = new ConcurrentHashMap<>();

    /*
    * topKdocsVectors : (Integer, ArrayList<Double>) pair where:
    * Integer : Document Id
    * ArrayList<Double> Document's vector
    * These vectors are the vectors of the top K docs that the search engine returned after the user's
    * query.
    * These vectors are calculated if the user asks for a feedback to their query
    * */
    protected static ConcurrentHashMap<Integer, ArrayList<Double>> topKdocsVectors = new ConcurrentHashMap<>();
    /*
     * docsVectors : (Integer, ArrayList<Double>) pair where:
     * Integer : Documnet Id
     * ArrayList<Double> Document's vector
     * These are vectors of the collection's documents
     * This HashMap contains the documents vectors that have been calculated so far from topKdocsVectors
     * and not all the collection's documents vectors.
     * */
    protected static HashMap<Integer, ArrayList<Double>> docsVectors = new HashMap<>();

    //Query's vector
    //TODO change to protected
    public static ArrayList<Double> queryVector = new ArrayList<>(vectorDimensions);

    //The new query vector that's calculated after the feedback
    protected static ArrayList<Double> newQueryVector = new ArrayList<>(vectorDimensions);

    //Constants that are used to the Rochio's formula to provide feedback
    private static final double A_feedBackConstant = 1.0, B_feedBackConstant = 0.5, C_feedBackConstant = 0.25;

    //The top K documents based on the user's query
    protected static ArrayList<Integer> topKDocs = new ArrayList<>(), prevTopKDocs = new ArrayList<>();

    // Lq : length of query
    // Ld : length of a specific document
    protected static double Lq = 0.0, Ld = 0.0;

    //This variable is used to stop the running threads
    protected static boolean feedBackProvided;

    public static int feedBackTimes = 0;

    protected static final int numOfThreads = 100;

    //This queue contains the terms of the query and is used by the treads
    //Each thread gets each term from this queue
    //Is used to calculate the accumulators of the documents
    protected static LinkedBlockingQueue<String> queryTermsQueue = new LinkedBlockingQueue<>();

    //Thread safe queue containing the id's of the top K documents
    //Is used to calculate the top K documents vectors
    protected static LinkedBlockingQueue<Integer> queueDocIds = new LinkedBlockingQueue<>();

    //Thread safe queue containing the terms of the lexicon
    //Is used to calculate the query vector
    protected static LinkedBlockingQueue<String> queueIndexTerms = new LinkedBlockingQueue<>();




    /*
     *Get Top-k-Inverted
     *This method is used to return the top K documents base on the user's query
     * */
    public static ArrayList<Integer> getTopKDocuments() {
            topKDocs.clear();
            docsAccumulators = calculateDocsAccumulators();
            Lq = Math.sqrt(Lq);
            Ld = Math.sqrt(Ld);

            for (int i : docsAccumulators.keySet()) {
                docsAccumulators.replace(i, docsAccumulators.get(i) / (Lq * Ld));
            }


            //Sort docsAccumulators in descending order by their value

            Map<Integer, Double> sortedAccumulators = docsAccumulators.entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

            ArrayList<Integer> sortedDocs = new ArrayList<>();

            for (Map.Entry<Integer, Double> entry : sortedAccumulators.entrySet()) {
                sortedDocs.add(entry.getKey());
            }


            //get the top k most similar documents with the query
            //sortedDocs.size() is the num of the most similar documents based on cosine similarity
            //this num might be less than topK num
           // in this case they are all returned

            if(sortedDocs.size() < topK){
                return sortedDocs;
            }

            //if sortedDocs.size() > topK then the top K are returned
             for (int i = 0; i < topK; i++) {

                topKDocs.add(sortedDocs.get(i));
            }


      return topKDocs;

    }

    //This method calculates  and returns the accumulators of the collection's documents
    public static ConcurrentHashMap<Integer, Double> calculateDocsAccumulators() {
        //initialize the data that are needed for the threads to function
        QueryAccumulators.initData(queryTerms);
        QueryAccumulators queryAccumulators = new QueryAccumulators();
        //create the threads and start processing
        for (int i = 0; i < numOfThreads; i++) {
            new Thread(queryAccumulators).start();
        }
        while (!QueryAccumulators.stop) {
        }

        return QueryAccumulators.getDocsAccumulators();
    }



    //if feedback is provided this is called
    public static void provideFeedBack(ArrayList<Integer> prevTopKDocs, ArrayList<Integer> relevantDocs){

        //calculate top K documents vectors
           calculateTopKdocsVectors(prevTopKDocs);
           //if feedback is provided for the first time then calculate the queryVector
           //if not it means that a previously newQueryVector is calculated and
          //assigned its values to the old one
           if(feedBackTimes == 0)
              calculateQueryVector(query);




        //TODO actions:
        // a)print the list of top K documents to the user...
        // b)ask user to choose the relevant docs...
        // c)somehow try getting an array that contains the id's of the relevant documents
        // *this should be done through an html page
       // ArrayList<Integer> relevantDocs = new ArrayList<>();//get them from the user


        ArrayList<Integer> irrelevantDocs = new ArrayList<>();
        //===============\\
        //split previous top K documents into two categories (relevant, irrelevant)
        //relevant are given by the user, thus we must find the irrelevant
        //parse every topK document
        for(int prevTopKDocId : prevTopKDocs){
            //if prevTopKdoc's id does not exist in relevantDocs
            //then add it to the irrelevant docs
            if(!relevantDocs.contains(prevTopKDocId)){
                irrelevantDocs.add(prevTopKDocId);
            }
        }

        //get top K docs based on users choice
        provideFeedBack2(relevantDocs, irrelevantDocs);
    }




    //calculate the new Query vector
    public static void provideFeedBack2(ArrayList<Integer> relevantDocs, ArrayList<Integer> irrelevantDocs) {
        ArrayList<ArrayList<Double>> relevantDocsVectors  = new ArrayList<>();
        ArrayList<ArrayList<Double>> irrelevantDocsVectors = new ArrayList<>();



        //append all relevant doc vectors into an array
        for(int relDocId: relevantDocs){
            ArrayList<Double> relDocVector = topKdocsVectors.get(relDocId);
            relevantDocsVectors.add(relDocVector);
        }

        //calculate v vector
        //v vector is the average vector of the sum of the relevant docs vectors
        ArrayList<Double> vVec;

        vVec = VectorHandler.addVecs(relevantDocsVectors);

        vVec = VectorHandler.vecDiv(vVec, relevantDocs.size());
        vVec = VectorHandler.vecMult(vVec, B_feedBackConstant);



        //append all irrelevant doc vectors into an array
        for(int irrelDocId: irrelevantDocs){
            ArrayList<Double> irrelDocVector = topKdocsVectors.get(irrelDocId);
            irrelevantDocsVectors.add(irrelDocVector);
        }



        //calculate v vector
        //u vector is the average vector of the sum of the irrelevant docs vectors
        ArrayList<Double> uVec;
        uVec = VectorHandler.addVecs(irrelevantDocsVectors);
        uVec = VectorHandler.vecDiv(uVec, irrelevantDocs.size());
        uVec = VectorHandler.vecMult(uVec, C_feedBackConstant);


        queryVector = VectorHandler.vecMult(queryVector, A_feedBackConstant);

        //Apply the Rochio formula
        //Qn' = a*Qn-1 + b*vVec - c*uVec
        //vVec - uVec

        //param false because we want to subtract the uVec for vVec
        vVec = VectorHandler.vecCalculator(vVec, uVec, false);

        //calculate new query vector
        newQueryVector = VectorHandler.vecCalculator(queryVector, vVec, true);

        //check all weights in the new query
        for(int i = 0; i < newQueryVector.size(); i++){
            //if any Wtq is negative then change it to zero
            if(newQueryVector.get(i) < 0.0){
                newQueryVector.set(i, 0.0);
            }
        }



        //get the query query in string form based on the new query vector
        newQuery = QueryHelper.constructQuery(newQueryVector);

        //assign the new data to the old ones

        query = newQuery;
        queryVector = newQueryVector;
        calcQueryTerms(query);
        setFeedBackProvided(true);
        feedBackTimes++;
        //get top K docs that based on cosine similarity
        //return getTopKDocuments();
    }

    //This method is used to calculate the top K documents vectors
    //using multiple threads
   public static void calculateTopKdocsVectors(ArrayList<Integer> prevTopKDocs){
       //if none vector of the documents has been calculated before then initialize the
       //docVectors in order to calculate some vectors
       if (QueryProcessor.docsVectors.size() == 0) {
           for (int i : prevTopKDocs) {
               QueryProcessor.docsVectors.put(i, new ArrayList<>());
           }
       }


       //initializing the queue with the top k documents id's
       QueryTopKVectorsJob.initData(prevTopKDocs);
       QueryTopKVectorsJob queryTopKVectorsJob = new QueryTopKVectorsJob();
       //creating threads to start calculating the top K docs
        for(int i = 0; i < numOfThreads; i++){
            new Thread(queryTopKVectorsJob).start();
        }
        while (!QueryTopKVectorsJob.stop){}

        topKdocsVectors = QueryTopKVectorsJob.getTopKdocsVectors();

   }


    /*
    * This function gets the query vector
    * */
    public static void calculateQueryVector(String query){
        if(!query.isBlank()){
            QueryVectorJob.initData(indexData);
            QueryVectorJob queryVectorJob = new QueryVectorJob();
            for(int i = 0; i < numOfThreads; i++){
                new Thread(queryVectorJob).start();
            }
            while (!QueryVectorJob.stop){}

            queryVector = QueryVectorJob.getQueryVector();


        }

    }





  public ArrayList<Double> getQueryVector(){
        return queryVector;
  }


    //An query is not acceptable if is blank or if any of its terms do not exist in index
    //if the query is not acceptable then the topK docs will be none.
    public static boolean queryIsAcceptable(String query) {
        String[] queryTerms = null;
        if(query != null){
            queryTerms = Tokenizer.tokenizeQuery(query, query.isBlank());
        }else {
            return false;
        }


        if(queryTerms == null){
            //query's not acceptable
            return false;
        }else{
          for(int i = 0; i < queryTerms.length; i++){
              //if the index contains at least one query term then the query is acceptable
              if(indexData.containsKey(queryTerms[i])){
                  return true;
              }

          }

        }

        return false;
    }

    //This method sets the feedback provided to true if a feedback is provided
    public static void setFeedBackProvided(boolean feedBackProvided){
        QueryProcessor.feedBackProvided = feedBackProvided;
    }
   //This method sets the data that are needed for the query processor to function
    public static void setQueryProcessorData(String query, int topK, boolean feedBackProvided){
        QueryProcessor.query = query;
        QueryProcessor.topK = topK;
        QueryProcessor.feedBackProvided = feedBackProvided;
        newQuery = new String();
        docsAccumulators.clear();
        queryTerms.clear();
        queryVector.clear();
        newQueryVector.clear();
        topKdocsVectors.clear();
        topKDocs.clear();
        Lq = 0.0;
        Ld = 0.0;
        //calculate queryTermsQueue
        calcQueryTerms(query);
        QueryHelper.constructQueryPosTermPair(indexData);
    }


    //This is used to initialize the queue that the query terms are added to
    public static void calcQueryTerms(String query){
        //get the pairs (term, ftq)
        //term: a term in the query
        //ftq : term's appearances in query
        queryTerms = Tokenizer.tokenizeQuery(query);
    }





}

