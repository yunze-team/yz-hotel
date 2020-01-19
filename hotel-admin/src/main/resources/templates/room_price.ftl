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

            <h1>房型30天价格列表</h1>

        </section>

        <section class="content">

            <div>

                <table id="priceTable" title="房型30天价格列表" class="easyui-datagrid" url="/price/list" method="GET"
                       pagination="true" rownumbers="true" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="hotelCode" width="100">酒店编号</th>
                        <th field="roomName" width="150">房型名称</th>
                        <th field="roomTypeCode" width="100">房型编号</th>
                        <th field="rateBasis" width="100">价格标准</th>
                        <th field="total" width="150">价格</th>
                        <th field="fromDate" width="100">入住日期</th>
                        <th field="toDate" width="100">离店日期</th>
                    </tr>
                    </thead>
                </table>

                <div id="toolbar" style="padding: 3px;">
                    <span>酒店编号：</span>
                    <input type="text" id="hotelCode" style="line-height:26px;border:1px solid #ccc" />
                    <span>房型编号：</span>
                    <input type="text" id="roomTypeCode" style="line-height:26px;border:1px solid #ccc" />
                    <span>入住日期：</span>
                    <input type="text" id="fromDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <span>离店日期：</span>
                    <input type="text" id="toDate" class="Wdate" onFocus="WdatePicker({lang:'zh-cn',dateFmt:'yyyy-MM-dd',readOnly:true})">
                    <a href="javascript:doSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width: 80px;">搜索</a>
<#--                    <a href="javascript:excel();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 100px;">生成excel</a>-->
                    <a href="javascript:download();" class="easyui-linkbutton" data-options="iconCls:'icon-edit'" style="width: 100px;">下载excel</a>
                </div>

            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('page_room_price');
    });
    function doSearch() {
        $('#priceTable').datagrid('load', {
            hotelCode: $('#hotelCode').val(),
            roomTypeCode: $('#roomTypeCode').val(),
            fromDate: $('#fromDate').val(),
            toDate: $('#toDate').val()
        });
    }
    function excel() {
        $.get(
            "/price/excel",
            function(data) {
                if (data.code == '200') {
                    $.messager.show({title:'成功', msg:data.msg});
                } else {
                    $.messager.show({title:'错误', msg:data.msg});
                }
            }
        );
    }
    function download() {
        var form=$("<form>");
        form.attr("style","display:none");
        form.attr("target","");
        form.attr("method","post");//提交方式为post
        form.attr("action","/sys/price_file_download");//定义action

        $("body").append(form);
        form.submit();        
    }
</script>
</body>
</html>