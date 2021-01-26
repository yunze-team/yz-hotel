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

            <h1>捷旅房型管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="hotelTable" title="房型列表" class="easyui-datagrid" url="/jl/room/list" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="hotelId" width="100">酒店编号</th>
                        <th field="roomTypeCn" width="200">房型中文名称</th>
                        <th field="roomTypeEn" width="200">房型英文名称</th>
                        <th field="roomTypeId" width="100">房型ID</th>
                        <th field="bedType" width="100">床类别</th>
                        <th field="bedName" width="100">床名称</th>
                        <th field="basisRoomCn" width="200">基础房型名称</th>
                        <th field="basisRoomId" width="100">基础房型ID</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>酒店编号：</span>
                    <input type="text" id="hotelId" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                </div>


            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('jl_room');
    });

    function doSearch() {
        $('#hotelTable').datagrid('load', {
            hotelId: $('#hotelId').val()
        });
    }

</script>
</body>
</html>