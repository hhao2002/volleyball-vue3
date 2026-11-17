<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>赛事</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath }/resources/css/matchs.css" />
	</head>

	<body>
		<%@ include file="ul.jsp"%>
		<div id="matchs">
			<div class="match">
				<div class="match button new">
					<p>添加一场比赛</p>
					&lt;<span class="add-match">+</span>&gt;
				</div>
			</div>
		</div>
		<div id="add-match-form">
			<form action="${pageContext.request.contextPath }/match/addMatch.do" method="post">
				<table>
					<span id="close-match" class="button">x</span>
					<tr>
						<td>
							<p>赛事名称:</p>
						</td>
						<td>
							<input name="name" type="text" />
						</td>
					</tr>
					<tr>
						<td>
							<p>比赛规则:</p>
						</td>
						<td>
							<select name="rules">
								<option>1-5</option>
								<option>3-15</option>
								<option>5-25</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>
							<p>开始时间:</p>
						</td>
						<td>
							<input name="startTime" type="datetime-local" />
						</td>
					</tr>
					<tr>
						<td>
							<p>结束时间:</p>
						</td>
						<td>
							<input name="endTime" type="datetime-local" />
						</td>
					</tr>
					<tr>
						<td>
							<p>举办方:</p>
						</td>
						<td>
							<input name="sponsor" type="text" />
						</td>
					</tr>
					<tr>
						<td>
							<p>状态:</p>
						</td>
						<td>
							<select name="state">
								<option>准备</option>
								<option>进行</option>
								<option>结束</option>
							</select>
						</td>
					</tr>
					<tr id="teams">
						<td id="team1">
							队1:
							<select name="teams[0].name">
							</select>
						</td>
						<td>
							| 队2:
							<select name="teams[1].name">
							</select>
							<input class="add-team button" type="button" value="添加队" />
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input id="submit" class="button" type="submit" value="创建比赛" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</body>

	<script type="text/javascript" charset="utf-8">
		$(function() {
			// 隐藏提交比赛表单
			$("#add-match-form").hide();
			// 动态显示出所有比赛
			var url = "${pageContext.request.contextPath }/match/getAll.do";
			$.get(url, function(matchs) {
				// 给每个比赛创建节点
				for(var i = 0; i < matchs.length; i++) {
					var $div = "<div class='match'><span class='button drop-match'>x</span><div state='" + matchs[i].state + "' tid='" + matchs[i].id + "' class='match exist button'><p class='topic' >赛事名称</p><p class='match-name' >" +
						matchs[i].name + "</p><hr/><p class='topic' >举办方</p>" + "<p>" +
						matchs[i].sponsor + "</p>" + "</div></div>"
					$(".new").parent().before($div);
				}
				// 删除比赛
				$(".drop-match").click(function() {
					console.log("删除比赛");
					// 判断是否是管理员
					if("${user.role}" != "管理员") {
						// 不符合身份要求，跳转登录页面
						alert("当前身份为：${user.role} 无法使用管理员的删除比赛权限,跳转到登录页面！");
						window.location.replace("${pageContext.request.contextPath }/login.do");
					} else {
						$.ajax({
							url: "${pageContext.request.contextPath }/match/deleteMatch.do",
							type: "post",
							data: JSON.stringify({
								id: $(this).siblings().attr("tid")
							}),
							contentType: "application/json;charset=UTF-8",
							dataType: "json"
							/* success: function() {
								alert("这场比赛删除成功！！！");
							 }	*/
						});
						alert("这场比赛删除成功！！！");
						window.location.replace("${pageContext.request.contextPath }/matchs.do");
					}
				});
				// 存在的比赛单击事件
				$(".exist").click(function() {
					if(!($(this).attr("state") == "结束")) { // 比赛未结束
						// 判断是否是管理员或用户
						if("${user.role}" == "用户") {
							console.log("观看实时比赛");
							// 符合身份要求，跳转查询详情界面
							alert("观看实时比赛,跳转到查询详情界面！");
							window.location.replace("${pageContext.request.contextPath }/statistics.do");
						} else if("${user.role}" == "记分员" || "${user.role}" == "管理员") { // 判断是否是计分员或管理员
							console.log("记录比赛");
							// 更新比赛信息 并 跳转到计分界面
							$.ajax({
								url: "${pageContext.request.contextPath }/match/getMatch.do",
								type: "post",
								data: JSON.stringify({
									id: $(this).attr("tid")
								}),
								contentType: "application/json;charset=UTF-8",
								dataType: "json",
								success: function(match) {
									window.location.replace("${pageContext.request.contextPath }/recording.do");
								}
							});
						} else {
							// 不符合身份要求，跳转登录页面
							alert("当前身份为：游客 无法使用观看实时比赛权限,跳转到登录页面！");
							window.location.replace("${pageContext.request.contextPath }/login.do");
						}
					} else { // 比赛结束
						window.location.replace("${pageContext.request.contextPath }/statistics.do?matchid=" + $(this).attr("tid"));
					}
				});
			});
			$("#close-match").click(function() {
				// 关闭表单
				$("#add-match-form").hide();
			});
			$(".new").click(function() {
				// 创建比赛
				console.log("创建比赛");
				// 判断是否是管理员
				if("${user.role}" != "记分员" && "${user.role}" != "管理员") {
					// 不符合身份要求，跳转登录页面
					alert("当前身份为：${user.role} 无法使用管理员或记分员的添加比赛权限,跳转到登录页面！");
					window.location.replace("${pageContext.request.contextPath }/login.do");
				} else {
					var url = "${pageContext.request.contextPath }/team/getAll.do";
					$.get(url, function(teams) {
						var selects = $("#teams>td>select");
						var flag = true;
						for(var i = 0; i < selects.length; i++) {
							selects[i].options.length = 0;
							for(var j = 0; j < teams.length; j++) {
								if(teams[j].matchId == 1) {
									$("<option class='team" + teams[j].id + "' value='" + teams[j].name + "'>" + teams[j].name + "</option>").appendTo(selects[i]);
								}
							}
							if(selects[i].length == 0)
								flag = false;
						}
						if(flag)
							$("#add-match-form").show();
						else
							alert("暂时没有空闲队伍！");
						$(selects[0]).find("option:last").hide();
						$(selects[1]).find("option:first").hide();
						$(selects[1]).find("option:last").attr("selected", true);
					})
				}
			});
			$("#teams>td>select").change(function() {
				// 完善选择队伍
				$("#teams option").show();
				var aselect = $(this).parent().siblings("td").children("select");
				$(this).find("." + aselect.find("option:selected").attr("class")).hide();
				aselect.find("." + $(this).find("option:selected").attr("class")).hide();
			});
		});
	</script>

</html>