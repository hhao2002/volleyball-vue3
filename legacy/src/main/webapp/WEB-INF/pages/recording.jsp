<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>记录比赛</title>
		<style>
			.mselect {
				background: #f7f5d1;
			}
			
			#tooltip {
				position: absolute;
				border: 1px solid #333;
				background: #f7f5d1;
				padding: 0.5vw;
				font-size: 2vw;
				color: #333;
				display: none;
			}
			
			span[class*=score] {
				font-weight: bolder;
				font-size: 3vw;
			}
			
			.team-score {
				font-size: 1.5vw;
				display: inline-block;
			}
			
			.tr-member {
				position: absolute;
				background-color: rgba(179, 179, 179, 0.8);
				border-radius: 0.5vw;
				left: 37.5vw;
				top: 12vw;
			}
			
			.info {
				font-size: 2vw;
				font-weight: bolder;
			}
			
			.heading {
				font-size: 1.5vw;
			}
			
			li:nth-child(3)>a {
				width: 70%;
				font-size: 32px;
				background-color: rgba(109, 76, 61, 0.8);
			}
			
			.right {
				text-align: right;
			}
			
			p,
			select,
			input {
				font-size: 2vw;
			}
			
			input[type=text] {
				border: none;
				background-color: rgba(0, 0, 0, 0);
				width: 25vw;
			}
			
			input[type=button] {
				width: 16vw;
				margin-left: 1vw;
				margin-top: 1vw;
				height: 8vh;
				white-space: nowrap;
			}
			
			.record {
				display: none;
				position: absolute;
				left: 10vw;
				top: 20vh;
				width: 80vw;
				border-radius: 6vw;
				background: linear-gradient(145deg, #f0f0f0, #cacaca);
				box-shadow: 2vw 2vw 2vw #b1b1b1, -2vw -2vw 2vw #ffffff;
				padding: 2vw;
			}
			
			.member {
				width: 40vw;
				border-radius: 3vw;
				background: #e0e0e0;
				box-shadow: inset 1vw 1vw 1vw #b1b1b1, inset -1vw -1vw 1vw #ffffff;
				padding: 2vw;
			}
		</style>
	</head>

	<body>
		<%@ include file="ul.jsp" %>
		<div class="record">
			<form action="${pageContext.request.contextPath }/record/submitRecord.do" method="post">
				<table>
					<tr>
						<td colspan='4' style="text-align: center;">
							<p class="heading">规则： <span class="info">${match.rules }</span></p>
						</td>
					</tr>
					<tr>
						<td class="right">
							<p>场次|</p>
						</td>
						<td>
							<select name="session">
							</select>
						</td>
						<td class="right">
							<select name="inning" onfocus=this.blur()>
							</select>
						</td>
						<td>
							<p>|局数</p>
						</td>
					</tr>
					<tr class="tr-member">
						<th colspan="4">
							<p>得分队员</p>
						</th>
					</tr>
					<tr>
						<td class="${match.teams[0].id} right" colspan='2'>
							<input class="info" type="text" value="${match.teams[0].name}" onfocus=this.blur() />
							<div class="team-score">
								<span class="score${match.scores[0].teamId}d"></span> |
								<span class="score${match.scores[0].teamId}x"></span>:
							</div>
						</td>
						<td class="${match.teams[1].id}" colspan='2'>
							<div class="team-score right">
								:<span class="score${match.scores[1].teamId}x"></span>|
								<span class="score${match.scores[1].teamId}d"></span>
							</div>
							<input class="right info" type="text" value="${match.teams[1].name}" onfocus=this.blur() />
						</td>
					</tr>
					<tr>
						<td tid="${match.teams[0].id}" class="member" colspan='2'>
							<c:forEach var="member" items="${match.teams[0].members }">
								<input class="button mid${member.id}" position="位置：${member.position}" value="${member.name}" mid="${member.id}" type="button" />
							</c:forEach>
						</td>
						<td tid="${match.teams[1].id}" class="member" colspan='2'>
							<c:forEach var="member" items="${match.teams[1].members }">
								<input class="button mid${member.id}" position="位置：${member.position}" value="${member.name}" mid="${member.id}" type="button" />
							</c:forEach>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</body>

</html>
<script type="text/javascript" charset="utf-8">
	$(function() {
		// 悬浮显示 队员位置信息
		var title = '';
		var x = 10;
		var y = 20;
		$('.button').mouseover(function(e) {
			title = $(this).attr("position")
			$('body').append('<div id="tooltip">' + title + '</div>');
			$('#tooltip').css({
				'left': (e.pageX + x + 'px'),
				'top': (e.pageY + y + 'px')
			}).show();
		}).mouseout(function() {
			$('#tooltip').remove();
		}).mousemove(function(e) {
			$('#tooltip').css({
				'left': (e.pageX + x + 'px'),
				'top': (e.pageY + y + 'px')
			}).show();
		})
		// 判断是否选择比赛
		if("${match}" == "") {
			setTimeout(
				function() {
					alert("请先选择一个比赛！");
					window.location
						.replace("${pageContext.request.contextPath }/matchs.do");
				}, 200);
		} else {
			// 获取比赛对象
			var match = $.parseJSON("${match}");
			// 如果已结束调用跳转到观看
			if(match.state == "结束") {
				setTimeout(
					function() {
						alert("该比赛已完成！");
						window.location
							.replace("${pageContext.request.contextPath }/statistics.do");
					}, 200);
			}
			// 初始化变量
			var rs = match.rules.split('-'); // 规则 [1,5] [3,15] [5,25]
			var recordURL = ''; // 提交的地址
			var session = $("select[name=session]"); // 场次数select节点
			var inning = $("select[name=inning]"); // 局数select节点
			var isD = false; // 是否进制
			var isEnd = false; // 比赛是否结束
			// 让单击的队员高亮显示  的方法
			function clickmem() {
				$(".mselect").removeClass("mselect");
				if(inning.val() != inning.children().eq(-1).val()) {
					$.ajax({
						url: "${pageContext.request.contextPath }/record/getOneRecord.do",
						type: "post",
						data: JSON.stringify({
							session: session.val(),
							inning: inning.val(),
							matchId: match.id
						}),
						contentType: "application/json;charset=UTF-8",
						dataType: "json",
						success: function(record) {
							console.log($("#" + record.memberId));
							$($(".mid" + record.memberId)).addClass("mselect");
						}
					});
				}
			}
			// 禁止修改场数
			session.change(function() {
				if(session.val() != session.children().eq(-1).val()) {
					alert("已结束的场次，无法修改！");
					session.children().eq(-1).prop("selected", "selected");
				}
			});
			// 局数修改时，高亮显示得分队员
			inning.change(clickmem);
			// 显示比赛界面
			$(".record").css({
				"display": "block"
			});
			// 初始化 分数
			for(var i = 0; i < match.scores.length; i++) {
				// 更新比分
				$(".score" + match.scores[i].teamId + "d").text(match.scores[i].dScore);
				$(".score" + match.scores[i].teamId + "x").text(match.scores[i].xScore);
			}
			// 初始化 场数
			for(var i = 1; i <= match.scores[0].dScore * 1 + match.scores[1].dScore * 1 + 1; i++) {
				$("<option value='" + i + "' selected>" + i + "</option>")
					.appendTo(session);
			}
			// 初始化 局数
			for(var i = 1; i <= match.scores[0].xScore * 1 + match.scores[1].xScore * 1 + 1; i++) {
				$("<option value='" + i + "' selected>" + i + "</option>")
					.appendTo(inning);
			}
			// 单击队员按钮 得分操作
			$(".button").click(function() {
				// 初始化变量
				var isAdd = inning.val() == inning.children().eq(-1).val(); // 是否是添加记录
				var teamId = $(this).parent().attr("tid");
				var team = $("." + teamId); // 队伍和得分的夫div
				var team1 = $(team[0]);
				var team2 = $(team.siblings()[0]); // 另一个队伍
				var team2Id = $($(this).parent().siblings()[0]).attr("tid");
				var t1d = team1.find("span[class*=d]").text(); // 得分队伍的 大比分
				var t2d = team2.find("span[class*=d]").text(); // 另一个队伍 大比分
				var t1x = team1.find("span[class*=x]").text(); // 得分队伍的 小比分
				var t2x = team2.find("span[class*=x]").text(); // 另一个队伍 小比分
				// 添加一次进球
				if(isAdd) {
					recordURL = "${pageContext.request.contextPath }/record/addRecord.do"
					// 改队伍的小比分自增
					t1x++;
					// 判断是否进一：局数 加1，小得分 归0
					isD = t1x * 1 >= rs[1] * 1 && t1x * 1 > t2x * 1 + 1;
					if(isD) {
						// 比分更新
						t1x = 0;
						t2x = 0;
						t1d++;
						// 判断是否结束
						isEnd = t1d * 1 > rs[0] / 2;
					}
					// 更新比分
					team1.find("span[class*=d]").text(t1d);
					team2.find("span[class*=d]").text(t2d);
					team1.find("span[class*=x]").text(t1x);
					team2.find("span[class*=x]").text(t2x);
					console.log(team1, teamId);
					console.log(t1d, t1x);
					console.log(team2, team2Id);
					console.log(t2d, t2x);
					// 提交比分
					$.ajax({
						type: "post",
						url: "${pageContext.request.contextPath }/score/updateScores.do",
						data: JSON.stringify([{
							id: '',
							matchsId: match.id,
							teamId: teamId,
							dScore: t1d,
							xScore: t1x
						}, {
							id: '',
							matchsId: match.id,
							teamId: team2Id,
							dScore: t2d,
							xScore: t2x
						}]),
						contentType: "application/json;charset=UTF-8",
						dataType: "json",
						success: function(scores) {
							if(scores != null)
								console.log("分数更新成功");
							else
								console.log("分数更新失败");
						}
					});
				} else {
					recordURL = "${pageContext.request.contextPath }/record/updateRecord.do";
					isAdd = false;
					console.log("更新操作");
				}
				// 提交一次记录（添加或修改记录）
				$.ajax({
					url: recordURL,
					type: "post",
					data: JSON.stringify({
						id: '',
						session: session.val(),
						inning: inning.val(),
						matchId: match.id,
						teamId: teamId,
						memberId: $(this).attr("mid"),
						scoreTime: ''
					}),
					contentType: "application/json;charset=UTF-8",
					dataType: "json",
					success: function(record) {
						// 显示得分队员
						$(".mselect").removeClass("mselect");
						$($(".mid" + record.memberId)).addClass("mselect");
						// 是添加操作，更新局数
						if(isAdd) { // 局数 加一
							if(!isD) {
								console.log("添加记录成功");
								$("<option value='" + (inning.val() * 1 + 1) + "' selected>" + (inning.val() * 1 + 1) + "</option>").appendTo(inning);
							} else {
								// 非结束，更新场数
								if(!isEnd) {
									// 清空局数
									inning.empty();
									// 场数 加一
									$("<option value='" + (session.val() * 1 + 1) + "' selected>" + (session.val() * 1 + 1) + "</option>").appendTo(session);
									// 局数 初始化为 1
									$("<option value='" + 1 + "' selected>" + 1 + "</option>").appendTo(inning);
								}
							}
						} else { // 修改操作
							if(record.teamId != teamId) { // 队伍不同时
								// 更新比分
								team1.find("span[class*=x]").text(t1x * 1 + 1);
								team2.find("span[class*=x]").text(t2x - 1);
								t1x++;
								t2x--;
								console.log(team1, teamId);
								console.log(t1d, t1x);
								console.log(team2, team2Id);
								console.log(t2d, t2x);
								// 提交比分
								$.ajax({
									type: "post",
									url: "${pageContext.request.contextPath }/score/updateScores.do",
									data: JSON.stringify([{
										id: '',
										matchsId: match.id,
										teamId: teamId,
										dScore: t1d,
										xScore: t1x
									}, {
										id: '',
										matchsId: match.id,
										teamId: team2Id,
										dScore: t2d,
										xScore: t2x
									}]),
									contentType: "application/json;charset=UTF-8",
									dataType: "json",
									success: function(scores) {
										if(scores != null)
											console.log("分数更新成功");
										else
											console.log("分数更新失败");
									}
								});
								console.log("修改记录成功")
								inning.children().eq(-1).prop("selected", "selected");
							}
						}
						// [5-25] 的最后一局 分数改为15
						console.log(session == '4', t1x == '0');
						if(rs[0] == '5' && session == '4' && t1x == '0') {
							rs[1] = 15;
							alert("已进入决赛：将采用15分制！")
						}
						// 判断是否胜利
						if(isEnd) {
							// 提交：修改比赛状态为结束
							data = {
								id: match.id,
								state: '结束'
							}
							//修改比赛状态为已完成 
							updateMatchStateById(data);
							// 修改：队伍的比赛id为 1
							updateMatchIdOfTeamToOneByMatchId(data.id);
							alert("${match.name}：结束\n " + team.find(".info").val() + " 获得胜利！！！");
							window.location.replace("${pageContext.request.contextPath }/matchs.do");
						}
					}
				});
			});
		}
	});

	//修改比赛状态为已完成 
	function updateMatchStateById(match) {
		$.ajax({
			url: "${pageContext.request.contextPath }/match/updateMatchStateById.do",
			type: "post",
			data: JSON.stringify(match),
			contentType: "application/json;charset=UTF-8",
			dataType: "json",
			success: function(test) {
				console.log(test);
			}
		})
	}

	// 修改：队伍的比赛id为 1
	function updateMatchIdOfTeamToOneByMatchId(matchId) {
		console.log("执行了updateMatchIdOfTeamToOneByMatchId")
		$.ajax({
			url: "${pageContext.request.contextPath }/team/updateMatchIdOfTeamToOneByMatchId.do",
			type: "post",
			data: JSON.stringify(matchId),
			contentType: "application/json;charset=UTF-8",
			dataType: "json",
		})
	}

	//提交比赛
	function submitMatch(data) {
		//修改比赛状态为已完成
		updateMatchStateById(data);

		// 修改：队伍的比赛id为 1
		updateMatchIdOfTeamToOneByMatchId(data.id);

		alert("${match.name}：结束\n " + " 获得胜利！！！");
		window.location.replace(
			"${pageContext.request.contextPath }/statistics.do");
	}
	//获得mvp
	function getMVp(matchId) {

	}
</script>