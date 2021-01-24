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
    //TODO index should be rebuilt once the crawler starts reading from the beginning

 if(request.getParameter("querySearched") != null) {

     String query = request.getParameter("user_Input");
     if(QueryProcessor.queryIsAcceptapble(query)){
         InvertedIndex.setInvertedIndexData(FilesHandler.loadIndexFromFile());
         QueryProcessor.setQueryProcessorData(query, 6, false);
         ArrayList<Integer> topKDocsIds = QueryProcessor.getTopKDocuments();
         ArrayList<String> topKDocsNames = FilesHandler.getDocNamesBasedOnIds(topKDocsIds);
         session.setAttribute("indexData", InvertedIndex.getInvertedIndexData());
         session.setAttribute("topKDocsIds", topKDocsIds);
         session.setAttribute("topKDocsNames", topKDocsNames);
         session.setAttribute("docsChosen", true);
         request.setAttribute("querySearched", null);
         response.sendRedirect("results.jsp");
     }else{

     }


 }


 if(request.getParameter("povidedFeedbackAnswer") != null) {
        String answer1 = request.getParameter("userChoice");


        if(answer1.equals("no")){

            response.sendRedirect("index.jsp");
        }else{

            response.sendRedirect("insertRelDocs.jsp");
        }
     request.setAttribute("povidedFeedbackAnswer", null);
 }


 if(request.getParameter("relDocsChosen") != null){

     String[] bestDocs = request.getParameterValues("topKDocsNames");
     ArrayList<Integer> relDocsIds = new ArrayList<>();
     if (bestDocs != null && bestDocs.length != 0) {

         for(int i = 0; i < bestDocs.length; i++){
             relDocsIds.add(Integer.parseInt(bestDocs[i]));
         }

         ArrayList<Integer> topKDocsIds = (ArrayList<Integer>) session.getAttribute("topKDocsIds");
         QueryProcessor.provideFeedBack(topKDocsIds, relDocsIds);
         topKDocsIds = QueryProcessor.getTopKDocuments();
         ArrayList<String> topKDocsNames = FilesHandler.getDocNamesBasedOnIds(topKDocsIds);
         session.setAttribute("topKDocsIds", topKDocsIds);
         session.setAttribute("topKDocsNames", topKDocsNames);
         session.setAttribute("docsChosen", true);
         response.sendRedirect("results.jsp");
     }else{
         session.setAttribute("docsChosen", false);
         response.sendRedirect("results.jsp");
     }

     request.setAttribute("relDocsChosen", null);



 }







%>
