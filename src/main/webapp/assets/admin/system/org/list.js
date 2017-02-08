var selected = null;
$(function() {
	$('#tree').tree({
		url : 'admin/system/org/tree',
		method : 'get',
		lines : true,
		onSelect : function(node) {
			var json = '{ "org.id" : ' + node.id + ' }';
			$('#dg-list').datagrid('clearSelections');
			$('#dg-list').datagrid('reload', {
				params : json
			});
			// 保留parentId信息
			$('#pid-add').val(node.id);
			$('#pname-add').val(node.text);
			$('#pid-edit').val(node.id);
			$('#pname-edit').val(node.text);
		},
		onLoadSuccess : function(node, data) {
			$(this).tree('collapseAll');
			if (selected) {
				$(this).tree('expandTo', $(this).tree('find', selected).target);
				$(this).tree('select', $(this).tree('find', selected).target);
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
		url : 'admin/system/org/data',
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
					title : '机构名称'
				}, {
					field : 'serialNum',
					title : '机构编码'
				}, {
					field : 'workPlace',
					title : '工作地点'
				}, {
					field : 'leader',
					title : '负责人'
				}, {
					field : 'contact',
					title : '联系电话'
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
	$('#dlg-add').dialog({
		onResize : function() {
			$('#dg-add').datagrid('resize');
		},
		onClose : function() {
			// 清空除外键关联以外的数据
			var pid = $('#pid-add').val();
			var pname = $('#pname-add').val();
			$('#fm-add').form('clear');
			$('#pid-add').val(pid);
			$('#pname-add').val(pname);
		}
	}).dialog('open');
}
function func_add() {
	// 数据处理
	var name = $('#fm-add input[name="name"]').val();
	var serialNum = $('#fm-add input[name="serialNum"]').val();
	var workPlace = $('#fm-add input[name="workPlace"]').val();
	var leader = $('#fm-add input[name="leader"]').val();
	var contact = $('#fm-add input[name="contact"]').val();
	var json =
		'[{' +
			'"name" : "' + name + '", ' +
			'"serialNum" : "' + serialNum + '", ' +
			'"workPlace" : "' + workPlace + '", ' +
			'"leader" : "' + leader + '", ' +
			'"contact" : "' + contact + '"' +
		'}]';
	$.ajax({
		type : 'post',
		url : 'admin/system/org/batch',
		data : {
			id : $('#pid-add').val(),
			objs : json
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

function dlg_edit() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要修改的条目！', 'info');
	} else if (rows.length == 1) {
		if (rows[0].org) {
			$('#pname-edit').val(rows[0].org.name);
		} else {
			$('#pname-edit').val('无');
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
		url : AppCore.baseUrl + 'admin/system/org/' + $('#id-edit').val(),
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
					url : 'admin/system/org/batch?ids=' + ids,
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