package com.yzly.core.domain.meit.dto;

import java.util.List;

/**
 * meit产品信息返回map
 */
public class HotelMap {

    private String currencyCode;// 币种，默认"CNY"
    private List<Room> rooms;// 产品数据数组
    private String timeZone;// 时区

}
