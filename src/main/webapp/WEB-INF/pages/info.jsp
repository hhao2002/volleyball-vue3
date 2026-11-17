<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>个人信息</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/resources/css/info.css">
</head>
<body>
	<%@ include file="ul.jsp"%>
	<script src="${pageContext.request.contextPath }/resources/js/info.js"
		type="text/javascript" charset="utf-8">
		
	</script>
	<p id="userid" style="display: none">${user.id}</p>
	<div class="manager">
		<div class="info">
			<table border="0">
				<tr>
					<th><p>用户名：</p></th>
					<td><p id="username">${user.username}</p></td>
					<td rowspan="3" class="app">
						<button id="ji" onclick="sendApp('记分员')">申请记分员</button>
						<button id="guan" onclick="sendApp('管理员')">申请管理员</button>
					</td>
				</tr>
				<tr>
					<th><p>邮箱：</p></th>
					<td><p>${user.email}</p></td>
				</tr>
				<tr>
					<th><p>权限：</p></th>
					<td><p id="role">${user.role}</p></td>
				</tr>
			</table>
		</div>

		<table border="1" class="infos">
			<thead>
				<tr>
					<td><span>申请人</span></td>
					<td><span>申请信息</span></td>
					<td><span>处理人</span></td>
					<td><span>申请时间</span></td>
					<td colspan="2"><span>状态</span></td>
				</tr>
			</thead>
			<tbody class="apps">
			</tbody>
		</table>

	</div>
</body>
</html>