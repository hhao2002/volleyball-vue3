<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>统计</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath }/resources/css/statistics.css">
</head>

<body>
	<%@ include file="ul.jsp"%>
	<script
		src="${pageContext.request.contextPath }/resources/js/statistics.js"
		type="text/javascript" charset="utf-8">
		
	</script>
	<p class="none">没有选择比赛，请到赛事页面选取已结束的比赛</p>
	<div class="auto">
		<div id="playBtn" class="circle" style="margin: 20px 0px 10px 20px;">
			<div class="circle_inner_play"></div>
		</div>

		<div id="pauseBtn" class="circle" style="margin: 20px 0px 10px 85px;">
			<div class="circle_inner_pause">▐▐</div>
		</div>
	</div>
	<div class="main">
		<div class="t1">
			<h1>队伍1</h1>
			<table class="info">
				<tr>
					<th>队伍名称</th>
					<td class="name"></td>
				</tr>
				<tr>
					<th>教练名称</th>
					<td class="coach"></td>
				</tr>
				<tr>
					<table class="mem">
						<thead>
							<tr>
								<th>队员名称</th>
								<th>主打位置</th>
							</tr>
						</thead>
						<tbody class="info"></tbody>
					</table>
				</tr>
			</table>
		</div>
		<div class="match">
			<h1>赛事信息</h1>
			<table class="info">
				<tr>
					<th>赛事名称</th>
					<td class="mname"></td>
				</tr>
				<tr>
					<th>举办方</th>
					<td class="sponsor"></td>
				</tr>
				<tr>
					<th>规则方式</th>
					<td class="rules"></td>
				</tr>
				<tr>
					<th>开始时间</th>
					<td class="start"></td>
				</tr>
				<tr>
					<th>结束时间</th>
					<td class="end"></td>
				</tr>
				<tr>
					<th>赛事状态</th>
					<td class="state"></td>
				</tr>
				<tr>
					<th class="s1"></th>
					<th class="s2"></th>
				</tr>
			</table>
		</div>
		<div class="t2">
			<h1>队伍2</h1>
			<table class="info">
				<tr>
					<th>队伍名称</th>
					<td class="name"></td>
				</tr>
				<tr>
					<th>教练名称</th>
					<td class="coach"></td>
				</tr>
				<tr>
					<table class="mem">
						<thead>
							<tr>
								<th>队员名称</th>
								<th>主打位置</th>
							</tr>
						</thead>
						<tbody class="info"></tbody>
					</table>
				</tr>
			</table>
		</div>
	</div>
	<div class="record_info">
		<table class="record">
			<thead>
				<tr>
					<th>场次</th>
					<th>局次</th>
					<th>得分队伍</th>
					<th>得分队员</th>
					<th>得分时间</th>
				</tr>
			</thead>
			<tbody class="info">
			</tbody>
		</table>
	</div>
</body>
</html>