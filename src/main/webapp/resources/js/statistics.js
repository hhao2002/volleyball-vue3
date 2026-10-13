$(function() {
	/*获取用户基本信息*/
	var userid = $("#userid").text();
	var role = $("#role").text();

	/*判断是否选取了比赛*/
	var mid = $.getUrlParam("matchid");
	if (mid != null) {
		$(".none").css("display", "none");
		/*获取比赛信息*/
		$.ajax({
			type: "post",
			url: "match/getMatch.do",
			contentType: "application/json;charset=UTF-8",
			dataType: "json",
			data: JSON.stringify({ id: mid }),
			success: function(match) {
				var scores = match.scores
				/*显示比赛信息*/
				$(".mname").text(match.name);
				$(".sponsor").text(match.sponsor);
				$(".start").text(match.startTime);
				$(".end").text(match.endTime);
				$(".state").text(match.state);
				$(".rules").text(match.rules);
				$(".s1").text(scores[0].dScore);
				$(".s2").text(scores[1].dScore);
				/*获取队员信息*/
				$.ajax({
					type: "post",
					url: "team/getTeamsById.do",
					contentType: "application/json;charset=UTF-8",
					dataType: "json",
					data: JSON.stringify([scores[0].teamId, scores[1].teamId]),
					success: function(teams) {
						console.log(teams);
						/*显示队伍信息*/
						$(".t1 .info .name").text(teams[0].name);
						$(".t1 .info .name").attr("class", "tn" + teams[0].id);
						$(".t1 .info .coach").text(teams[0].coach);
						$(".t2 .info .name").text(teams[1].name);
						$(".t2 .info .name").attr("class", "tn" + teams[1].id);
						$(".t2 .info .coach").text(teams[1].coach);
						/*显示队员信息*/
						for (var i = 0; i < teams[0].members.length; i++)
							$(".t1 .mem").append("<tr><td class='mn" + teams[0].members[i].id + "'>" + teams[0].members[i].name + "</td>" + "<td>" + teams[0].members[i].position + "</td></tr>");
						for (var i = 0; i < teams[1].members.length; i++)
							$(".t2 .mem").append("<tr><td class='mn" + teams[1].members[i].id + "'>" + teams[1].members[i].name + "</td>" + "<td>" + teams[1].members[i].position + "</td></tr>");
						/*分页*/
						var $table1 = $(".t1 .mem"),
							currentPage = 0;
						var pageSize = 3;
						$table1.bind('paging', function() {
							//隐藏所有的行，取出当前页的行显示
							$table1.find('.info tr').hide().slice(currentPage * pageSize, (currentPage + 1) * pageSize).show();
						});
						var sumRows = $table1.find('.info tr').length;  //记录总行数
						var sumPages = Math.ceil(sumRows / pageSize);
						var $pager = $("<div class='page'></div>");  //分页div
						for (var pageindex = 0; pageindex < sumPages; pageindex++) {
							//为页标签加上链接
							$('<a href="#"><span>' + (pageindex + 1) + '</span></a>').bind('click', { "newpage": pageindex }, function(event) {
								currentPage = event.data["newpage"];
								$table1.trigger('paging');
							}).appendTo($pager);
							$pager.append(' ');
						}
						$pager.insertAfter($table1);
						$table1.trigger('paging');

						var $table2 = $(".t2 .mem");
						$table2.bind('paging', function() {
							//隐藏所有的行，取出当前页的行显示
							$table2.find('.info tr').hide().slice(currentPage * pageSize, (currentPage + 1) * pageSize).show();
						});
						var sumRows = $table2.find('.info tr').length;  //记录总行数
						var sumPages = Math.ceil(sumRows / pageSize);
						var $pager = $("<div class='page'></div>");  //分页div
						for (var pageindex = 0; pageindex < sumPages; pageindex++) {
							//为页标签加上链接
							$('<a href="#"><span>' + (pageindex + 1) + '</span></a>').bind('click', { "newpage": pageindex }, function(event) {
								currentPage = event.data["newpage"];
								$table2.trigger('paging');
							}).appendTo($pager);
							$pager.append(' ');
						}
						$pager.insertAfter($table2);
						$table2.trigger('paging');
						/*获取记录信息*/
						$.ajax({
							type: "post",
							url: "record/getRecords.do",
							contentType: "application/json;charset=UTF-8",
							dataType: "json",
							data: JSON.stringify(mid),
							success: function(records) {
								var tid, mid, session, inning = 0;
								var rtext = "";
								/*添加信息*/
								for (var i = 0; i < records.length; i++) {
									/*赋值信息*/
									tid = $(".tn" + records[i].teamId).text();
									mid = $(".mn" + records[i].memberId).text();
									session = records[i].session;
									inning = records[i].inning;
									rtext += "第" + session + "场 " + "第" + inning + "局 " + tid + "队 " + mid + "球员得分,";
									/*添加元素*/
									$(".record").append("<tr><td>" + session + "</td>" + "<td>" + inning + "</td>" + "<td>" + tid + "</td>" + "<td>" + mid + "</td>" + "<td>" + records[i].scoreTime + "</td></tr>");
								}
								/*语音播报*/
								var utterThis = new window.SpeechSynthesisUtterance(rtext);
								window.speechSynthesis.speak(utterThis);
								$("#playBtn").click(function(){
									window.speechSynthesis.speak(utterThis);
								});
								$("#pauseBtn").click(function(){
									window.speechSynthesis.cancel(utterThis);
								});
								

								/*分页*/
								var $table = $(".record"),
									currentPage = 0;
								var pageSize = 5;
								$table.bind('paging', function() {
									//隐藏所有的行，取出当前页的行显示
									$table.find('.info tr').hide().slice(currentPage * pageSize, (currentPage + 1) * pageSize).show();
								});
								var sumRows = $table.find('.info tr').length;  //记录总行数
								var sumPages = Math.ceil(sumRows / pageSize);
								var $pager = $("<div class='page'></div>");  //分页div
								for (var pageindex = 0; pageindex < sumPages; pageindex++) {
									//为页标签加上链接
									$('<a href="#"><span>' + (pageindex + 1) + '</span></a>').bind('click', { "newpage": pageindex }, function(event) {
										currentPage = event.data["newpage"];
										$table.trigger('paging');
									}).appendTo($pager);
									$pager.append(' ');
								}
								$pager.insertAfter($table);
								$table.trigger('paging');
							}
						});
					}
				});
			}
		});
	} else {
		$(".main,.record_info").css("display", "none");
	}
});