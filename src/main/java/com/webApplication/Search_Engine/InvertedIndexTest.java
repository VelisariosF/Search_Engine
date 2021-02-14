package com.webApplication.Search_Engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InvertedIndexTest {

    public static void main(String[] args) {
        int k = 0;
        while (k < 1) {



        long startIndexing = System.nanoTime();
        InvertedIndex.BuildInvertedIndex_InMemory(true);
        long stopIndexing = System.nanoTime();
        System.out.println("loop: " + k + ", size: " + InvertedIndex.invertedIndexData.size());
        System.out.println("It took : " + (double) (stopIndexing - startIndexing) / 1000000000 + "s");
        k++;
    }
       // InvertedIndex.printData();
       FilesHandler.saveIndexToFile(InvertedIndex.invertedIndexData);









    }

}
