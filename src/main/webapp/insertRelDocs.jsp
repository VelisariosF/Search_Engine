<%--
  This page is for the insertion of the relevant docs by the user in case
  they chose to provide feedback
--%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="server.jsp"%>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="index2.css">
    <title>Title</title>
</head>
<body>

<h3 style="margin-left: 39%; text-decoration: underline">Choose relevant documents</h3>
<form action="" method="POST" >
    <%
  //get the top k docs from the session
        ArrayList<Integer> topKDocsIds = (ArrayList<Integer>)session.getAttribute("topKDocsIds");
        ArrayList<String> topKDocsNames = (ArrayList<String>) session.getAttribute("topKDocsNames");
     //print them on screen and let the user chose relevant documents
        for(int i = 0; i < topKDocsNames.size(); i++){
    %>
         <input type="checkbox" name="topKDocsNames" value="<%=topKDocsIds.get(i)%>"/>
           <label><%=topKDocsNames.get(i)%> </label><br>
    <%  } %>
    <button style="margin-top: 10px" type="submit" name="relDocsChosen" value="Submit feedback">Submit feedback</button>




</form>
</body>
</html>
