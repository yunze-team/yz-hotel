<aside class="main-sidebar">

    <section class="sidebar">
        <ul class="sidebar-menu">
            <#if username == 'admin'>
                <li id="page_index">
                    <a href="/index">
                        <i class="fa fa-dashboard"></i> <span>首页</span>
                    </a>
                </li>
                <li id="page_hotel">
                    <a href="/index/hotel">
                        <i class="fa fa-folder"></i> <span>酒店管理</span>
                    </a>
                </li>
            <#--DOTW-->
                <li class="tree-view">
                    <a href="#">
                        <i class="fa fa-folder"></i> <span>DOTW</span>
                    </a>
                    <ul class="tree-view">
                        <li id="sys_hotel_info">
                            <a href="/index/syn_hotel_info">
                                <span>已同步酒店</span>
                            </a>
                        </li>
                        <li id="page_order">
                            <a href="/index/order_info">
                                <span>订单管理</span>
                            </a>
                        </li>
                    </ul>
                </li>
            <#--End-->
            <#--美团-->
                <li class="tree-view">
                    <a href="#">
                        <i class="fa fa-folder"></i> <span>美团</span>
                    </a>
                    <ul class="tree-view">
                        <li id="page_synHotel">
                            <a href="#">
                                <span>POI同步管理</span>
                            </a>
                        </li>
                        <li id="page_order">
                            <a href="#">
                                <span>自动加价管理</span>
                            </a>
                        </li>
                        <li id="page_order">
                            <a href="#">
                                <span>订单管理</span>
                            </a>
                        </li>
                    </ul>
                </li>
            <#--End-->


            </#if>
        </ul>
    </section>

</aside>