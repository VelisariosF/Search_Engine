package com.webApplication.Search_Engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class implements a helper for the Query Processor
 */
public class QueryHelper {
    /*
    * queryPosTermPair represents a pair in form (termPosInLexicon, term)
    * it
    * termPosInLexicon : term's position in the lexicon
    * term : the specific term
    * this pair helps in retrieving the term of a specific vector coordinate from the lexicon
    * the term is actually retrieved from the queryPosTermPair but at the same time this term exist at the
    * lexicon position
    * queryPosTermPair is a replica of the lexicon
    * example:suppose a term is placed at pos = 3 in the lexicon
    * and  queryVec = (0, 3, 1, 4)
    * 0 : pos == 1, 3 : pos == 2, 1 : pos == 3, 4 : pos == 4
    * if we want to get the term,  from the lexicon,  with Wtq == 1 (3rd pos in vec)
    * we can cal queryPosTerm.get(2) which will return the term, that is placed at pos == 3 of the vector,
    * from the queryTermPair but the same term exists in the lexicon at pos == 3.
    *
    * */
    private static HashMap<Integer, String> queryPosTermPair = new HashMap<>();

   // private static int dimension = QueryProcessor.vectorDimensions;
    //This method is used to construct the queryPosTermPair hashMap based on the data
    //of the index
    public static void constructQueryPosTermPair(HashMap<String, PostingList> indexData){
        for(Map.Entry<String, PostingList> indexDataEntry : indexData.entrySet()){
            String term = indexDataEntry.getKey();
            PostingList termPostingList = indexDataEntry.getValue();
            int termPosInLexicon = termPostingList.getTermPosInLexicon();
            queryPosTermPair.put(termPosInLexicon, term);

        }

    }

    //This function gets a query vector and returns the query in String form
    public static String constructQuery(ArrayList<Double> queryVec){
        StringBuilder queryBuilder = new StringBuilder();
        for(int i = 0; i < queryVec.size(); i++){
            if(queryVec.get(i) > 0.0){
                //if this coordinate has Wtq > 0.0 this means, the term that belongs to this coordinate
                //must be present at the new query
                //thus we must retrieve it from the lexicon
                //but because we cant get it from the lexicon by its pos in O(1) time
                //we use the queryPosTermPair which returns a term by its pos in O(1) time
                String term = queryPosTermPair.get(i);
                queryBuilder.append(term);
                queryBuilder.append(" ");

            }
        }

        //getTopKDocuments(queryBuilder.toString(), topK, true);
        return queryBuilder.toString();
    }


}
