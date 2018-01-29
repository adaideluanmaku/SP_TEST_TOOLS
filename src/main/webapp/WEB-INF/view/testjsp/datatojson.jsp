<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body>
<h1>取数据库数据转换JSON串</h1>
<form action="${pageContext.request.contextPath}/All/datatojsontest" method="POST">
	visitcode:<input type="text" name="visitcode" value="${visitcode}">
	<input type="submit" value="提交">
	请输入数据库字段visitcode值
</form>
<c:out value="${result}"></c:out>
</body>
</html>