<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>首页</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/index.css" />
		<style type="text/css">
			li:nth-child(1)>a {
				width: 70%;
				font-size: 32px;
				background-color: rgba(109,76,61,0.8);
			}
		</style>
	</head>

	<body>
		<%@ include file="ul.jsp"%>
		<div id="notice">
			<h1>公告</h1>
			<p>暂无公告</p>
		</div>
	</body>

</html>