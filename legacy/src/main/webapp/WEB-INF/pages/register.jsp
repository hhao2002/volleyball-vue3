<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注册</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/resources/css/register.css" />
</head>
<body>
	<%@ include file="ul.jsp"%>
	<!-- 注册表单 -->
	<div id="loginDiv">

		<form id="register"
			action="${pageContext.request.contextPath}/users/register.do"
			method="post">
			<a href="${pageContext.request.contextPath}/login.do">返回登录</a>
			<h1>注册</h1>
			<span style="color: red">${waitApply}</span>
			<table border="0">
				<tr>
					<td>
						<p>用&nbsp;&nbsp;户&nbsp;&nbsp;名:</p>
					</td>
					<td><input id="username" name="username" type="text" autofocus
						required onblur="checkUsername()" /></td>
					<td><span class="uname"></span></td>
				</tr>
				<tr>
					<td />
					<td colspan="2"><span class="uname"></span></td>
				</tr>
				<tr>
					<td>
						<p>密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码:</p>
					</td>
					<td><input id="password" name="password" type="password"
						required onblur="checkPassword()" /></td>
					<td><span class="uword"></span></td>
				</tr>
				<tr>
					<td />
					<td colspan="2"><span class="uword"></span></td>
				</tr>
				<tr>
					<td><p>确认密码:</p></td>
					<td><input id="repassword" name="repassword" type="password"
						required onblur="nextcheckPassWord()" /></td>
					<td><span class="uword1"></span></td>
				</tr>
				<tr>
					<td />
					<td colspan="2"><span class="uword1"></span></td>
				</tr>
				<tr>
					<td><p>电子邮箱:</p></td>
					<td><input id="email" name="email" type="text" required
						onblur="checkEmail()" /></td>
					<td><span class="Temail"></span></td>
				</tr>
				<tr>
					<td />
					<td colspan="2"><span class="Temail"></span></td>
				</tr>
				<tr>
					<td><p>用户角色:</p></td>
					<td><select id="role" name="role">
							<option>用户</option>
							<option>记分员</option>
							<option>管理员</option>
					</select></td>
				</tr>

			</table>

			<p style="text-align: center;">
				<input type="submit" class="button" value="注册"> <input
					type="reset" class="button" value="重置">
			</p>

		</form>
	</div>
</body>
<script
	src="${pageContext.request.contextPath }/resources/js/register.js"
	type="text/javascript" charset="utf-8"></script>
</html>