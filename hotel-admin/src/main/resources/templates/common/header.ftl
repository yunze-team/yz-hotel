<header class="main-header">
    <a href="/index" class="logo">
        <span class="logo-mini"><b>昀泽</b></span>
        <span class="logo-lg"><b>昀泽</b></span>
    </a>

    <nav class="navbar navbar-static-top">

        <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
                <li class="dropdown user user-menu">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                        <span class="hidden-xs">${Request["YZ_ADMIN_LOGIN_IDENTITY"].name}</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li class="user-footer">
                            <div class="pull-right">
                                <a href="/logout" class="btn btn-default btn-flat">退出</a>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>

    </nav>
</header>