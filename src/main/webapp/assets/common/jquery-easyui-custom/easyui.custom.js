(function($){
	
	/*
		validatebox验证扩展
		自带内容：
			email: Match email regex rule.
			url: Match URL regex rule.
			length[0,100]: Between x and x characters allowed.
	 */
	
	$.extend($.fn.validatebox.defaults.rules, {
		// 长短限制
		minLength : {
			validator : function(value, param) {
				return value.length >= param[0];
			},
			message : '请输入最小{0}位字符'
		},
		maxLength : {
			validator : function(value, param) {
				return param[0] >= value.length;
			},
			message : '请输入最大{0}位字符'
		},
		height : {
			validator : function(value, param) {
				return param[0] >= value.length;
			},
			message : '请输入最大{0}位数字身高，身高单位：CM'
		},
		weight : {
			validator : function(value, param) {
				return param[0] >= value.length;
			},
			message : '请输入最大{0}位数字身高，体重单位：KG'
		},
		// 常用格式限制
		intOrFloat : {
			validator : function(value) {
				return /^\d+(\.\d+)?$/.test(value);
			},
			message : '请输入整数或小数'
		},
		intNum : {
			validator : function(value) {
				return /^([+]?[1-9])+\d*$/.test(value);
			},
			message : '请输入正整数'
		},
		chs : {
			validator : function(value) {
				return /^[\Α-\￥]+$/.test(value);
			},
			message : '请输入汉字'
		},
		eng : {
			validator : function(value) {
				return /^[A-Za-z]+$/.test(value);
			},
			message : '请输入英文'
		},

		// 常用内容限制
		age : {
			validator : function(value) {
				return /^(?:[1-9][0-9]?|1[01][0-9]|120)$/.test(value);
			},
			message : '年龄必须在0到120之间'
		},
		zip : {
			validator : function(value) {
				return /^[1-9]\d{5}$/.test(value);
			},
			message : '请输入正确的邮政编码'
		},
		phone : {
			validator : function(value) {
				return /^(13|15|18)\d{9}$/.test(value); // 这里就是一个正则表达是
			},
			message : '请输入正确的手机号码'// 这里是错误后的提示信息
		},
		tel : {
			validator : function(value) {
				return /(\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$/.test(value); // 这里就是一个正则表达是
			},
			message : '请输入正确的电话号码'// 这里是错误后的提示信息
		},
		idcard : {
			validator : function(value) {
				return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value);
			},
			message : '请输入正确的身份证'
		},
		qq : {
			validator : function(value, param) {
				return /^[1-9]\d{4,10}$/.test(value);
			},
			message : '请输入正确的QQ号码'
		},
		loginName : {
			validator : function(value) {
				return /^[a-zA-Z][a-zA-Z0-9_]{5,15}$/.test(value);
			},
			message : '用户名不合法（字母开头，长度6-16，允许字母数字下划线）'
		},
		loginPwd : {
			validator : function(value) {
				return /^[a-zA-Z][a-zA-Z0-9_]{5,15}$/.test(value);
			},
			message : '密码不合法（字母开头，长度6-16，允许字母数字下划线）'
		}
	});

})(jQuery);