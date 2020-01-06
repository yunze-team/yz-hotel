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
            <h1>订单管理</h1>
        </section>
        <section class="content">
            <div>
                <table id="hotelTable" title="订单列表" class="easyui-datagrid" url="/order/order_infos" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="hotelId" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="orderCode" width="100">订单编号</th>
                        <th field="hotelName" width="150">酒店名称</th>
                        <th field="roomName" width="100">房型</th>
                        <th field="actualAdults" width="100">人数</th>
                        <th field="orderStatus" width="150">状态</th>
                    </tr>
                    </thead>
                </table>
                <div id="toolbar" style="padding: 3px;">
                    <span>订单编号：</span>
                    <input type="text" id="orderCode" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                </div>
            </div>
        </section>
    </div>
</div>
<script>
    $(function() {
        slideMenu('order_info');
    });

    function doSearch() {
        $('#hotelTable').datagrid('load', {
            orderCode: $('#orderCode').val()
        });
    }

</script>
</body>
</html>