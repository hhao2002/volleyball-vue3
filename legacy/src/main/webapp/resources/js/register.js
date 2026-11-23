/*if (window.speechSynthesis) {
	// let spelSynth = window.speechSynthesis.getVoices && window.speechSynthesis.getVoices().find(item => item.localService && item.lang === "zh-CN");
	window.speechSynthesis.cancel();
	const synth = window.speechSynthesis;
	const msg = new SpeechSynthesisUtterance();
	msg.text = "你好得分";
	msg.lang = "zh-CN";
	msg.volume = "1";
	msg.rate = 1;
	msg.pitch = 1;
	synth.speak(msg);
	msg.text = "李四得分"
	synth.speak(msg);
} */

$(document).submit(function() {
	if (checkUsername() == false)
		return false;
	if (checkPassword() == false)
		return false;
	if (nextcheckPassWord() == false)
		return false;
	if (checkEmail() == false)
		return false;
	return true;
});
//校验用户名
function checkUsername() {
	//先拿到用户输入的用户名
	var Ufocus = document.getElementById("username");
	var username = document.getElementById("username").value.trim();
	var ts = document.getElementsByClassName("uname");
	var regx = /^[\u4e00-\u9fa5_a-zA-Z0-9]{2,15}$/;
	if (!regx.test(username)) {
		ts[0].innerHTML = "";
		ts[1].innerHTML = "用户名由2-15个中文、英文，数字，下划线组成!";
		Ufocus.focus();
		return false;
	} else {
		//判断是否存在此用户名
		var flag = false
		$.ajax({
			type: "post",
			url: "users/isExist.do",
			data: "username=" + username,
			dataType: "json",
			async: false,
			success: function(result) {
				flag = result
			}
		});
		if (flag) {
			ts[0].innerHTML = "";
			ts[1].innerHTML = "此用户名已存在！";
			Ufocus.focus();
			return false;
		} else {
			ts[0].innerHTML = "√";
			ts[1].innerHTML = "";
			return true;
		}
	}
}
//校验密码
function checkPassword() {
	//1.获取密码的值
	var Tpassword = document.getElementById("password");
	var password = document.getElementById("password").value;
	//2.定义正则表达式
	var reg_password = /^[a-zA-Z\d]{6,12}$/;
	//3.判断值是否符合正则的规则
	var flag = reg_password.test(password);
	//4.提示信息
	var s_password = document.getElementsByClassName("uword");

	if (flag) {
		s_password[0].innerHTML = "√";
		s_password[1].innerHTML = "";
	} else {
		s_password[0].innerHTML = "";
		s_password[1].innerHTML = "密码由6-12个英文，数字组成!";
		Tpassword.focus();
	}
	return flag;
}


//校验两次输入的密码是否相等
function nextcheckPassWord() {
	//先拿到用户第一次输入的密码
	var Tpassword = document.getElementById("repassword");
	var Tpassword2 = document.getElementById("password");
	var password = document.getElementById("password").value.trim();
	//再拿到用户第二次输入的密码
	var password1 = document.getElementById("repassword").value.trim();
	//进行友好提示
	var tip = document.getElementsByClassName("uword1");
	//对比两次输入的密码是否相同
	var flag = password == password1;

	if (flag) {
		tip[0].innerHTML = "√";
		tip[1].innerHTML = "";
	} else {
		tip[0].innerHTML = "";
		tip[1].innerHTML = "两次密码输入不一致"
		Tpassword2.focus();
	}
	return flag;
}
//校验Email
function checkEmail() {
	//1.获取Email的值
	var email1 = document.getElementById("email");

	var email = document.getElementById("email").value;
	//2.定义正则表达式
	var reg_email = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;

	//3.判断值是否符合正则的规则
	var flag = reg_email.test(email);
	//4.提示信息
	var s_email = document.getElementsByClassName("Temail");

	if (flag) {
		s_email[0].innerHTML = "√";
		s_email[1].innerHTML = "";
	} else {
		s_email[0].innerHTML = "";
		s_email[1].innerHTML = "邮箱格式不正确";
		email1.focus();
	}
	return flag;
}
