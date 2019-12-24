package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 美团接口请求返回视图
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Getter
@Setter
public class MeitResult {

    private Integer code;
    private String message;
    private Boolean success;
    private Object data;

}