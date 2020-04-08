package com.yzly.api.annotations;

/**
 * @Description:TODO
 * @Auther tanyuan
 * version V1.0
 **/
public enum HeaderType{
    APPHEADER("app-header"),
    SYSHEADER("sys-header"),
    LOCALHEADER("local-header"),
    BODY("body");

    private final String value;
    private HeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
