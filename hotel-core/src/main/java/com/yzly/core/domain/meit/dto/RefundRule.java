package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit产品信息，房间退订规则
 */
@Setter
@Getter
public class RefundRule {

    private Boolean returnable;// 是否可退订
    private Integer refundType;// 0不扣款，1按固定金额扣款，2按比例扣款，3按几晚扣款
    private Integer fine;// 罚金
    private Integer percent;// 罚金比例
    private Integer nights;// 罚扣晚数
    private String refundDesc;// 退订规则描述
    private Integer maxHoursBeforeCheckIn;// 退订规则开始时间
    private Integer minHoursBeforeCheckIn;// 退订规则结束时间

}
