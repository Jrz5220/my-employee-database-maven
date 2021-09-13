<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>

<html>
	<head>
			<meta charset="utf-8">
			<meta name="viewport" content="width=device-width, initial-scale=1.0">
			<title>Error Details</title>
			<link rel="preconnect" href="https://fonts.gstatic.com">
			<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
			<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
	</head>
	<body>
		<div class="error-page-container">
			<h2>An Error has Occurred - ${errorTitle}</h2>
			<hr>
			<br>
			<c:if test="${errorTitle == 'Exception Details'}">
				<div>Servlet Name: ${servletName}</div>
				<div>Exception Name: ${exceptionName}</div>
				<div>Requested URI: ${requestUri}</div>
				<div>Exception Message: ${exceptionMsg}</div>
			</c:if>
			<c:if test="${errorTitle == 'Error Details'}">
				<div>Status Code: ${statusCode}</div>
				<div>Requested URI: ${requestUri}</div>
			</c:if>
			<br>
			<hr>
			<br>
			<div>
				<a href="${pageContext.request.contextPath}/index.jsp" class="add-btn">Home</a>
			</div>
		</div>
	</body>
</html>