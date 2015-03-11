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
		url : 'admin/system/perm/data',
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
		columns : [[{
					field : 'name',
					title : '权限名称'
				}, {
					field : 'token',
					title : '权限标识'
				}, {
					field : 'url',
					title : '链接地址'
				}]],
		// queryParams:{},
		loadMsg : '数据载入中...'
	});
	$('#dg-list').datagrid('getPager').pagination({
		beforePageText : '第',
		afterPageText : '页    共 {pages} 页',
		displayMsg : '当前显示 {from} - {to} 条记录    共 {total} 条记录'
	});
	$('#tree').tree({
		url : 'admin/system/perm/tree',
		method : 'get',
		onLoadSuccess : function(node, data) {
			// 处理右侧大小超出页面范围的问题
			$('#dg-list').datagrid('resize');
		},
		onClick : function(node) {
		}
	});
});

function search(value, name) {
	var json = '{"' + name + '" : "' + value + '"}';
	$('#dg').datagrid({
		queryParams : {
			"params" : json
		}
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
					title : '权限名称',
					editor : {
						type : 'validatebox',
						options : {
							required : true
						}
					}
				}, {
					field : 'token',
					width : 100,
					title : '权限标识',
					editor : {
						type : 'validatebox',
						options : {
							required : true
						}
					}
				}, {
					field : 'url',
					width : 150,
					title : '链接地址',
					editor : {
						type : 'validatebox'
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
		var data = jQuery('#dg-add').datagrid('getData');
		$.ajax({
			type : 'post',
			url : 'admin/system/perm/batch',
			data : {
				objs : JSON.stringify(data.rows)
			},
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					$('#tree').tree('reload');
					$('#dg-list').datagrid('reload');
					$('#dlg-add').dialog('close');
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
}

function dlg_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要修改的条目！', 'info');
	} else if (rows.length == 1) {
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
		url : AppCore.baseUrl + 'admin/system/perm/' + $('#id-edit').val(),
		onSubmit : function(param) {
			return $(this).form('validate');
		},
		success : function(data) {
			var result = eval('(' + data + ')');
			if (result.success) {
				$('#tree').tree('reload');
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-edit').dialog('close');
			} else {
				$.messager.show({
					title : '错误',
					msg : result.msg
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
					url : 'admin/system/perm/batch?ids=' + ids,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							$('#tree').tree('reload');
							$('#dg-list').datagrid('reload');
							$('#dg-list').datagrid('clearSelections');
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
		});
	} else {
		$.messager.alert('提示', '请选择要删除的条目！', 'info');
	}
}