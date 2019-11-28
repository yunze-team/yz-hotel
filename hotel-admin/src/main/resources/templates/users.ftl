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

            <h1>用户管理</h1>

        </section>

        <section class="content">

            <div>

                <table id="usersTable" title="用户列表" class="easyui-datagrid" url="/users/list" method="GET"
                       pagination="true" rownumbers="false" singleSelect="true" toolbar="#toolbar" pageSize="20" idField="id" fitColumns="true">
                    <thead>
                    <tr>
                        <th field="id" width="100" sortable="true">用户ID</th>
                        <th field="userName" width="150">用户名</th>
                        <th field="phone" width="150">手机号</th>
                        <th field="bindInfo" width="200">绑定信息</th>
                        <th field="userType" width="100">用户类别</th>
                        <th field="createdAt" width="150">创建时间</th>
                        <th field="updatedAt" width="150">更新时间</th>
                    </tr>
                    </thead>
                </table>


            </div>

        </section>


    </div>

</div>
<script>
    $(function() {
        slideMenu('page_users');
    });

</script>
</body>
</html>