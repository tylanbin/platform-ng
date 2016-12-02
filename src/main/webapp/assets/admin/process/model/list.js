var selected = null;
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
		url : 'admin/process/model/data',
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
					"field" : "createTime",
					"title" : "创建时间",
					formatter : function(value, row, index) {
						if (value) {
							var date = new Date(value);
							return date.format('yyyy-MM-dd HH:mm:ss');
						}
					}
				}, {
					"field" : "lastUpdateTime",
					"title" : "修改时间",
					formatter : function(value, row, index) {
						if (value) {
							var date = new Date(value);
							return date.format('yyyy-MM-dd HH:mm:ss');
						}
					}
				}, {
					"field" : "editorSourceValueId",
					"title" : "JSON编号"
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

function dlg_add() {
	$('#fm-add').form('clear');
	$('#dlg-add').dialog('open');
}
function func_add() {
	if ($('#fm-add').form('validate')) {
		// $('#dg-list').datagrid('reload');
		// $('#dg-list').datagrid('clearSelections');
		// $('#dlg-edit').dialog('close');
		$('#fm-add').attr('action', AppCore.baseUrl + 'admin/process/model/add');
		$('#fm-add').submit();
	}
}

function func_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要设计的条目！', 'info');
	} else if (rows.length == 1) {
		window.location.href = 'editor.html?id=' + rows[0].id;
	} else {
		$.messager.alert('提示', '设计时只可以选择一个！', 'info');
	}
}

function func_del() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length > 0) {
		$.messager.confirm('提示', '确定删除已选择的条目？', function(r) {
			if (r) {
				var ids = new Array();
				$.each(rows, function(i, row) {
					ids.push(row.id);
				});
				$.ajax({
					type : 'delete',
					url : 'admin/process/model/batch?ids=' + ids,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							$('#dg-list').datagrid('reload');
							$('#dg-list').datagrid('clearSelections');
						} else {
							// 出错也需要重载
							$('#dg-list').datagrid('reload');
							$('#dg-list').datagrid('clearSelections');
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
					},
					error : function() {
						// 出错也需要重载
						$('#dg-list').datagrid('reload');
						$('#dg-list').datagrid('clearSelections');
						$.messager.show({
							title : '错误',
							msg : '服务器正忙，请稍后再试！',
							showType : 'fade',
							style : {
								right : '',
								bottom : ''
							}
						});
					}
				});
			}
		});
	} else {
		$.messager.alert('提示', '请选择要删除的条目！', 'info');
	}
}

function func_reload() {
	$('#searchbox').searchbox('setValue', '');
	$('#dg-list').datagrid('clearSelections');
	$('#dg-list').datagrid('reload', {});
}

function func_getJson() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要导出的条目！', 'info');
	} else if (rows.length == 1) {
		window.location.href = AppCore.baseUrl + 'admin/process/model/' + rows[0].id + '/json';
	} else {
		$.messager.alert('提示', '导出时只可以选择一个！', 'info');
	}
}

function func_deploy() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要部署的条目！', 'info');
	} else if (rows.length == 1) {
		$.ajax({
			type : 'post',
			url : 'admin/process/model/' + rows[0].id + '/deploy',
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					$('#dg-list').datagrid('reload');
					$('#dg-list').datagrid('clearSelections');
					$.messager.show({
						title : '部署成功',
						msg : '流程ID：' + data.processDefinitionId,
						showType : 'fade',
						style : {
							right : '',
							bottom : ''
						}
					});
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
			},
			error : function() {
				$.messager.show({
					title : '错误',
					msg : '服务器正忙，请稍后再试！',
					showType : 'fade',
					style : {
						right : '',
						bottom : ''
					}
				});
			}
		});
	} else {
		$.messager.alert('提示', '部署时只可以选择一个！', 'info');
	}
}