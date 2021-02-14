<%--
  This page is the page is where the results after the searching phase appear
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="server.jsp"%>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="index.css">
    <title>Title</title>
</head>
<body>


<%
    PrintWriter printWriter = response.getWriter();
    //this variable is used to check if the user chose relevant documents
    // after the feedback
    //at first a feedback is not provided so the server sets this to true in order to
    //show results from the first search
    boolean docsChosen = (boolean) session.getAttribute("docsChosen");
    //get the urls from the session
    ArrayList<String> topKDocsUrls = (ArrayList<String>)session.getAttribute("topKDocsUrls");
    //get the titles from the session
    ArrayList<String> topKDocsTitles = (ArrayList<String>) session.getAttribute("topKDocsTitles");

    ArrayList<Integer> topKDocsIds = (ArrayList<Integer>) session.getAttribute("topKDocsIds");
    if(docsChosen && topKDocsUrls != null) {
        //print the results and give the choice for a feedback
        printWriter.println("<ul>");
        for (int i = 0; i < topKDocsTitles.size(); i++) {
            printWriter.println("<li style=\"list-style:none;\"> " + topKDocsTitles.get(i) + "</li><br>");
            printWriter.println("<li style=\"list-style:none;\"> " + topKDocsUrls.get(i)  + "</li>");

            printWriter.println("<hr>");
        }
        printWriter.println("</ul>");
%>


<form action="" method="POST">
    <p>Provide feedback?</p>
    <input  type="radio" id="yes" name="userChoice" value="yes" required>
    <label> yes</label>
    <input  type="radio" id="no" name="userChoice" value="no" required>
    <label> no</label> <br>
    <button style="margin-top: 5px" type="submit" name="povideFeedbackAnswer" value="submit answer">submit answer</button>
</form>

<%} else if(!docsChosen) {
 //if any docs are chosen from the user the show this message%>
<div class="emptyResults">
    <h3 style="text-decoration: underline">No documents are chosen</h3>

    <button style="margin-left: -6%" onclick="window.location.href='insertRelDocs.jsp'">Choose relevant Documents</button>
    <button onclick="window.location.href='index.jsp'">search again</button>


</div>


<%}else if(topKDocsUrls == null){

%>
<div class="emptyResults">
    <h3 style="text-decoration: underline">No relevant documents</h3> </br>

    <button onclick="window.location.href='index.jsp'">search again</button>

</div>


<%}%>

</body>
</html>
