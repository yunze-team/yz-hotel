package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit产品信息的早餐数据
 */
@Setter
@Getter
public class Breakfast {

    private Integer count; // 免费早餐数量
    private String desc; // 早餐描述

}
