<%--
  Created by IntelliJ IDEA.
  User: velisarios
  Date: 12/1/21
  Time: 1:54 μ.μ.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import = " java.util.* " %>
<%@ page import="com.webApplication.Search_Engine.Tokenizer" %>
<%@ page import="java.io.*" %>
<%@ page import="com.webApplication.Search_Engine.*" %>

<%
/*
* This is the server. Server is responsible for every situation
*
* */

    //load the index in memory
    InvertedIndex.setInvertedIndexData(FilesHandler.loadIndexFromFile());
    //also load the lengths
    QueryProcessor.setDocIdLdPairs(FilesHandler.loadLengthsFromFile());
 //check if the searched button from the index.jsp is pressed
 if(request.getParameter("querySearched") != null) {
     //if its pressed get the user input from the input field
     String query = request.getParameter("user_Input");

     //get the number k
     int topK = Integer.parseInt(request.getParameter("topK"));
     if(QueryProcessor.queryIsAcceptable(query) && topK != 0){
            //set the data for the query processor
         QueryProcessor.setQueryProcessorData(query, topK, false);
         //get the top k documents
         ArrayList<Integer> topKDocsIds = QueryProcessor.getTopKDocuments();
         //get the urls
         ArrayList<String> topKDocsUrls = FilesHandler.getDocUrlsBasedOnIds(topKDocsIds);
         //get the titles
         ArrayList<String> topKDocsTitles = FilesHandler.getDocumentTitlesBasedOnIds(topKDocsIds);

         //save the results in this session
         session.setAttribute("indexData", InvertedIndex.getInvertedIndexData());
         session.setAttribute("topKDocsIds", topKDocsIds);
         session.setAttribute("topKDocsUrls", topKDocsUrls);
         session.setAttribute("topKDocsTitles", topKDocsTitles);
         session.setAttribute("topK", topK);
     } else{
         session.setAttribute("topKDocsUrls", null);
         session.setAttribute("topKDocsTitles", null);
    }
     request.setAttribute("querySearched", null);
     session.setAttribute("docsChosen", true);
     //redirect to the results page
     response.sendRedirect("results.jsp");

 }

    //check if the povideFeedbackAnswer button from the results.jsp is pressed
 if(request.getParameter("povideFeedbackAnswer") != null) {
       //get the users choice about the feedback
        String answer1 = request.getParameter("userChoice");


        if(answer1.equals("no")){
            //if the user did not want to give feedback redirect to the index page
            //for a new search
            response.sendRedirect("index.jsp");
        }else{
             //else redirect to the page from which the user will
            //choose the relevant documents
            response.sendRedirect("insertRelDocs.jsp");
        }
     request.setAttribute("povideFeedbackAnswer", null);
 }

    //check if the searched button from the index.jsp is pressed
 if(request.getParameter("relDocsChosen") != null){
     //get the docs chosen by the user
     String[] bestDocs = request.getParameterValues("topKDocsUrls");
     ArrayList<Integer> relDocsIds = new ArrayList<>();
     if (bestDocs != null && bestDocs.length != 0) {
        //if the user chose documents then get them
         for(int i = 0; i < bestDocs.length; i++){
             relDocsIds.add(Integer.parseInt(bestDocs[i]));
         }

         ArrayList<Integer> topKDocsIds = (ArrayList<Integer>) session.getAttribute("topKDocsIds");
         //provide feedback
         QueryProcessor.provideFeedBack(topKDocsIds, relDocsIds);
         topKDocsIds = QueryProcessor.getTopKDocuments();
         ArrayList<String> topKDocsUrls = FilesHandler.getDocUrlsBasedOnIds(topKDocsIds);

         ArrayList<String> topKDocsTitles = FilesHandler.getDocumentTitlesBasedOnIds(topKDocsIds);
         //save the results to this session
         session.setAttribute("topKDocsIds", topKDocsIds);
         session.setAttribute("topKDocsUrls", topKDocsUrls);
         session.setAttribute("topKDocsTitles", topKDocsTitles);
         session.setAttribute("docsChosen", true);
         response.sendRedirect("results.jsp");
     }else{
         //if the user chose none docs then get then redirect to the results page
         session.setAttribute("docsChosen", false);
         response.sendRedirect("results.jsp");
     }

     request.setAttribute("relDocsChosen", null);



 }






%>
