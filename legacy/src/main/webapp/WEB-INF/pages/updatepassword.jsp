<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/resources/css/login.css" />
<script
	src="${pageContext.request.contextPath }/resources/js/jquery.min.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${pageContext.request.contextPath }/resources/js/updatepassword.js"
	type="text/javascript" charset="utf-8"></script>
</head>
<body>

	<%@ include file="ul.jsp"%>
	<div class="box">
		<h2>修改密码</h2>
		<form
			action="${pageContext.request.contextPath}/users/updatepassword.do"
			method="post">
			<span style="color: red">${msg} </span>
			<div class="inputBox">
				<input type="text" id="username" name="username"
					value="${user.username}" required="required" /> <label>用户名</label>
			</div>
			<div class="inputBox">
				<input type="text" id="email" name="email" required="required" /> <label>邮箱</label>
			</div>
			<div class="inputBox">
				<input type="password" id="password" name="password"
					required="required" onblur="checkPassword()" /> <label>新密码</label>
			</div>
			<span id="uword"></span>
			<div align="center">
				<input type="submit" value="修改">
			</div>
		</form>
	</div>
</body>
</html>