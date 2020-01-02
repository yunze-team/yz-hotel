package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * meit订单创建参数视图
 * @author lazyb
 * @create 2019/12/31
 * @desc
 **/
@Setter
@Getter
public class OrderCreateParam {

    private String hotelId;// 酒店id
    private String roomId;// 房型id
    private String ratePlanCode;// 售卖计划
    private String checkin;// 入住日期
    private String checkout;// 离店日期
    private Integer numberOfAdults;// 成人数
    private Integer numberOfChildren;// 儿童数
    private String childrenAges;// 儿童年龄
    private Integer roomNum;// 预定房间数
    private String totalPrice;// 订单总金额
    private String currencyCode;// 币种
    private List<GuestInfo> guestInfo;// 客人信息列表
    private OrderInfo orderInfo;// 订单信息
    private CreditCard creditCard;// 信用卡信息

}
