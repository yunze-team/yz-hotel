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
            <h1>已同步酒店</h1>
        </section>
        <section class="content">
            <div>
                <table id="hotelTable" title="酒店列表" class="easyui-datagrid" url="/syn_info/syn_hotel_info" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="hotelId" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="dotwHotelCode" width="100">酒店代码</th>
                        <th field="hotelName" width="150">酒店名称</th>
                        <th field="supplier" width="100">供应商</th>
                        <th field="distributor" width="100">同步渠道</th>
                        <th field="syncStatus" width="150">同步状态</th>
                    </tr>
                    </thead>
                </table>
                <div id="toolbar" style="padding: 3px;">
                    <span>酒店代码：</span>
                    <input type="text" id="hotelCode" style="line-height:26px;border:1px solid #ccc" />
                    <span>酒店名称：</span>
                    <input type="text" id="hotelName" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                </div>
            </div>
        </section>
    </div>
</div>
<script>
    $(function() {
        slideMenu('sys_hotel_info');
    });

    function doSearch() {
        $('#hotelTable').datagrid('load', {
            hotelCode: $('#hotelCode').val(),
            hotelName:$('#hotelName').val()
        });
    }

</script>
</body>
</html>