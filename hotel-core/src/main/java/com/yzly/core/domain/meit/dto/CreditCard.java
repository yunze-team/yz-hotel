package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit订单创建视图中的信用卡信息视图
 * @author lazyb
 * @create 2020/1/2
 * @desc
 **/
@Setter
@Getter
public class CreditCard {

    private String creditCardNumber;// 信用卡号
    private String holderName;// 持卡人姓名
    private String expire;// 信用卡过期日期
    private String creditCardIdentifier;// 信用卡cvc码
    private String validFrom;// 刷卡有效期（开始）
    private String validTo;// 刷卡有效期（结束）
    private String timeZone;// 时区

}
