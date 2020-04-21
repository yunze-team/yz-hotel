package com.yzly.api.constants.xiecheng;

import com.yzly.api.annotations.*;
import lombok.Data;

@Data
public class XmlRequest {
    //系统头
    @Header(HeaderType.SYSHEADER)
    @XMLData("SYS_HEAD")
    private Auth auth;
    @Header(HeaderType.APPHEADER)
    @XMLData("APP_HEAD")
    private AppHeader appHeader;
    @Header(HeaderType.BODY)
    @XMLData
    private Object body;

}
