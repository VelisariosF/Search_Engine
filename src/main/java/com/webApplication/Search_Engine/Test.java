package com.webApplication.Search_Engine;
import java.awt.*;
import java.io.*;
import java.lang.ProcessBuilder;
import java.lang.module.ModuleDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.lucene.*;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class Test {
   private static String parentDirectory = FilesHandler.CRAWLED_SITES_FILE_PATH;

    public static void main(String[] args) throws IOException {
         /*  try{
               BufferedWriter writer = new BufferedWriter(new FileWriter(parentDirectory));
               for(int i = 0; i < 6004; i++){
                   writer.write("document " + i + "\n");
               }
               writer.close();
           }catch (Exception e){
               e.printStackTrace();

           }*/

        String s = "life's \n" +
                "\"life's best gift is how we're gonna do this'\n" +
                "\"129#_9E_@()($0329e932- they don't know";
        String[] strings = s.split(" ");
        for(String string : strings){
            System.out.println(Tokenizer.tokenize(string));
        }


    }
}
