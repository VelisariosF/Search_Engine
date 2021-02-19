<%--
  This page is the main page for searching

--%>
<%@ page import = " java.util.* " %>
<%@ page import="com.webApplication.Search_Engine.Tokenizer" %>
<%@ page import="java.io.*" %>
<%@ page import="com.webApplication.Search_Engine.*" %>
<%@ page import="javax.net.ssl.HandshakeCompletedEvent" %>
<%@ include file="server.jsp"%>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title>Title</title>
</head>
<body>


<form style="padding-top: 90px" action="" method="POST" >
    <input type="text" name="user_Input"  id ="user_Input" placeholder="search.." />
    <input type="number" name="topK" placeholder="k number"   min="0" required >
    <button type="submit" name="querySearched"  value="Search" ><i class="fa fa-search"></i></button>
</form>



</body>

</html>
