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
		url : 'admin/process/ins/data/his',
		queryParams : {},
		method : 'get',
		frozenColumns : [[{
					field : 'ck',
					checkbox : true
				}]],
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
							return '正常结束';
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
