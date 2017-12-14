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

// 可以将表单直接序列化为一个json对象
$.fn.serializeObj = function() {
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name]) {
			if (!o[this.name].push) {
				o[this.name] = [o[this.name]];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
};