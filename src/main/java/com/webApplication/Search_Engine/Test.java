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
   private static String parentDirectory = FilesHandler.CRAWLED_SITES_FILE_PATH;

    public static void main(String[] args) throws IOException {
           try{
               BufferedWriter writer = new BufferedWriter(new FileWriter(parentDirectory));
               for(int i = 0; i < 6004; i++){
                   writer.write("document " + i + "\n");
               }
               writer.close();
           }catch (Exception e){
               e.printStackTrace();

           }
    }
}
