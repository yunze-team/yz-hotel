<!DOCTYPE html>
<html lang="zh-CN">
<head>
<#include "/common/common.ftl">
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">

    <#include "/common/header.ftl">

    <#include "/common/side.ftl">

    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                昀泽酒店后台管理系统
            </h1>
        </section>

        <section class="content">

            <div class="row">
                <h3>${now!}</h3>
            </div>

        </section>

    </div>

</div>
<script>
    $(function() {
        slideMenu('page_index');

    });
</script>
</body>
</html>