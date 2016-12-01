var selected = null;
$(function() {
	$('#tree').tree({
		url : 'admin/system/org/tree',
		method : 'get',
		lines : true,
		formatter : function(node) {
			var text = node.text;
			$.ajax({
				type : 'get',
				url : 'admin/system/role/data',
				data : {
					page : 1,
					rows : 0,
					params : '{ "org.id" : ' + node.id + ' }'
				},
				dataType : 'json',
				async : false,
				success : function(data) {
					text += '&nbsp;<span style=\'color:blue\'>(' + data.total + ')</span>';
				}
			});
			return text;
		},
		onSelect : function(node) {
			var json = '{ "org.id" : ' + node.id + ' }';
			$('#dg-list').datagrid('clearSelections');
			$('#dg-list').datagrid('reload', {
				params : json
			});
			// 保留parentId信息
			$('#orgId-add').val(node.id);
			$('#orgId-edit').val(node.id);
			$('#orgName-edit').val(node.text);
		},
		onLoadSuccess : function(node, data) {
			$(this).tree('collapseAll');
			if (selected) {
				$(this).tree('expandTo', $(this).tree('find', selected).target);
			}
		}
	});
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
		url : 'admin/system/role/data',
		queryParams : {
			params : '{ "org.id" : -1 }'
		},
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
		columns : [[{
					field : 'name',
					title : '角色名称'
				}, {
					field : 'description',
					title : '角色描述'
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

// 多行添加代码
var editIndex = undefined;
function endEditing() {
	if (editIndex == undefined) {
		return true;
	}
	if ($('#dg-add').datagrid('validateRow', editIndex)) {
		$('#dg-add').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function onClickRow(index) {
	if (editIndex != index) {
		if (endEditing()) {
			$('#dg-add').datagrid('selectRow', index).datagrid('beginEdit', index);
			editIndex = index;
		} else {
			$('#dg-add').datagrid('selectRow', editIndex);
		}
	}
}
function appendLine() {
	if (endEditing()) {
		$('#dg-add').datagrid('appendRow', {});
		editIndex = $('#dg-add').datagrid('getRows').length - 1;
		$('#dg-add').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
	}
}
function removeLine() {
	if (editIndex == undefined) {
		return;
	}
	$('#dg-add').datagrid('cancelEdit', editIndex).datagrid('deleteRow', editIndex);
	editIndex = undefined;
}

function dlg_add() {
	$('#dg-add').datagrid({
		striped : true,
		border : true,
		idField : 'id',
		rownumbers : true,
		fitColumns : true,
		singleSelect : true,
		columns : [[{
					field : 'name',
					width : 80,
					title : '角色名称',
					editor : {
						type : 'validatebox',
						options : {
							required : true,
							validType : ['length[0, 10]']
						}
					}
				}, {
					field : 'description',
					width : 150,
					title : '角色描述',
					editor : {
						type : 'validatebox',
						options : {
							validType : ['length[0, 100]']
						}
					}
				}]],
		loadMsg : '数据载入中...',
		onClickRow : onClickRow
	});
	$('#dlg-add').dialog({
		onResize : function() {
			$('#dg-add').datagrid('resize');
		},
		onClose : function() {
			editIndex = undefined;
			$('#dg-add').datagrid('loadData', {
				total : 0,
				rows : []
			});
		}
	}).dialog('open');
}
function func_add() {
	if (endEditing()) {
		$('#dg-add').datagrid('acceptChanges');
		// 数据处理
		var data = $('#dg-add').datagrid('getData');
		$.ajax({
			type : 'post',
			url : 'admin/system/role/batch',
			data : {
				id : $('#orgId-add').val(),
				objs : JSON.stringify(data.rows)
			},
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					var node = $('#tree').tree('getSelected');
					if (node) {
						selected = node.id;
					}
					$('#tree').tree('reload');
					$('#dg-list').datagrid('reload');
					$('#dg-list').datagrid('clearSelections');
					$('#dlg-add').dialog('close');
				} else {
					// 出错也需要重载，避免重复数据
					$('#tree').tree('reload');
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
				// 出错也需要重载，避免重复数据
				$('#tree').tree('reload');
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
}

function dlg_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要修改的条目！', 'info');
	} else if (rows.length == 1) {
		if (rows[0].org) {
			$('#orgName-edit').val(rows[0].org.name);
		} else {
			$('#orgName-edit').val('无');
		}
		$('#id-edit').val(rows[0].id);
		$('#fm-edit').form('load', rows[0]);
		$('#dlg-edit').dialog('open');
	} else {
		$.messager.alert('提示', '修改条目时只可以选择一个！', 'info');
	}
}
function func_edit() {
	// 这里是利用html中的form进行提交，所以需要加上项目路径AppCore.baseUrl
	$('#fm-edit').form('submit', {
		url : AppCore.baseUrl + 'admin/system/role/' + $('#id-edit').val(),
		onSubmit : function(param) {
			return $(this).form('validate');
		},
		success : function(data) {
			var data = eval('(' + data + ')');
			if (data.success) {
				var node = $('#tree').tree('getSelected');
				if (node) {
					selected = node.id;
				}
				$('#tree').tree('reload');
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-edit').dialog('close');
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
					url : 'admin/system/role/batch?ids=' + ids,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							var node = $('#tree').tree('getSelected');
							if (node) {
								selected = node.id;
							}
							$('#tree').tree('reload');
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
	$('#dg-list').datagrid('reload', {
		params : '{ "org.id" : -1 }'
	});
}

function dlg_auth() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要授权的条目！', 'info');
	} else if (rows.length == 1) {
		// 载入权限树
		$('#tree-auth').tree({
			url : 'admin/system/perm/tree',
			method : 'get',
			lines : true,
			checkbox : true,
			onClick : function(node) {
				if (node.checked) {
					$(this).tree('uncheck', node.target);
				} else {
					$(this).tree('check', node.target);
				}
			},
			onLoadSuccess : function(node, data) {
				// 这里需要读取该角色已有的权限
				$.ajax({
					type : 'get',
					url : 'admin/system/role/' + rows[0].id + '/auth',
					dataType : 'json',
					async : false,
					success : function(data) {
						for (var i = 0; i < data.length; i++) {
							$('#tree-auth').tree('check', $('#tree-auth').tree('find', data[i].id).target);
						}
					}
				});
				$(this).tree('collapseAll');
			}
		});
		$('#id-auth').val(rows[0].id);
		$('#dlg-auth').dialog('open');
	} else {
		$.messager.alert('提示', '授权时只可以选择一个！', 'info');
	}
}
function func_auth() {
	var roleId = $('#id-auth').val();
	var perms = $('#tree-auth').tree('getChecked');
	var permIds = new Array();
	$.each(perms, function(i, perm) {
		permIds.push(perm.id);
	});
	$.ajax({
		type : 'post',
		url : 'admin/system/role/' + roleId + '/auth',
		data : {
			permIds : permIds.join(',')
		},
		dataType : 'json',
		async : true,
		success : function(data) {
			if (data.success) {
				// 授权不会影响页面数据，无需刷新
				$('#dlg-auth').dialog('close');
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