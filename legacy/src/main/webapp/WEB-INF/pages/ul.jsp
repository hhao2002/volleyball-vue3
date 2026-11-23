<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath }/resources/css/ul.css" />
<script
	src="${pageContext.request.contextPath }/resources/js/jquery.min.js"
	type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath }/resources/js/alert.js"
	type="text/javascript" charset="utf-8"></script>
<ul>
	<li><a class="ul-a"
		href="${pageContext.request.contextPath }/index.do">首页</a></li>
	<li><a class="ul-a"
		href="${pageContext.request.contextPath }/matchs.do">赛事</a></li>
	<li><a class="ul-a"
		href="${pageContext.request.contextPath }/recording.do">记录比赛</a></li>
	<li><a class="ul-a"
		href="${pageContext.request.contextPath }/statistics.do">统计</a></li>
	<li><a class="ul-a"
		href="${pageContext.request.contextPath }/login.do">登录</a></li>
	<li><a class="ul-a longa"
		href="${pageContext.request.contextPath }/info.do">个人信息</a></li>
</ul>
