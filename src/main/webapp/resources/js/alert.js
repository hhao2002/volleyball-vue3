$(function() {
	// 判断信息
	var info = $.getUrlParam("info");
	if (info != null) {
		console.log(info)
		switch (info) {
			case "200":
				toAlert("恭喜您注册成功");
				break;
			case "204":
				toAlert("您使用的用户名已存在");
				break;
			case "304":
				toAlert("您输入的验证码不正确");
				break;
			case "400":
				alert("恭喜您登录成功");
				break;
			case "404":
				toAlert("您的密码或者账户不匹配");
				break;
			default:
				break;
		}
	}
	/*isMax();*/
});

// 判断是否最大化
function isMax() {
	if (window.outerHeight === screen.availHeight) {
		if (window.outerWidth === screen.availWidth) {
			return;
		}
	}
	alert("建议全屏下应用此页面");
}

// 获取请求参数
(function($) {
	$.getUrlParam = function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null) return decodeURI(r[2]);
		return null;
	}
})(jQuery);

// 打印提示信息
function toAlert(info) {
	console.log(info);
	$("#alert").html(info);
}
