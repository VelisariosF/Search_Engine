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
    <title>Title</title>
</head>
<body>


<form action="" method="POST" >
    <input type="text" name="user_Input"  id ="user_Input" />
    <input type="submit" name="querySearched"  value="Search"/>
</form>

</body>

</html>
