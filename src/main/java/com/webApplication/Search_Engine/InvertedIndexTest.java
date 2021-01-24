package com.webApplication.Search_Engine;

public class InvertedIndexTest {

    public static void main(String[] args) {

        long startIndexing = System.nanoTime();
        InvertedIndex.BuildInvertedIndex_InMemory(true);
        long stopIndexing = System.nanoTime();
        System.out.println("It took : " + (double) (stopIndexing - startIndexing) / 1000000000 + "s");
        InvertedIndex.printData();
    }

}
