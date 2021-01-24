package com.webApplication.Search_Engine;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Test {
   private static String parentDirectory = "Documents/";

    public static void main(String[] args) throws IOException {
        HashMap<Integer, Double> p = new HashMap<>();
        p.put(1, 2.0);
        p.put(2, 1.0);
        p.put(3, 4.0);
        System.out.println(p);

        Map<Integer, Double> sortedAccumulators = p.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println(sortedAccumulators);

    }
}
