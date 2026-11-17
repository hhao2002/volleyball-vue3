var userid;
$(function() {
	/*获取用户基本信息*/
	userid = $("#userid").text();

	/*判断是否已登录*/
	if (userid == "") {
		alert("请先登录！")
		window.location.href = "login.do";
	}

	/*判断身份*/
	switch ($("#role").text()) {
		case "管理员":
			$("#guan").css("display", "none");
		case "记分员":
			$("#ji").css("display", "none");
			break;
	}
});

/*绑定申请*/
function sendApp(role) {
	/*防止多次申请*/
	if ($(".apps tr[state=未处理]").length > 1 && $("#role").text() != "管理员") {
		alert("您已提交过身份申请，不能重复提交超过两次，等待批准...");
		return false;
	}
	$.ajax({
		type: "post",
		url: "application/sendApp.do",
		contentType: "application/json;charset=UTF-8",
		dataType: "json",
		data: JSON.stringify({ id: userid, username: $("#username").text(), role: role })
	});
	alert("申请管理员成功等待批准");
	location.reload();
}

/*获取所有申请*/
$.ajax({
	type: "post",
	url: "application/getApps.do",
	contentType: "application/json;charset=UTF-8",
	dataType: "json",
	success: function(apps) {
		console.log(apps);
		/*生成数据*/
		for (var i = 0; i < apps.length; i++) {
			var info = "<tr state='" + apps[i].state + "'>" +
				"<td><span>" + apps[i].apUserName + "</span></td>" +
				"<td><span>" + apps[i].apType.description + "</span></td>" +
				"<td><span>" + apps[i].dealUserName + "</span></td>" +
				"<td><span>" + apps[i].time + "</span></td>" +
				"<td><span>" + apps[i].state + "</span></td>";
			if (apps[i].state == "未处理" && $("#role").text() == "管理员") {
				info += "<td><button Id='" + apps[i].id + "' aid='" + apps[i].apUserId + "' tid='" + apps[i].apType.id + "'>通过</button>" +
					"<button Id='" + apps[i].id + "' aid='" + apps[i].apUserId + "' tid='" + apps[i].apType.id + "'>否定</button></td></tr >";
			}
			$(".apps").append(info + "</tr>");
		}
		/*绑定触发函数*/
		$(".apps button").click(function(e) {
			var dname = $("#username").text();
			var aid = $(this).attr("aid");
			var tid = $(this).attr("tid");
			var id = $(this).attr("Id");
			var state = $(this).text();
			console.log("获取的信息：", id, info, userid, dname)
			/*发送请求*/
			$.ajax({
				type: "post",
				url: "application/updataApp.do",
				contentType: "application/json;charset=UTF-8",
				dataType: "json",
				data: JSON.stringify({ id: id, dealUserId: userid, dealUserName: dname, state: state, apUserId: aid, apTypeId: tid })
			});
			alert("已" + state + "批准");
			location.reload();
		});
		/*分页*/
		var $table = $(".infos"),
			currentPage = 0;
		var pageSize = 5;
		$table.bind('paging', function() {
			//隐藏所有的行，取出当前页的行显示
			$table.find('.apps tr').hide().slice(currentPage * pageSize, (currentPage + 1) * pageSize).show();
		});
		var sumRows = $table.find('.apps tr').length;  //记录总行数
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
