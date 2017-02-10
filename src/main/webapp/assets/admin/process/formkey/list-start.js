$(function() {
	$('#dg-list').datagrid({
		fit : true,
		striped : true,
		border : true,
		idField : 'id',
		rownumbers : true,
		fitColumns : true,
		singleSelect : true,
		pagination : true,
		pageSize : 15,
		pageList : [10, 15, 20],
		url : 'admin/process/formkey/process/startList',
		queryParams : {},
		method : 'get',
		columns : [[{
					"field" : "id",
					"title" : "编号"
				}, {
					"field" : "key",
					"title" : "KEY"
				}, {
					"field" : "name",
					"title" : "名称"
				}, {
					"field" : "version",
					"title" : "版本"
				}, {
					"field" : "category",
					"hidden" : true
				}, {
					"field" : "deploymentId",
					"hidden" : true
				}, {
					"field" : "description",
					"title" : "描述",
					formatter : function(value, row, index) {
						if (value) {
							if (value.length > 10) {
								return value.substring(0, 10) + '...';
							} else {
								return value;
							}
						}
					}
				}, {
					"field" : "isSuspended",
					"title" : "状态",
					formatter : function(value, row, index) {
						if (value) {
							return '挂起';
						} else {
							return '激活';
						}
					}
				}]],
		loadMsg : '数据载入中...'
	});
	$('#dg-list').datagrid('getPager').pagination({
		beforePageText : '第',
		afterPageText : '页    共 {pages} 页',
		displayMsg : '当前显示 {from} - {to} 条记录    共 {total} 条记录'
	});
});

function search(value, name) {
	$('#dg-list').datagrid('clearSelections');
	$('#dg-list').datagrid('reload', {
		params : '{ "' + name + '" : "' + value + '" }'
	});
}

function func_reload() {
	$('#searchbox').searchbox('setValue', '');
	$('#dg-list').datagrid('clearSelections');
	$('#dg-list').datagrid('reload', {});
}

function dlg_start() {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		var html = '<div style="text-align: center;">没有需要填写的表单</div>';
		$.ajax({
			type : 'get',
			url : 'admin/process/formkey/process/' + row.id + '/form',
			dataType : 'html',
			async : false,
			success : function(data) {
				html = data;
			}
		});
		// 将表单的html填入form
		$('#fm-current').html(html);
		// 将表单使用EasyUI进行解析
		$.parser.parse('#fm-current');
		// 记录流程的信息
		$('#start-pdId').val(row.id);
		$('#start-piName').val(row.name);
		$('#dlg-start').dialog('open');
	} else {
		$.messager.alert('提示', '请选择要启动的流程！', 'info');
	}
}
function func_start() {
	// 这里是利用html中的form进行提交，所以需要加上项目路径AppCore.baseUrl
	var pdId = $('#start-pdId').val();
	var piName = $('#start-piName').val();
	$('#fm-current').form('submit', {
		url : AppCore.baseUrl + 'admin/process/formkey/process/' + pdId + '/start',
		onSubmit : function(param) {
			param.piName = piName;// 追加参数
			return $(this).form('validate');
		},
		success : function(data) {
			var data = eval('(' + data + ')');
			if (data.success) {
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-start').dialog('close');
			} else {
				$.messager.show({
					title : '错误',
					msg : data.msg,
					showType : 'fade',
					style : {
						right : '',
						bottom : ''
					}
				});
			}
		}
	});
}

function func_view(type) {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		var url = AppCore.baseUrl + 'admin/process/def/' + row.id + '/resource/' + type;
		if (type == 'xml') {
			window.open(url);
		} else {
			$('#dlg-view').dialog({
				content : '<img src="' + url + '"/>'
			}).dialog('open');
		}
	} else {
		$.messager.alert('提示', '请选择要预览的流程！', 'info');
	}
}