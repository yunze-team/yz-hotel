package com.yzly.api.constants;

import com.yzly.api.annotations.*;
import com.yzly.core.domain.jielv.Auth;

@lombok.Data
public class XmlRequest {
    //系统头
    @Header(HeaderType.SYSHEADER)
    @Data("SYS_HEAD")
    private Auth auth;
    @Data("APP_HEAD")
    @Header(HeaderType.APPHEADER)
    private AppHeader appHeader;
    @Header(HeaderType.BODY)
    @com.yzly.api.annotations.Data
    private Object body;

}
