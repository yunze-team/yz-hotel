package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * meit产品信息的房间数据
 */
@Setter
@Getter
public class Room {

    private Breakfast breakfast;// 早餐
    private String checkInTime;// 入住时间
    private String checkOutTime;// 离店时间
    private List<DayInfo> dayInfos;// 日期对应价格数组
    private String ratePlanCode;// 售卖计划
    private List<RefundRule> refundRules;// 退订规则数组
    private String roomId;// 房型id
    private String roomName;// 房型名称

}
