<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="kr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>analyzeNumber</title>
<link rel="stylesheet" href="/webjars/bootstrap/3.3.7-1/css/bootstrap.css"></link>
<script type="text/javascript" src="/webjars/jquery/3.1.0/jquery.js"></script>
<script type="text/javascript" src="/webjars/jquery-ui/1.12.0/jquery-ui.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/3.3.7-1/js/bootstrap.js"></script>
<style type="text/css">
</style>
</head>
<body>
	<div class="page-header">
		<h1>1</h1>
	</div>
	<div>
		<ul>
			<c:forEach var="dwrt" items="${step1List}" varStatus="status">
			<li>
				<c:out value="${dwrt.key}"/>
				<c:forEach var="num" items="${dwrt.value}" varStatus="status">
					<c:out value="${num}"/>
				</c:forEach>
			</li>
			</c:forEach>
		</ul>
	</div>
	<div>
		<button type="button" class="btn btn-lg btn-default" onclick="location.href='./analyzeNum2';">다음</button>
	</div>
</body>
</html>