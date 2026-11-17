<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登录</title>
<style type="text/css">
li:nth-child(5)>a {
	width: 70%;
	font-size: 32px;
	background-color: rgba(109, 76, 61, 0.8);
}
</style>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/resources/css/login.css" />
</head>
<body>
	<%@ include file="ul.jsp"%>
	<div id="loginpro">
		<span>如果没有帐号，请先<a
			href="${pageContext.request.contextPath }/register.do">注册</a>！
		</span>
	</div>
	<!-- 登录表单 -->
	<div class="box">
		<h2>登录</h2>
		<form id="login"
			action="${pageContext.request.contextPath}/users/login.do"
			method="post">
			<!-- 提示登陆信息 -->
			<span style="color: red">${msg }</span> <br> <br>
			<div class="inputBox">
				<input type="text" id="username" name="username" required="required"
					value="${user.username}"> <label>用户名：</label>
			</div>
			<div class="inputBox">
				<input type="password" id="password" name="password"
					required="required"> <label>密码</label>
			</div>
			<div align="center">
				<input type="submit" value="登录"> <a href="login.jsp"><input
					type="reset" value="重置"></a>
			</div>
		</form>
	</div>
</body>

</html>