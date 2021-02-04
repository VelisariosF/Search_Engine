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
    <title>Title</title>
</head>
<body>

<h3>Choose relevant documents:</h3>
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
    <input type="submit" name="relDocsChosen" value="Submit feedback"/>




</form>
</body>
</html>
