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

            <h1>捷旅酒店管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="hotelTable" title="酒店列表" class="easyui-datagrid" url="/jl/hotel/list" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="hotelId" width="100">酒店编号</th>
                        <th field="hotelNameCn" width="250">酒店中文名称</th>
                        <th field="hotelNameEn" width="200">酒店英文名称</th>
                        <th field="cityId" width="100">城市ID</th>
                        <th field="countryId" width="100">国家ID</th>
                        <th field="phone" width="100">联系电话</th>
                        <th field="star" width="100">星级标准</th>
                        <th field="addressCn" width="200">酒店中文地址</th>
                        <th field="addressEn" width="200">酒店英文地址</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>酒店编号：</span>
                    <input type="text" id="hotelId" style="line-height:26px;border:1px solid #ccc" />
                    <span>国家ID：</span>
                    <input type="text" id="countryId" style="line-height:26px;border:1px solid #ccc" />
                    <span>城市ID：</span>
                    <input type="text" id="cityId" style="line-height:26px;border:1px solid #ccc" />
                    <span>酒店中文名称：</span>
                    <input type="text" id="nameCn" style="line-height:26px;border:1px solid #ccc" />
                    <span>酒店英文名称：</span>
                    <input type="text" id="nameEn" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                    <a href="javascript:manual();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" style="width: 160px;">同步酒店房型</a>
                </div>


            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('jl_hotel');
    });

    function doSearch() {
        $('#hotelTable').datagrid('load', {
            hotelId: $('#hotelId').val(),
            countryId: $('#countryId').val(),
            cityId: $('#cityId').val(),
            nameCn: $('#nameCn').val(),
            nameEn: $('#nameEn').val()
        });
    }

    function manual() {
        var row = $('#hotelTable').datagrid('getSelected');
        if (row) {
            var hotel_id = row.hotelId;
            $.post(
                "/jl/room/sync",
                {hotelId: hotel_id},
                function(result) {
                    if (result.code == '200') {
                        $.messager.alert('成功', result.msg);
                    } else {
                        $.messager.alert('失败', result.msg);
                    }
                }
            );
        } else {
            $.messager.alert('提示', '请选择一行操作！');
        }
    }

</script>
</body>
</html>