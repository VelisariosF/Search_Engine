<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: velisarios
  Date: 12/1/21
  Time: 1:36 μ.μ.
  To change this template use File | Settings | File Templates.
--%>
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

        ArrayList<Integer> topKDocsIds = (ArrayList<Integer>)session.getAttribute("topKDocsIds");
        ArrayList<String> topKDocsNames = (ArrayList<String>) session.getAttribute("topKDocsNames");

        for(int i = 0; i < topKDocsNames.size(); i++){
    %>
         <input type="checkbox" name="topKDocsNames" value="<%=topKDocsIds.get(i)%>"/>
           <label><%=topKDocsNames.get(i)%> </label><br>
    <%  } %>
    <input type="submit" name="relDocsChosen" value="Submit feedback"/>




</form>
</body>
</html>
