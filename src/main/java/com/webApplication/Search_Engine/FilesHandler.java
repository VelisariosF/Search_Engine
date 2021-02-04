package com.webApplication.Search_Engine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class is used for handling situations that engage file processing.
 */
public class FilesHandler {
    //TODO might change this comment when project is complete
    //Here put the path of the tomcat server bin
    protected static String filesPath = "/home/velisarios/Desktop/DATA/apache-tomcat-8.5.61/bin/SearchEngineData/";
    //TODO delete readFilePath when project is completed
    protected static String readFilePath ="Documents/";
    //TODO make private
    protected final static String CRAWLED_SITES_FILE_PATH = filesPath + "crawledSites.txt",
            METADATA_FILE_PATH = filesPath + "metaData.txt", INDEX_FILE_PATH = filesPath + "INDEX.dat";

    //This queue is used by the threads that write the content of the documents
    //to the files
    protected static LinkedBlockingQueue<String> markedLinksQueue = new LinkedBlockingQueue<>();

    //This is the directory that contains the documents of the collection
    //*in this directory put only documents of the collection
    protected static String  PARENT_DIRECTORY = filesPath + readFilePath;


    //This set is used to as a helper for the crawler in order to understand
    //which links have been crawled from a previous crawling session
    private static HashSet<String> helper = new HashSet<>();


    //This variable represents the document's id that was last parsed by the index
    //If index is not going to be rebuilt then we must insert terms that exist to the new documents
    //that were crawled (the last document and after)
    private static int lastParsedDocId = 0;

    //Number of threads that will be used to save the documents content in to the files
    private final static  int numOfThreads = 10;

