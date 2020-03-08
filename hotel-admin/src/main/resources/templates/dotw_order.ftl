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

            <h1>DOTW订单管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="orderTable" title="DOTW订单列表" class="easyui-datagrid" url="/dotw/order" method="GET"
                       pagination="true" rownumbers="true" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id">
                    <thead>
                    <tr>
                        <th field="id" width="50">ID</th>
                        <th field="orderId" width="150">订单编号</th>
                        <th field="hotelId" width="100">酒店编号</th>
                        <th field="roomTypeCode" width="100">房型编号</th>
                        <th data-options="field:'createdAt',width:150,formatter:dateFormatter">下单时间</th>
                        <th field="priceValue" width="100">成交价格（元）</th>
                        <th field="orderStatus" width="100">订单状态</th>
                        <th field="roomNum" width="50">订房数量</th>
                        <th field="actualAdults" width="50">成人数量</th>
                        <th field="children" width="50">儿童数量</th>
                        <th field="childrenAges" width="50">儿童年龄</th>
                        <th field="roomNum" width="50">订房数量</th>
                        <th field="fromDate" width="100">入住日期</th>
                        <th field="toDate" width="100">离店日期</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>酒店编号：</span>
                    <input type="text" id="hotelId"  class="easyui-textbox" />
                    <span>订单编号：</span>
                    <input type="text" id="orderId"  class="easyui-textbox" />
                    <span>订单状态：</span>
                    <select class="easyui-combobox" id="orderStatus" style="width:150px;">
                        <option value="">ALL</option>
                        <option value="FAILED">FAILED</option>
                        <option value="SAVED">SAVED</option>
                        <option value="CONFIRMED">CONFIRMED</option>
                        <option value="PRECANCLED">PRECANCLED</option>
                        <option value="CANCELED">CANCELED</option>
                    </select>
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
                            <tr><td width="30%">订单编号：</td><td><input type="text" name="orderId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>酒店编号：</td><td><input type="text" name="hotelId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>房型编号：</td><td><input type="text" name="roomTypeCode" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>订单状态：</td><td><input type="text" name="orderStatus" class="easyui-textbox" /></td></tr>
                            <tr><td>成交价格（元）：</td><td><input type="text" name="priceValue" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>下单房型价格编号：</td><td><input type="text" name="allocationDetails" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 50px;" readonly /></td></tr>
                            <tr><td>成人数量：</td><td><input type="text" name="actualAdults" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>儿童数量：</td><td><input type="text" name="children" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>儿童年龄：</td><td><input type="text" name="childrenAges" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>入住日期：</td><td><input type="text" name="fromDate" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>离店日期：</td><td><input type="text" name="toDate" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>客人信息：</td><td><input type="text" name="passengerInfos" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 100px;" readonly /></td></tr>
                            <tr><td>房间确认号：</td><td><input type="text" name="bookingReferenceNunber" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 50px;" readonly /></td></tr>
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
        slideMenu('dotw_order');
    });
    function doSearch() {
        $('#orderTable').datagrid('load', {
            hotelId: $('#hotelId').val(),
            orderId: $('#orderId').val(),
            orderStatus: $('#orderStatus').combobox('getValue'),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        });
    }
    function view() {
        var row = $('#orderTable').datagrid('getSelected');
        if (row) {
            $('#view-dlg').dialog('open').dialog('setTitle', '订单详细');
            $('#view-fm').form('load', row);
        } else {
            $.messager.alert('提示', '请选择一行操作！');
        }
    }
</script>
</body>
</html>