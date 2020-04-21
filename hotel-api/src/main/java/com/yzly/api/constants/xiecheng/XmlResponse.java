package com.yzly.api.constants.xiecheng;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 17:01
 **/


import com.yzly.api.annotations.*;
import lombok.ToString;

/**
 *@ClassName ESBXmlResponse
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 17:01
 *@Version 1.0
 **/
@lombok.Data
@ToString
public class XmlResponse {

    //系统头
    @Header(HeaderType.SYSHEADER)
    private Auth auth;
    @Header(HeaderType.APPHEADER)
    private AppHeader appHeader;
    @Header(HeaderType.BODY)
    private Object body;

    public XmlResponse() {
    }

    public XmlResponse(Auth auth, AppHeader appHeader, Object body) {
        this.auth = auth;
        this.appHeader = appHeader;
        this.body = body;
    }
}
