<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <#include "/common/common.ftl">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <#include "/common/header.ftl">

    <#include "/common/side.ftl">

    <div class="content-wrapper">

        <section class="content-header">

            <h1>参数管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="eventTable" title="参数配置列表" class="easyui-datagrid" url="/event/all" method="GET"
                       pagination="true" rownumbers="true" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id"  fitColumns="true">
                    <thead>
                    <tr>
                        <th field="eventType" width="200">参数类型</th>
                        <th field="eventValue" width="150">参数值</th>
                        <th data-options="field:'createdAt',width:200,formatter:dateFormatter">配置时间</th>
                        <th data-options="field:'updatedAt',width:200,formatter:dateFormatter">修改时间</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <a href="javascript:view();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 80px;">修改</a>
                </div>

                <div id="view-dlg" class="easyui-dialog" style="width: 500px; padding: 10px 20px;" closed="true" buttons="#view-dlg-buttons">
                    <form id="view-fm" method="post">
                        <input type="hidden" name="id" />
                        <table cellpadding="5">
                            <tr><td width="30%">参数类型：</td><td><input type="text" name="eventType" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>参数值：</td><td><input type="text" name="eventValue" class="easyui-textbox"  data-options="required:true" /></td></tr>
                        </table>
                    </form>
                </div>
                <div id="view-dlg-buttons">
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="edit()">修改</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#view-dlg').dialog('close');">关闭</a>
                </div>

            </div>

    </div>

    </section>


</div>

</div>
<script>
    $(function() {
        slideMenu('page_event');
    });
    function view() {
        var row = $('#eventTable').datagrid('getSelected');
        if (row) {
            $('#view-dlg').dialog('open').dialog('setTitle', '订单详细');
            $('#view-fm').form('load', row);
        } else {
            $.messager.alert('提示', '请选择一行操作！');
        }
    }
    function edit() {
        $('#view-fm').form('submit', {
            url: '/event/edit',
            onSubmit: function () {
                return $(this).form('validate');
            },
            success: function (result) {
                var result = eval('(' + result + ')');
                console.info(result);
                if (result.code == '200') {
                    $.messager.alert('成功', result.msg);
                    $('#view-dlg').dialog('close');
                    $('#eventTable').datagrid('reload');
                } else {
                    $.messager.alert('失败', result.msg);
                }
            }
        });
    }
</script>
</body>
</html>