package com.yzly.core.domain.meit.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lazyb
 * @create 2020/3/2
 * @desc
 **/
@Setter
@Getter
public class RefundEasyRule {

    private Integer fine;// 罚金

    @JSONField(serialize = false)
    private String fromDate;

    @JSONField(serialize = false)
    private String toDate;

}
