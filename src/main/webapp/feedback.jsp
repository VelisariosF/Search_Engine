<%--
  Created by IntelliJ IDEA.
  User: velisarios
  Date: 12/1/21
  Time: 1:35 μ.μ.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import = " java.util.* " %>
<%@ page import="com.webApplication.Search_Engine.*" %>
<%@ page import="java.io.PrintWriter" %>
<%
    //do them in server
    PrintWriter printWriter = response.getWriter();
   String answer1 = request.getParameter("myradio");
     if(answer1.equals("yes")){
//response.setHeader("Location", "index.jsp");

printWriter.print("Insert relevant ids seperated by space: <input type=\"text\" name=\"relIds\" id =\"relIds\" required/>");
ArrayList<Integer> topK = (ArrayList<Integer>) session.getAttribute("topK");
String best = request.getParameter("relIds");
String[] bestID = best.split(" ");
ArrayList<Integer> relevantDocs = new ArrayList<>();
for(int i = 0; i < bestID.length; i++){
relevantDocs.add(Integer.parseInt(bestID[i]));

}

QueryProcessor.provideFeedBack(topK, relevantDocs);
topK = QueryProcessor.getTopKDocuments();
if(topK.size() != 0){
ArrayList<String> names2 = FilesHandler.getDocNamesBasedOnIds(topK);

for(int i = 0; i < names2.size(); i++){
printWriter.println("<h1>"+ names2.get(i) + "</h1>");
}

}else
printWriter.println("empty");
}else{
response.setHeader("Location", "index.jsp");
}








%>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
