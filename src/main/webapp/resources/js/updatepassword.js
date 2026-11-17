$(document).submit(function() {
	return checkPassword();
});

function checkPassword() {
	//1.获取密码的值
	var Tpassword = document.getElementById("password");
	var password = document.getElementById("password").value;
	//2.定义正则表达式
	var reg_password = /^[a-zA-Z\d]{6,12}$/;
	//3.判断值是否符合正则的规则
	var flag = reg_password.test(password);
	//4.提示信息
	var s_password = document.getElementById("uword");
	if (flag) {
		s_password.innerHTML = "";
	} else {
		s_password.innerHTML = "密码由6-12个英文，数字组成!";
		Tpassword.focus();
	}
	return flag;
}