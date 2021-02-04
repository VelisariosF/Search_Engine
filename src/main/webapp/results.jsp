<%--
  This page is the page is where the results after the searching phase appear
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.PrintWriter" %><
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="server.jsp"%>
<html>
<head>
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
    ArrayList<String> topKDocsNames = (ArrayList<String>)session.getAttribute("topKDocsNames");
    if(docsChosen && topKDocsNames != null) {
        //print the results and give the choice for a feedback
        printWriter.println("<ul>");
        for (int i = 0; i < topKDocsNames.size(); i++) {
            printWriter.println("<li style=\"list-style:none;\"> " + topKDocsNames.get(i) + "</li>");
            printWriter.println("<hr style=\"width:50%; margin-right: 100%\">");
        }
        printWriter.println("</ul>");
%>
<form action="" method="POST">
    <p>Provide feedback?</p>
    <input type="radio" id="yes" name="userChoice" value="yes" required>
    <label> yes</label> <br>
    <input type="radio" id="no" name="userChoice" value="no" required>
    <label> no</label> <br>
    <input type="submit" name="povideFeedbackAnswer" value="submit answer">
</form>

<%} else if(!docsChosen) {
 //if any docs are chosen from the user the show this message%>

<h3>No documents are chosen</h3>
<a href="index.jsp">search from beginning</a> <br>
<a href="insertRelDocs.jsp">Choose relevant Documents</a>

<%}else if(topKDocsNames == null){

%>

<h3>No relevant documents</h3> <br>

<a href="index.jsp">search again</a>

<%}%>

</body>
</html>
