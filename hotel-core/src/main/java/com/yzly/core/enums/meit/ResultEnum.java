package com.yzly.core.enums.meit;

/**
 * @author lazyb
 * @create 2019/12/17
 * @desc
 **/
public enum ResultEnum {
    SUCCESS(true, 0, "请求成功"),
    PARAM_ERROR_BA(false, 12, "美团认证校验失败"),
    PARAM_ERROR_BA_AUTH(false, 121, "美团认证校验失败,Authorization错误"),
    PARAM_ERROR_BA_PARTNER_ID(false, 123, "美团认证校验失败,partnerId错误"),
    PARAM_ERROR_BA_DATE(false, 122, "美团认证校验失败,Date 错误"),
    FAIL(false, 2, "系统异常"),
    NONE(false, 1, "无数据");

    private boolean result;
    private int resultCode;
    private String resultMsg;

    ResultEnum(boolean result, int resultCode, String resultMsg) {
        this.result = result;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public boolean getResult() {
        return result;
    }

}
