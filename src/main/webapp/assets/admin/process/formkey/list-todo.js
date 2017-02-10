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
		url : 'admin/process/formkey/task/todoList',
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
					"hidden" : true
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
					"field" : "processDefinitionId",
					"title" : "流程编号"
				}, {
					"field" : "processInstanceId",
					"title" : "流程实例ID"
				}, {
					"field" : "assignee",
					"title" : "已签收",
					formatter : function(value, row, index) {
						if (value) {
							return '是';
						} else {
							return '否';
						}
					}
				}]],
		loadMsg : '数据载入中...',
		onSelect : function(index, row) {
			if (row.assignee) {
				$('#btn-claim').linkbutton('disable');
			} else {
				$('#btn-claim').linkbutton('enable');
			}
		},
		onLoadSuccess : function(data) {
			$('#btn-claim').linkbutton('enable');
		}
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

function dlg_complete() {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		if (row.assignee) {
			var html = '<div style="text-align: center;">没有需要填写的表单</div>';
			$.ajax({
				type : 'get',
				url : 'admin/process/formkey/task/' + row.id + '/form',
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
			// 记录任务的信息
			$('#complete-taskId').val(row.id);
			$('#dlg-complete').dialog('open');
		} else {
			$.messager.alert('提示', '请先签收后再进行办理！', 'info');
		}
	} else {
		$.messager.alert('提示', '请选择要办理的任务！', 'info');
	}
}
function func_complete() {
	// 这里是利用html中的form进行提交，所以需要加上项目路径AppCore.baseUrl
	var taskId = $('#complete-taskId').val();
	$('#fm-current').form('submit', {
		url : AppCore.baseUrl + 'admin/process/formkey/task/' + taskId + '/complete',
		onSubmit : function(param) {
			return $(this).form('validate');
		},
		success : function(data) {
			var data = eval('(' + data + ')');
			if (data.success) {
				$('#dg-list').datagrid('reload');
				$('#dg-list').datagrid('clearSelections');
				$('#dlg-complete').dialog('close');
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

function func_claim() {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		$.ajax({
			type : 'post',
			url : 'admin/process/formkey/task/' + row.id + '/claim',
			dataType : 'json',
			async : false,
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
	} else {
		$.messager.alert('提示', '请选择要签收的任务！', 'info');
	}
}

function func_view(type) {
	var row = $('#dg-list').datagrid('getSelected');
	if (row) {
		var url = AppCore.baseUrl + 'admin/process/ins/' + row.processInstanceId + '/resource/' + type;
		if (type == 'img') {
			$('#dlg-view').dialog({
				content : '<img src="' + url + '"/>'
			}).dialog('open');
		}
	} else {
		$.messager.alert('提示', '请选择要预览的任务！', 'info');
	}
}