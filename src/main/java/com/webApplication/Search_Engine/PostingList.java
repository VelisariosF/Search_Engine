package com.webApplication.Search_Engine;

import java.io.Serializable;
import java.util.*;


import java.io.Serializable;
import java.util.*;

/**
 * This class implements a term's posting list
 */
public class PostingList implements Serializable {
    //This set contains the id's of the documents that contain current term
    private  HashSet<Integer> docsThatContainCurrentWord = new HashSet<>();
   private  int numOfDocsThatContainCurrentWord; //nt
    //This variable represents the position of the term in the lexicon
    //This helps retrieve the term from the lexicon by its position in the lexicon
    private int termPosInLexicon;

    //constructor that gets as a parameter the term's position in the lexicon
    public PostingList(int termPosInLexicon){
        this.termPosInLexicon = termPosInLexicon;
    }

    //1st Integer represents The document id
    //2nd Integer represents the term frequency in current doc
    private HashMap<Integer, Integer> docIdFtdPairs = new HashMap<>();

    //This method returns the number of documents that contain current term
   public int getNumOfDocsThatContainCurrentWord(){
       return numOfDocsThatContainCurrentWord;
   }

    //this sets the number of documents that contain current term
   public void setNumOfDocsThatContainCurrentWord(){
       numOfDocsThatContainCurrentWord = docsThatContainCurrentWord.size();
   }
   //This method adds a doc id that contains the current term
   public void addDocsIdThatContainsCurrentWord(int id){
       docsThatContainCurrentWord.add(id);
       numOfDocsThatContainCurrentWord = docsThatContainCurrentWord.size();
   }

   //This method returns the documents that contain the current term
   public HashSet<Integer> getDocsThatContainCurrentWord(){
       return docsThatContainCurrentWord;
   }

   //This returns the (docId, ftd) pairs that exist inside the term's posting list
    public HashMap<Integer, Integer> getDocIdFtdPairs() {
        return docIdFtdPairs;
    }

    //this method returns the position of the term in the lexicon
    public int getTermPosInLexicon() {
        return termPosInLexicon;
    }
}
