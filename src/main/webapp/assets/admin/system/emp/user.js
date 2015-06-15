var userEditIndex = undefined;
function endUserEditing() {
	if (userEditIndex == undefined) {
		return true;
	}
	if ($('#dg-user').datagrid('validateRow', userEditIndex)) {
		$('#dg-user').datagrid('endEdit', userEditIndex);
		userEditIndex = undefined;
		return true;
	} else {
		return false;
	}
}
function onClickUserRow(index) {
	if (userEditIndex != index) {
		if (endUserEditing()) {
			$('#dg-user').datagrid('selectRow', index).datagrid('beginEdit', index);
			userEditIndex = index;
		} else {
			$('#dg-user').datagrid('selectRow', userEditIndex);
		}
	}
}
function appendUser() {
	if (endUserEditing()) {
		$('#dg-user').datagrid('appendRow', {
			enabled : 1
		});
		userEditIndex = $('#dg-user').datagrid('getRows').length - 1;
		$('#dg-user').datagrid('selectRow', userEditIndex).datagrid('beginEdit', userEditIndex);
	}
}
function removeUser() {
	if (userEditIndex == undefined) {
		return;
	}
	var row = $('#dg-user').datagrid('getSelected');
	if (row.id) {
		$.messager.confirm('提示', '确定删除已选择的条目？', function(r) {
			if (r) {
				var empId = $('#empId-user').val();
				$.ajax({
					type : 'delete',
					url : 'admin/system/emp/' + empId + '/user/' + row.id,
					dataType : 'json',
					async : true,
					success : function(data) {
						if (data.success) {
							$('#dg-user').datagrid('reload');
							$('#dg-user').datagrid('clearSelections');
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
		$('#dg-user').datagrid('cancelEdit', userEditIndex).datagrid('deleteRow', userEditIndex);
	}
	userEditIndex = undefined;
}

function dlg_user() {
	var rows = $('#dg-list').datagrid('getSelections');
	if (rows.length == 0) {
		$.messager.alert('提示', '请选择要分配的条目！', 'info');
	} else if (rows.length == 1) {
		// 载入用户的信息
		var empId = rows[0].id;
		$('#dg-user').datagrid({
			striped : true,
			border : true,
			idField : 'id',
			rownumbers : true,
			fitColumns : true,
			singleSelect : true,
			url : 'admin/system/emp/' + empId + '/user/data',
			method : 'get',
			columns : [[{
						field : 'loginName',
						width : 80,
						title : '用户名',
						editor : {
							type : 'validatebox',
							options : {
								required : true,
								validType : ['loginName']
							}
						}
					}, {
						field : 'loginPass',
						width : 80,
						title : '密码',
						editor : {
							type : 'validatebox',
							options : {
								required : true,
								validType : ['loginPass']
							}
						}
					}, {
						field : 'enabled',
						width : 30,
						title : '启用',
						formatter : function(value, row, index) {
							if (value == 1) {
								return '是';
							} else {
								return '否';
							}
						},
						editor : {
							type : 'combobox',
							options : {
								required : true,
								editable : false,
								panelHeight : 'auto',
								data : [{
											text : '是',
											value : 1
										}, {
											text : '否',
											value : -1
										}]
							}
						}
					}, {
						field : 'createDate',
						width : 80,
						title : '创建时间',
						formatter : function(value, row, index) {
							if (value) {
								var date = new Date(value);
								return date.format('yyyy-MM-dd');
							}
						}
					}, {
						field : 'loginRange',
						title : '范围',
						hidden : true
					}]],
			loadMsg : '数据载入中...',
			onClickRow : onClickUserRow
		});
		$('#empId-user').val(empId);
		$('#dlg-user').dialog('open');
	} else {
		$.messager.alert('提示', '分配时只可以选择一个！', 'info');
	}
}
function func_user() {
	if (endUserEditing()) {
		$('#dg-user').datagrid('acceptChanges');
		// 数据处理
		var data = $('#dg-user').datagrid('getData');
		var empId = $('#empId-user').val();
		$.ajax({
			type : 'post',
			url : 'admin/system/emp/' + empId + '/user',
			data : {
				objs : JSON.stringify(data.rows)
			},
			dataType : 'json',
			async : true,
			success : function(data) {
				if (data.success) {
					$('#dlg-user').dialog('close');
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