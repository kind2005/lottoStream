<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="kr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CalculateNum</title>
<link rel="stylesheet" href="/webjars/bootstrap/3.3.7-1/css/bootstrap.css"></link>
<script type="text/javascript" src="/webjars/jquery/3.1.0/jquery.js"></script>
<script type="text/javascript" src="/webjars/jquery-ui/1.12.0/jquery-ui.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/3.3.7-1/js/bootstrap.js"></script>
<style type="text/css">
div.circle-div{
    height:50px;
    width:50px;
    overflow:hidden;
    border-radius:50%;
    background-color:green;
}
.circle-div div{
    text-align: center;
    vertical-align: middle;
    padding: 15px;
}
</style>
</head>
<body>
<form:form name="param" commandName="paramVO" action="./analyzeNum1" method="post">
<%-- <form:form name="param" commandName="paramVO" action="./calcNum" method="post"> --%>
	<form:input path="startDrwNo" value="${not empty startDrwNo ? startDrwNo : 5}"/>~
	<form:input path="endDrwNo" />
	<form:checkbox path="bonusNoFlag"/><br/>
	selectNo : <form:input path="selectNo" /><br/>
	exceptNo : <form:input path="exceptNo" /><br/>
	exceptCnt : <form:input path="exceptCount" maxlength="1" value="${not empty exceptCount ? exceptCount : 5}"/><br/>
	<form:button>submit</form:button>
</form:form>
<div>
<c:out value="${step1List}"/><br/>
<c:out value="${step2List}"/><br/>
<c:out value="${step3List}"/><br/>
<c:out value="${step4List}"/><br/>
</div>
<div class="circle-div">
  <div>1</div>
</div>
</body>
</html>