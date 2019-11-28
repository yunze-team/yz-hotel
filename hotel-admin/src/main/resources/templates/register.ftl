<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>昀泽酒店后台管理系统-注册</title>
    <link rel="stylesheet" href="/static/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/static/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="/static/iconicons/css/ionicons.min.css">
    <link rel="stylesheet" href="/static/css/AdminLTE.min.css">
    <script src="/static/js/jquery-2.2.3.min.js"></script>
    <script src="/static/bootstrap/js/bootstrap.js"></script>
</head>
<body class="hold-transition register-page">
<div class="register-box">
    <div class="register-logo">
        <a href="/index">昀泽酒店后台管理系统</a>
    </div>

    <div class="register-box-body">
        <p class="login-box-msg">注册一个新用户</p>
        <form action="/register" method="post">
            <div class="form-group has-feedback">
                <input class="form-control" placeholder="用户名" name="name">
                <span class="glyphicon glyphicon-user form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" class="form-control" placeholder="密码" name="password">
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <button type="submit" class="btn btn-primary btn-block btn-flat">注册</button>
                </div>
            </div>
        </form>

        <a href="/login" class="text-center">已有账户</a>

    </div>

</div>

</body>
</html>