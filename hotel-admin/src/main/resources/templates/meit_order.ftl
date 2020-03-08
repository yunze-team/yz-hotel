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

            <h1>美团订单管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="orderTable" title="美团订单列表" class="easyui-datagrid" url="/meit/order" method="GET"
                       pagination="true" rownumbers="true" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id">
                    <thead>
                    <tr>
                        <th field="orderId" width="150">订单编号</th>
                        <th field="hotelId" width="100">酒店编号</th>
                        <th field="roomId" width="100">房型编号</th>
                        <th field="rateBaseId" width="50">费率编号</th>
                        <th data-options="field:'createdAt',width:150,formatter:dateFormatter">下单时间</th>
                        <th field="totalPrice" width="100">下单价格（分）</th>
                        <th field="actualTotalPrice" width="100">实际成交价格（分）</th>
                        <th field="orderStatus" width="100">订单状态</th>
                        <th field="roomNum" width="50">订房数量</th>
                        <th field="numberOfAdults" width="50">成人数量</th>
                        <th field="numberOfChildren" width="50">儿童数量</th>
                        <th field="childrenAges" width="50">儿童年龄</th>
                        <th field="checkin" width="100">入住日期</th>
                        <th field="checkout" width="100">离店日期</th>
                        <th field="dotwOrderId" width="50">关联dotw订单ID</th>
                        <th field="orderAvailable" width="50">订单手动标识</th>
                        <th field="confirmationNumbers" width="200">手动房间确认号</th>
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
                        <option value="BOOKING">BOOKING</option>
                        <option value="BOOK_SUCCESS">BOOK_SUCCESS</option>
                        <option value="BOOK_FAIL">BOOK_FAIL</option>
                        <option value="CANCEL_SUCCESS">CANCEL_SUCCESS</option>
                        <option value="CANCEL_FAIL">CANCEL_FAIL</option>
                    </select>
                    <span>起始日期：</span>
                    <input type="text" id="startDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <span>结束日期：</span>
                    <input type="text" id="endDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                    <a href="javascript:view();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 80px;">查看</a>
                    <a href="javascript:manual();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" style="width: 160px;">手动完成订单</a>
                    <a href="javascript:dotw();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" style="width: 160px;">去dotw完成订单</a>
                    <a href="javascript:syncManual();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 160px;">同步dotw成功订单</a>
                    <a href="javascript:cancelOrder();" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" style="width: 100px;">取消订单</a>
                </div>

                <div id="sync_order_dlg" class="easyui-dialog" style="width: 500px; padding: 10px 20px;" closed="true" buttons="#sync-dlg-buttons">
                    <form id="sync_fm" method="post">
                        <input type="hidden" name="id" />
                        <table cellpadding="5">
                            <tr><td width="30%">订单编号：</td><td><input type="text" class="easyui-textbox" name="orderId" readonly /></td></tr>
                            <tr><td>DOTW订单ID：</td><td><input name="dotwOrderId" type="text" class="easyui-textbox" data-options="required:true" /></td></tr>
                        </table>
                    </form>
                </div>
                <div id="sync-dlg-buttons">
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="syncOrder()">手动下单</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#sync_order_dlg').dialog('close');">关闭</a>
                </div>

                <div id="manual_order_dlg" class="easyui-dialog" style="width: 500px; padding: 10px 20px;" closed="true" buttons="#manual-dlg-buttons">
                    <form id="manual_fm" method="post">
                        <input type="hidden" name="id" />
                        <table cellpadding="5">
                            <tr><td width="30%">订单编号：</td><td><input type="text" class="easyui-textbox" name="orderId" readonly /></td></tr>
                            <tr><td>实际下单价格（分）：</td><td><input id="manual_price" name="actualTotalPrice" type="text" class="easyui-textbox" data-options="required:true" /></td></tr>
                            <tr><td>房间确认号：</td><td><input name="confirmationNumbers" type="text" class="easyui-textbox" data-options="required:true" /></td></tr>
                        </table>
                    </form>
                </div>
                <div id="manual-dlg-buttons">
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="manualOrder()">手动下单</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#manual_order_dlg').dialog('close');">关闭</a>
                </div>

                <div id="dlg" class="easyui-dialog" style="width: 600px; padding: 10px 20px;" closed="true" buttons="#dlg-buttons">
                    <form id="fm" method="post">
                        <input type="hidden" name="id" />
                        <input type="hidden" name="orderStatus" id="order_status">
                        <table cellpadding="5">
                            <tr><td width="30%">订单编号：</td><td><input type="text" name="orderId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>酒店编号：</td><td><input type="text" name="hotelId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>房型编号：</td><td><input type="text" name="roomId" class="easyui-textbox" readonly></td></tr>
                            <tr><td>费率编号：</td><td><input type="text" name="rateBaseId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>下单价格（分）：</td><td><input type="text" name="totalPrice" class="easyui-textbox" readonly></td></tr>
                            <tr><td>房间确认号：</td><td><input type="text" name="confirmationNumbers" class="easyui-textbox" readonly></td></tr>
                            <tr><td>成人数量：</td><td><input type="text" name="numberOfAdults" class="easyui-textbox" readonly></td></tr>
                            <tr><td>入住日期：</td><td><input type="text" name="checkin" class="easyui-textbox" readonly></td></tr>
                            <tr><td>离店日期：</td><td><input type="text" name="checkout" class="easyui-textbox" readonly></td></tr>
                            <tr><td>客人信息（下单前需确认，需与成人数量一致）：</td><td><input type="text" name="guestInfo" class="easyui-textbox" data-options="multiline:true,required:true" style="width: 300px; height: 100px;" /></td></tr>
                            <tr>
                                <td>DOTW关联房型房价唯一编号（下单前需修改确认）：</td>
                                <td><input type="text" name="ratePlanCode" class="easyui-textbox" style="width: 250px;" required="true"></td>
                            </tr>
                        </table>
                    </form>
                </div>
                <div id="dlg-buttons">
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" onclick="dotwOrder()">DOTW下单</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cancel" onclick="$('#dlg').dialog('close');">关闭</a>
                </div>

                <div id="view-dlg" class="easyui-dialog" style="width: 600px; padding: 10px 20px;" closed="true" buttons="#view-dlg-buttons">
                    <form id="view-fm" method="post">
                        <input type="hidden" name="id" />
                        <input type="hidden" name="orderStatus" id="order_status">
                        <table cellpadding="5">
                            <tr><td width="30%">订单编号：</td><td><input type="text" name="orderId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>酒店编号：</td><td><input type="text" name="hotelId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>房型编号：</td><td><input type="text" name="roomId" class="easyui-textbox" readonly></td></tr>
                            <tr><td>费率编号：</td><td><input type="text" name="rateBaseId" class="easyui-textbox" readonly /></td></tr>
                            <tr><td>下单价格（分）：</td><td><input type="text" name="totalPrice" class="easyui-textbox" readonly></td></tr>
                            <tr><td>房间确认号：</td><td><input type="text" name="confirmationNumbers" class="easyui-textbox" readonly></td></tr>
                            <tr><td>成人数量：</td><td><input type="text" name="numberOfAdults" class="easyui-textbox" readonly></td></tr>
                            <tr><td>入住日期：</td><td><input type="text" name="checkin" class="easyui-textbox" readonly></td></tr>
                            <tr><td>离店日期：</td><td><input type="text" name="checkout" class="easyui-textbox" readonly></td></tr>
                            <tr><td>客人信息：</td><td><input type="text" name="guestInfo" class="easyui-textbox" data-options="multiline:true" style="width: 300px; height: 100px;" readonly /></td></tr>
                            <tr><td>DOTW关联房型房价唯一编号：</td><td><input type="text" name="ratePlanCode" class="easyui-textbox" style="width: 250px;" readonly /></td></tr>
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
        slideMenu('meit_order');
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
    function dotw() {
        var row = $('#orderTable').datagrid('getSelected');
        var order_status = row.orderStatus;
        if (order_status != 'BOOKING') {
            $.messager.alert('错误', '订单状态为BOOKING才可执行此操作!');
        } else {
            if (row) {
                $('#dlg').dialog('open').dialog('setTitle', '去DOTW下单');
                $('#fm').form('load', row);
            } else {
                $.messager.alert('提示', '请选择一行操作！');
            }
        }
    }
    function manual() {
        var row = $('#orderTable').datagrid('getSelected');
        var order_status = row.orderStatus;
        if (order_status != 'BOOKING') {
            $.messager.alert('错误', '订单状态为BOOKING才可执行此操作!');
        } else {
            if (row) {
                $('#manual_order_dlg').dialog('open').dialog('setTitle', '手动下单');
                $('#manual_fm').form('load', row);
            } else {
                $.messager.alert('提示', '请选择一行操作！');
            }
        }
    }
    function syncManual() {
        var row = $('#orderTable').datagrid('getSelected');
        var order_status = row.orderStatus;
        if (order_status != 'BOOKING') {
            $.messager.alert('错误', '订单状态为BOOKING才可执行此操作!');
        } else {
            if (row) {
                $('#sync_order_dlg').dialog('open').dialog('setTitle', '手动同步DOTW成功订单');
                $('#sync_fm').form('load', row);
            } else {
                $.messager.alert('提示', '请选择一行操作！');
            }
        }
    }
    function cancelOrder() {
        var row = $('#orderTable').datagrid('getSelected');
        var order_status = row.orderStatus;
        if (order_status != 'BOOK_SUCCESS') {
            $.messager.alert('错误', '订单状态为BOOK_SUCCESS才可执行此操作!');
        } else {
            if (row) {
                $.messager.confirm('确定', '确定要执行此操作吗？', function(r) {
                    if (r) {
                        $.post(
                            "/meit/order/cancel",
                            {orderId: row.orderId},
                            function(result) {
                                if (result.code == '200') {
                                    $.messager.alert('成功', result.msg);
                                    $('#orderTable').datagrid('reload');
                                } else {
                                    $.messager.alert('失败', result.msg);
                                }
                            }
                        );
                    }
                });
            } else {
                $.messager.alert('提示', '请选择一行操作！');
            }
        }
    }
    function dotwOrder() {
        $.messager.confirm('确定', '确定要执行此操作吗？', function(r) {
            if (r) {
                var order_status = $('#order_status').val();
                if (order_status != 'BOOKING') {
                    $.messager.alert('错误', '订单状态为BOOKING才可执行此操作!');
                }
                $('#fm').form('submit', {
                    url: '/meit/order/dotw',
                    onSubmit: function () {
                        return $(this).form('validate');
                    },
                    success: function (result) {
                        var result = eval('(' + result + ')');
                        console.info(result);
                        if (result.code == '200') {
                            $.messager.alert('成功', result.msg);
                            $('#dlg').dialog('close');
                            $('#orderTable').datagrid('reload');
                        } else {
                            $.messager.alert('失败', result.msg);
                        }
                    }
                });
            }
        });
    }
    function manualOrder() {
        $.messager.confirm('确定', '确定要执行此操作吗？', function(r) {
            if (r) {
                $('#manual_fm').form('submit', {
                    url: '/meit/order/manual',
                    onSubmit: function () {
                        return $(this).form('validate');
                    },
                    success: function (result) {
                        var result = eval('(' + result + ')');
                        console.info(result);
                        if (result.code == '200') {
                            $.messager.alert('成功', result.msg);
                            $('#manual_order_dlg').dialog('close');
                            $('#orderTable').datagrid('reload');
                        } else {
                            $.messager.alert('失败', result.msg);
                        }
                    }
                });
            }
        });
    }
    function syncOrder() {
        $.messager.confirm('确定', '确定要执行此操作吗？', function(r) {
            if (r) {
                $('#sync_fm').form('submit', {
                    url: '/meit/order/sync',
                    onSubmit: function () {
                        return $(this).form('validate');
                    },
                    success: function (result) {
                        var result = eval('(' + result + ')');
                        console.info(result);
                        if (result.code == '200') {
                            $.messager.alert('成功', result.msg);
                            $('#sync_order_dlg').dialog('close');
                            $('#orderTable').datagrid('reload');
                        } else {
                            $.messager.alert('失败', result.msg);
                        }
                    }
                });
            }
        });
    }
</script>
</body>
</html>