    //This function is used to save the names of the crawled links to file
    public static void writeToFile(Set<String> crawledSites, boolean overwrite) {

        try {
            //if overwrite is true it means that a crawling session from the starting
            //page has started
            //if it is false it means that crawler has started crawling from the last
            //crawled site
            BufferedWriter writer = null;
            if(overwrite){
                 writer = new BufferedWriter(new FileWriter(CRAWLED_SITES_FILE_PATH, false));
            }else{
                 writer = new BufferedWriter(new FileWriter(CRAWLED_SITES_FILE_PATH, true));

            }

            Iterator<String> it = crawledSites.iterator();

            while(it.hasNext()) {
                writer.write(it.next() + "\n");
            }
            writer.close();



        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //This function is used to write the content of crawled documents to files
    public static void writeDocsToFile(Set<String> marked, boolean readFromStartPage){
        int numOfSites = marked.size();
        //if readFromStart page is false it means that the number of sites that
        //have been crawled are the ones that are already saved to the file +
        //the ones that were crawled in this session
        if(!readFromStartPage)
            numOfSites= FilesHandler.getNumOfDocsFromMetaDatFile() + marked.size();


        //initialize the data for the file handler job that saves
        //the contents the files
        FilesHandlerWriteDocsToFileJob.initData(marked, readFromStartPage);
        FilesHandlerWriteDocsToFileJob filesHandlerWriteDocsToFileJob = new FilesHandlerWriteDocsToFileJob();


        //create the threads for the file handler job
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        for(int i = 0; i < numOfThreads; i++){
            executorService.execute(filesHandlerWriteDocsToFileJob);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }


       //after this session is finished save the new number of docs to a file
       saveNumOfDocsToMetadataFile(numOfSites);


    }




    //This function initializes and returns the helper set
    //helper contains the names of the previously crawled sites
    public static HashSet<String> getHelper(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CRAWLED_SITES_FILE_PATH));
            String line;
            while((line = reader.readLine()) != null){
                if(!line.isBlank()){
                    helper.add(line);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return helper;
    }



    //This method is used to return the name of the last crawled site
    //This name is important of the user sets the crawler to start
    //crawling from the last crawled link
    public static String getLastCrawledSite() {
        String lastCrawledSite = null;
        if(new File(CRAWLED_SITES_FILE_PATH).exists()){
            try{

                BufferedReader in = new BufferedReader(new FileReader(CRAWLED_SITES_FILE_PATH));

                String currentLine;
                while((currentLine = in.readLine()) != null){
                    if(!currentLine.isBlank())
                        lastCrawledSite = currentLine;
                }

                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            lastCrawledSite = "";
        }

        return lastCrawledSite;
    }

    //This method returns the names(links) of the already crawled sites
    public static ArrayList<String> getCrawledSites(){
        ArrayList<String> crawledSites = new ArrayList<>();
        if(new File(CRAWLED_SITES_FILE_PATH).exists()){
            try{

                BufferedReader in = new BufferedReader(new FileReader(CRAWLED_SITES_FILE_PATH));

                String currentLine;
                while((currentLine = in.readLine()) != null) {
                    if (!currentLine.isBlank())
                        crawledSites.add(currentLine);
                }
                in.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return crawledSites;
    }


   
    //This method is used to save the number of all the sites that have already been crawled into
    //the meta data file.
    public static void saveNumOfDocsToMetadataFile(int numOfDocs){
        //save the last doc id in a variable because the metaData file will be overwritten
        int lastDocId = FilesHandler.getLastParsedDocId();
        String newValue = String.valueOf(numOfDocs);
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(METADATA_FILE_PATH));
                ){

            writer.write(newValue + "\n");
            writer.write(String.valueOf(lastDocId));

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //This function is used to return the number of sites tha were previously
    //crawled
    public static int getNumOfDocsFromMetaDatFile(){
        int numOfDocs = 0;
        if(new File(METADATA_FILE_PATH).exists()){
            try {
                numOfDocs = Integer.parseInt(Files.readAllLines(Paths.get(METADATA_FILE_PATH)).get(0));
            }catch (IOException e){
                e.printStackTrace();

            }
        }

        return numOfDocs;
    }



   //This method returns the names of the documents based on their ids
    public static ArrayList<String> getDocNamesBasedOnIds(ArrayList<Integer> docIds){
        ArrayList<String> docNames = new ArrayList<>(), allDocNames = getCrawledSites();

        for(int id : docIds){
            if(allDocNames.get(id) != null){
                docNames.add(allDocNames.get(id));
            }
        }
        return docNames;
    }

   /* public static ArrayList<Integer> getDocIdsBasedOnNames(String[] names){
        ArrayList<String> allDocsNames = getDocs();
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < names.length; i++){
            for(int j = 0; i < allDocsNames.size(); i++){
                if(names[i].equals(allDocsNames.get(i))){


                }
            }
        }

        return ids;
    }*/

    //This function is used to return the names of the documents that
    //are saved.
    public static ArrayList<String> getDocs() {
        Tokenizer.initStopWords();
        ArrayList<String> documents = new ArrayList<>();
        try{
            File file = new File(PARENT_DIRECTORY);
            File[] files = file.listFiles();
            if(file != null){
                //sort documents by their id (numerical order)
                Arrays.sort(files, new Comparator<File>(){
                    @Override
                    public int compare(File f1, File f2) {

                        String s1 = f1.getName().substring(0, f1.getName().indexOf("."));
                        String s2 = f2.getName().substring(0, f2.getName().indexOf("."));
                        return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
                    }
                });
            }



            for(int i = 0; i < files.length; i++) {
                if (files[i].isFile())
                {
                    documents.add(files[i].getName());
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }


        return documents;

    }

    //This method return the terms of a specific document
    public static ArrayList<String> getDocumentWords(String fileName) {

        ArrayList<String> docTerms = new ArrayList<>();
        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            String word;
            while(scanner.hasNext()){
                word = Tokenizer.tokenize(scanner.next());
                if(!word.equals(" ")){
                  docTerms.add(word);
               }
            }


            scanner.close();



        }catch (Exception e){
            e.printStackTrace();
        }
     return docTerms;

    }



    public static String getParentDirectory(){
        return PARENT_DIRECTORY;
    }


    public static void deleteAllFiles(){
        File directory = new File(PARENT_DIRECTORY);
      // Get all files in directory
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.delete()) {
                System.out.println("Failed to delete "+file);
            }
        }


    }

  //This method returns the topK documents links
  public static ArrayList<String> getTopKDocsLinks(ArrayList<Integer> topKDocsIds){
        ArrayList<String> topKDocsLinks = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(CRAWLED_SITES_FILE_PATH));
            String link = null;
            int k = 0;
            while ((link = reader.readLine()) != null && k < topKDocsIds.size()){
                topKDocsLinks.add(link);
                k++;
            }

            reader.close();

        }  catch (IOException e){
            e.printStackTrace();
        }


      return topKDocsLinks;
    }


    //This method writes the index object to file
    public static void saveIndexToFile(HashMap<String, PostingList> index){
        try{
            FileOutputStream fos = new FileOutputStream(INDEX_FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(index);
            oos.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //This method loads the index object from file
    public static HashMap<String, PostingList> loadIndexFromFile(){
        try {

            FileInputStream fileIn = new FileInputStream(INDEX_FILE_PATH);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            objectIn.close();
            return (HashMap<String, PostingList>) obj;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }


    //This returns the id of the document that was last parsed by the index.
    public static int getLastParsedDocId(){
        try{

           File metaDataFile = new File(METADATA_FILE_PATH);
           Scanner scanner = new Scanner(metaDataFile);
           while (scanner.hasNext()){
               lastParsedDocId = Integer.parseInt(scanner.next());
           }
            scanner.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        return lastParsedDocId;
    }

    //This method is used to save the id of the last parsed document to the metadata file
    public static void saveLastParsedDocIdToMetaDataFile(int docId){
        lastParsedDocId = docId;
        //save the number of documents in a variable because the metaData file will be overwritten
        Integer numOfDocs = getNumOfDocsFromMetaDatFile();
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(METADATA_FILE_PATH));
        ){
            writer.write(String.valueOf(numOfDocs) + "\n");
            writer.write(String.valueOf(lastParsedDocId));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}