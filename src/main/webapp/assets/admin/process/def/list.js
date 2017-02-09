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
		url : 'admin/process/def/data/all',
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

function dlg_deploy() {
	$('#fm-deploy').form('clear');
	$('#dlg-deploy').dialog('open');
}
function func_deploy() {
	$('#fm-deploy').form('submit', {
		url : AppCore.baseUrl + 'admin/process/def/deploy',
		onSubmit : function(param) {
			if ($(this).form('validate')) {
				// 避免文件过大导致用户重复提交
				$('body').mask('操作中...');
			}
			return $(this).form('validate');
		},
		success : function(data) {
			$('body').unmask();
			var data = eval('(' + data + ')');
			if (data.success) {
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-deploy').dialog('close');
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

function dlg_auth() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要设置的条目！', 'info');
	} else if (rows.length == 1) {
		$('#auth-pdId').val(rows[0].id);
		$('#auth-ids').combotree({
		    url : 'admin/system/role/tree',
		    method : 'get',
		    required : true,
			editable : false,
			multiple : true,
			cascadeCheck : false,
			onBeforeSelect : function(node) {
				// 不允许select节点，会自动check
				return false;
			},
			onBeforeCheck : function(node) {
				// 机构类型不允许选择
				if (!$.isNumeric(node.id)) {
					$.messager.show({
						title : '提示',
						msg : '不允许选择机构！',
						showType : 'fade'
					});
					return false;
				}
			}
		});
		// 读取历史数据
		$.ajax({
			type : 'get',
			url : 'admin/process/def/' + rows[0].id + '/auth',
			dataType : 'json',
			async : false,
			success : function(data) {
				$('#auth-ids').combotree('setValues', data);
			}
		});
		$('#dlg-auth').dialog('open');
	} else {
		$.messager.alert('提示', '设置发起人时只可以选择一个！', 'info');
	}
}
function func_auth() {
	var pdId = $('#auth-pdId').val();
	var ids = $('#auth-ids').combo('getValues');
	$.ajax({
		type : 'post',
		url : 'admin/process/def/' + pdId + '/auth',
		data : {
			ids : ids.join(',')
		},
		dataType : 'json',
		async : true,
		success : function(data) {
			if (data.success) {
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-auth').dialog('close');
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

function func_del() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length > 0) {
		$.messager.confirm('提示', '确定删除已选择的条目？', function(r) {
			if (r) {
				var ids = new Array();
				$.each(rows, function(i, row) {
					ids.push(row.deploymentId);
				});
				$.ajax({
					type : 'delete',
					url : 'admin/process/def/batch?ids=' + ids,
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

function func_state() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要激活/挂起的条目！', 'info');
	} else if (rows.length == 1) {
		var url = '';
		var title = '';
		var msg = '';
		if (rows[0].isSuspended) {
			// 激活操作
			url = 'admin/process/def/' + rows[0].id + '/state/active';
			title = '激活成功';
			msg = rows[0].id + '已被成功激活';
		} else {
			// 挂起操作
			url = 'admin/process/def/' + rows[0].id + '/state/suspend';
			title = '挂起成功';
			msg = rows[0].id + '已被成功挂起';
		}
		if (url) {
			$.ajax({
				type : 'put',
				url : url,
				dataType : 'json',
				async : true,
				success : function(data) {
					if (data.success) {
						$('#dg-list').datagrid('reload');
						/*$('#dg-list').datagrid('clearSelections');
						$.messager.show({
							title : title,
							msg : msg,
							showType : 'fade',
							style : {
								right : '',
								bottom : ''
							}
						});*/
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
		}
	} else {
		$.messager.alert('提示', '激活/挂起时只可以选择一个！', 'info');
	}
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