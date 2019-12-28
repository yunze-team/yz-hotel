package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit产品搜索查询视图
 */
@Setter
@Getter
public class GoodsSearchQuery {

    private String hotelIds;// 酒店ID，多个用“，”分隔
    private String roomId;// 房型ID，单产品查询时有值
    private String ratePlanCode;// 售卖计划，单产品查询时有值
    private String checkin;// 入住日期，格式为YYYY-MM-DD
    private String checkout;// 离店日期，格式为YYYY-MM-DD
    private Integer roomNumber;// 房间数
    private Integer numberOfAdults;// 成人数
    private Integer numberOfChildren;// 儿童数
    private String childrenAges;// 儿童年龄，以“,”分隔的数字组合
    private String currencyCode;// 币种，（海外）Currency code. ISO 4217（国内）默认为“CNY”

    public GoodsSearchQuery(String hotelIds, String roomId, String ratePlanCode,
                            String checkin, String checkout, Integer roomNumber,
                            Integer numberOfAdults, Integer numberOfChildren,
                            String childrenAges, String currencyCode) {
        this.hotelIds = hotelIds;
        this.roomId = roomId;
        this.ratePlanCode = ratePlanCode;
        this.checkin = checkin;
        this.checkout = checkout;
        this.roomNumber = roomNumber;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.childrenAges = childrenAges;
        this.currencyCode = currencyCode;
    }
}
