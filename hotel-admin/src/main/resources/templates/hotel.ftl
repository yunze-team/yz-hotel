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

            <h1>酒店管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="hotelTable" title="酒店列表" class="easyui-datagrid" url="/hotel/list" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="dotwHotelCode" width="100">酒店代码</th>
                        <th field="hotelName" width="150">酒店名称</th>
                        <th field="reservationTelephone" width="150">预定电话</th>
                        <th field="region" width="100">所属地区</th>
                        <th field="country" width="100">所属国家</th>
                        <th field="city" width="100">所属城市</th>
                        <th field="starRating" width="150">星级标准</th>
                        <th field="hotelAddress" width="200">酒店地址</th>
                        <th field="chainName" width="150">连锁名称</th>
                        <th field="brandName" width="150">品牌名称</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>酒店代码：</span>
                    <input type="text" id="hotelCode" style="line-height:26px;border:1px solid #ccc" />
                    <span>所属国家：</span>
                    <input type="text" id="country" style="line-height:26px;border:1px solid #ccc" />
                    <span>所属城市：</span>
                    <input type="text" id="city" style="line-height:26px;border:1px solid #ccc" />
                    <span>所属地区：</span>
                    <input type="text" id="region" style="line-height:26px;border:1px solid #ccc" />
                    <span>品牌名称：</span>
                    <input type="text" id="brandName" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                </div>


            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('page_hotel');
    });

    function doSearch() {
        $('#hotelTable').datagrid('load', {
            hotelCode: $('#hotelCode').val(),
            country: $('#country').val(),
            city: $('#city').val(),
            region: $('#region').val(),
            brandName: $('#brandName').val()
        });
    }

</script>
</body>
</html>