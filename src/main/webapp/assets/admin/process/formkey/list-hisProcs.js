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
		url : 'admin/process/formkey/process/hisList',
		queryParams : {},
		method : 'get',
		columns : [[{
					"field" : "id",
					"title" : "实例ID"
				}, {
					// 流程实例名称存在BUG，不会记录到历史中
					"field" : "name",
					"hidden" : true
				}, {
					"field" : "processDefinitionId",
					"title" : "编号"
				}, {
					"field" : "processDefinitionKey",
					"title" : "KEY"
				}, {
					"field" : "processDefinitionName",
					"title" : "名称"
				}, {
					"field" : "startUserId",
					"title" : "发起用户ID"
				}, {
					"field" : "startUser",
					"title" : "发起员工"
				}, {
					"field" : "startTime",
					"title" : "发起时间",
					formatter : function(value, row, index) {
						if (value) {
							var date = new Date(value);
							return date.format('yyyy-MM-dd HH:mm:ss');
						}
					}
				}, {
					"field" : "endTime",
					"title" : "结束时间",
					formatter : function(value, row, index) {
						if (value) {
							var date = new Date(value);
							return date.format('yyyy-MM-dd HH:mm:ss');
						}
					}
				}, {
					"field" : "deleteReason",
					"title" : "描述",
					formatter : function(value, row, index) {
						if (value) {
							if (value == 'ACTIVITI_DELETED') {
								return '强制停止执行';
							} else {
								return value;
							}
						} else {
							if (row.endTime) {
								return '正常结束';
							} else {
								return '流转中';
							}
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

function func_task() {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		$('#dg-task').datagrid({
			striped : true,
			border : true,
			idField : 'id',
			rownumbers : true,
			fitColumns : true,
			singleSelect : true,
			url : 'admin/process/ins/' + row.id + '/tasks',
			queryParams : {},
			method : 'get',
			columns : [[{
						"field" : "id",
						"title" : "ID"
					}, {
						"field" : "taskDefinitionKey",
						"title" : "KEY"
					}, {
						"field" : "name",
						"title" : "名称"
					}, {
						"field" : "formKey",
						"title" : "FormKey"
					}, {
						"field" : "assignee",
						"title" : "办理用户ID"
					}, {
						"field" : "assigneeName",
						"title" : "办理人"
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
						"field" : "claimTime",
						"title" : "签收时间",
						formatter : function(value, row, index) {
							if (value) {
								var date = new Date(value);
								return date.format('yyyy-MM-dd HH:mm:ss');
							} else {
								return '自动签收';
							}
						}
					}, {
						"field" : "endTime",
						"title" : "结束时间",
						formatter : function(value, row, index) {
							if (value) {
								var date = new Date(value);
								return date.format('yyyy-MM-dd HH:mm:ss');
							}
						}
					}]],
			loadMsg : '数据载入中...'
		});
		$('#dlg-task').dialog('open');
	} else {
		$.messager.alert('提示', '请选择要查看的任务！', 'info');
	}
}

function func_data() {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		$('#dg-data').datagrid({
			striped : true,
			border : true,
			idField : 'id',
			rownumbers : true,
			fitColumns : true,
			singleSelect : true,
			url : 'admin/process/ins/' + row.id + '/datas',
			queryParams : {},
			method : 'get',
			columns : [[{
						"field" : "id",
						"title" : "ID"
					}, {
						"field" : "variableTypeName",
						"title" : "类型"
					}, {
						"field" : "name",
						"title" : "名称"
					}, {
						"field" : "value",
						"title" : "值"
					}]],
			loadMsg : '数据载入中...'
		});
		$('#dlg-data').dialog('open');
	} else {
		$.messager.alert('提示', '请选择要查看的任务！', 'info');
	}
}