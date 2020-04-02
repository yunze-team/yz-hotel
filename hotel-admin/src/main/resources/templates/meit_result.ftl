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

            <h1>美团请求日志查询</h1>

        </section>

        <section class="content">

            <div>

                <table id="resultTable" title="美团请求列表" class="easyui-datagrid" url="/meit/result" method="GET"
                       pagination="true" rownumbers="true" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="traceId" width="300">请求编号</th>
                        <th field="code" width="50">返回code</th>
                        <th field="message" width="100">返回信息</th>
                        <th field="success" width="50">成功标志</th>
                        <th data-options="field:'createdAt',width:150,formatter:dateFormatter">请求时间</th>
                        <th data-options="field:'updatedAt',width:150,formatter:dateFormatter">响应时间</th>
                        <th field="reqMethod" width="150">请求方法</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>请求编号：</span>
                    <input type="text" id="traceId"  class="easyui-textbox" />
                    <span>请求方法：</span>
                    <input type="text" id="reqMethod"  class="easyui-textbox" />
                    <span>起始日期：</span>
                    <input type="text" id="startDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <span>结束日期：</span>
                    <input type="text" id="endDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                    <a href="javascript:view();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 80px;">查看</a>
                </div>

                <div id="view-dlg" class="easyui-dialog" style="width: 600px; padding: 10px 20px;" closed="true" buttons="#view-dlg-buttons">
                    <form id="view-fm" method="post">
                        <input type="hidden" name="id" />
                        <table cellpadding="5">
                            <tr><td width="30%">请求编号：</td><td><input type="text" name="traceId" class="easyui-textbox" style="width: 300px;" readonly /></td></tr>
                            <tr><td>本地编号：</td><td><input type="text" name="localTraceId" class="easyui-textbox" style="width: 300px;" readonly /></td></tr>
                            <tr><td>请求方法：</td><td><input type="text" name="reqMethod" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>请求时间：</td><td><input type="text" name="createdAt" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>响应时间：</td><td><input type="text" name="updatedAt" class="easyui-textbox" readonly></td></tr>
                            <tr><td>请求参数：</td><td><input type="text" name="req" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 100px;" readonly /></td></tr>
                            <tr><td>响应结果：</td><td><input type="text" name="resp" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 100px;" readonly /></td></tr>
                        </table>
                    </form>
                </div>
                <div id="view-dlg-buttons">
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#view-dlg').dialog('close');">关闭</a>
                </div>

            </div>

            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('meit_result');
    });
    function doSearch() {
        $('#resultTable').datagrid('load', {
            traceId: $('#traceId').val(),
            reqMethod: $('#reqMethod').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        });
    }
    function view() {
        var row = $('#resultTable').datagrid('getSelected');
        if (row) {
            $('#view-dlg').dialog('open').dialog('setTitle', '请求详细');
            $('#view-fm').form('load', row);
        } else {
            $.messager.alert('提示', '请选择一行操作！');
        }
    }
</script>
</body>
</html>