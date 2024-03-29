package com.webApplication.Search_Engine;

import java.io.*;
import org.jsoup.Jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements a job for the file handler.
 * This job has to do with saving the contents of the documents to a file.
 */
public class FilesHandlerWriteDocsToFileJob implements Runnable{
    protected static volatile boolean stop;
    //This variable is used such that every thread knows which was the last document's contents
    //written to a file.
    private static int i;

    @Override
    public void run() {
            while (!stop){
                saveDocumentToFile();
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }



            }
    }

    //This method is used by each thread in order to save the contents to a file
    private synchronized void saveDocumentToFile(){
        String urlToCrawl = null;

        synchronized (FilesHandler.markedLinksQueue){
            //if the queue is empty it means that all the document's contents are saved to a file
            if(FilesHandler.markedLinksQueue.size() != 0 && FilesHandler.markedLinksQueue.peek() != null){
                //if it is not empty then get a link form the marked (crawled) sites
                //and save its contents to a file
                urlToCrawl = FilesHandler.markedLinksQueue.poll();
            }else
                return;
        }

       try(

        BufferedWriter bw = new BufferedWriter(new FileWriter(FilesHandler.PARENT_DIRECTORY + i + ".txt"));
       ) {

            System.out.println("Saving Contents of document " + i);
            bw.write(Tokenizer.extractText(new InputStreamReader(new URL(urlToCrawl).openStream()), i));
            bw.close();
            i++;
            if(FilesHandler.markedLinksQueue.size() == 0)
                stop = true;

       }catch (MalformedURLException e) {
           e.printStackTrace();

       }catch (IOException e1){
            e1.printStackTrace();
        }

    }


    //This method is used to initialize the data that are needed for the threads to function
    public static void initData(Set<String> marked, boolean readFromStartPage){
        Iterator<String> iterator = marked.iterator();

        FilesHandler.markedLinksQueue.clear();
        stop = false;
        //add in the queue all the marked links
        while (iterator.hasNext()){
            FilesHandler.markedLinksQueue.add(iterator.next());
        }
        FilesHandler.documentsTitles = FilesHandler.loadDocumentsTitles();
         if(readFromStartPage){
           FilesHandler.documentsTitles.clear();
         }

        i = readFromStartPage? 0: FilesHandler.getNumOfDocsFromMetaDatFile();
    }
}
