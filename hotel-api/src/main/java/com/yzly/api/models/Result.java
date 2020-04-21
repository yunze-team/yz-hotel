package com.yzly.api.models;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 17:26
 **/


import com.yzly.api.annotations.Fields;
import com.yzly.api.annotations.Struct;

/**
 *@ClassName Body
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 17:26
 *@Version 1.0
 **/
@Struct
public class Result {

    @Fields(length = 10)
    private String retCode;
    @Fields(length = 512)
    private String retMsg;

    public Result() {
    }

    public Result(String retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    @Override
    public String toString() {
        return "Result{" +
                "retCode='" + retCode + '\'' +
                ", retMsg='" + retMsg + '\'' +
                '}';
    }
}
