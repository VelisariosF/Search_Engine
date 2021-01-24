<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.PrintWriter" %><%--
  Created by IntelliJ IDEA.
  User: velisarios
  Date: 12/1/21
  Time: 2:23 μ.μ.
  To change this template use File | Settings | File Templates.
--%>
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
    boolean docsChosen = (boolean) session.getAttribute("docsChosen");
    ArrayList<String> topKDocsNames = (ArrayList<String>)session.getAttribute("topKDocsNames");
    if(docsChosen) {
        printWriter.println("<ul>");
        for (int i = 0; i < topKDocsNames.size(); i++) {
            printWriter.println("<li style=\"list-style:none;\"> " + topKDocsNames.get(i) + "</li>");
            printWriter.println("<hr>");
        }
        printWriter.println("</ul>");
    %>
<form action="" method="POST">
    <p>Provide feedback?</p>
    <input type="radio" id="yes" name="userChoice" value="yes" required>
    <label> yes</label> <br>
    <input type="radio" id="no" name="userChoice" value="no" required>
    <label> no</label> <br>
    <input type="submit" name="povidedFeedbackAnswer" value="submit answer">

</form>

<%} else {%>
<h3>No docs are chosen</h3> <br>
<a href="index.jsp">search from beginning</a> <br>
<a href="insertRelDocs.jsp">Choose relevant Documents</a>

<%}%>
</body>
</html>
