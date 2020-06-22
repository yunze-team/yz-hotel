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

            <h1>捷旅城市管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="cityTable" title="城市列表" class="easyui-datagrid" url="/jl/city/list" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="cityId" width="100">城市ID</th>
                        <th field="cityCn" width="250">城市中文名称</th>
                        <th field="cityEn" width="200">城市英文名称</th>
                        <th field="countryId" width="100">国家ID</th>
                        <th field="countryCn" width="200">国家中文名称</th>
                        <th field="countryEn" width="250">国家英文名称</th>
                        <th field="stateId" width="100">身份ID</th>
                        <th field="stateCn" width="200">省份中文名称</th>
                        <th field="stateEn" width="200">省份英文名称</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>城市ID：</span>
                    <input type="text" id="cityId" style="line-height:26px;border:1px solid #ccc" />
                    <span>城市中文名称：</span>
                    <input type="text" id="cityCn" style="line-height:26px;border:1px solid #ccc" />
                    <span>国家ID：</span>
                    <input type="text" id="countryId" style="line-height:26px;border:1px solid #ccc" />
                    <span>国家中文名称：</span>
                    <input type="text" id="countryCn" style="line-height:26px;border:1px solid #ccc" />
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
                </div>


            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('jl_city');
    });

    function doSearch() {
        $('#cityTable').datagrid('load', {
            countryId: $('#countryId').val(),
            cityId: $('#cityId').val(),
            cityCn: $('#cityCn').val(),
            countryCn: $('#countryCn').val()
        });
    }

</script>
</body>
</html>