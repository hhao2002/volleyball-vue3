// 添加时间函数
function time() {
	// 初始化时间变量
	var date = new Date(), h, m, s;
	// 分别为h,m,s变量赋值上时间数值
	h = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
	m = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
	s = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
	// 在id为time的div上输入时间信息
	$('#time').html(h + ":"+ m + ":"+ s);
}
// 为time函数添加定时器
setInterval(time, 1000);