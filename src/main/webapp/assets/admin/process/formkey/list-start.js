$(function() {
	$('#dg-list').datagrid({
		fit : true,
		striped : true,
		border : true,
		idField : 'id',
		rownumbers : true,
		fitColumns : true,
		singleSelect : false,
		pagination : true,
		pageSize : 15,
		pageList : [10, 15, 20],
		url : 'admin/process/formkey/process/startList',
		queryParams : {},
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
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
}
function func_start() {
}

function func_view(type) {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要预览的条目！', 'info');
	} else if (rows.length == 1) {
		var url = AppCore.baseUrl + 'admin/process/def/' + rows[0].id + '/resource/' + type;
		if (type == 'xml') {
			window.open(url);
		} else {
			$('#dlg-view').dialog({
				content : '<img src="' + url + '"/>'
			}).dialog('open');
		}
	} else {
		$.messager.alert('提示', '预览时只可以选择一个！', 'info');
	}
}