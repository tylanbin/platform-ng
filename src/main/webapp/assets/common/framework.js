var AppCore = {
	// 定义项目的基础路径，修改的同时需要修改404和500的错误页面
	baseUrl : 'http://localhost/'
};
$(function() {
	// options对象包括accepts、crossDomain、contentType、url、async、type、headers、error、dataType等许多参数选项
	// originalOptions对象就是你为$.ajax()方法传递的参数对象，也就是 { url: "/index.php" }
	// jqXHR对象 就是经过jQuery封装的XMLHttpRequest对象（保留了其本身的属性和方法）
	$.ajaxPrefilter("json", function(options, originalOptions, jqXHR) {
		// 封装请求路径，使业务和视图进行分离
		options.url = AppCore.baseUrl + originalOptions.url;
	});
	$.ajaxPrefilter("html", function(options, originalOptions, jqXHR) {
		// 封装请求路径，使业务和视图进行分离
		options.url = AppCore.baseUrl + originalOptions.url;
	});
})