package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * meit产品信息返回map
 */
@Setter
@Getter
public class HotelMap {

    private String currencyCode;// 币种，默认"CNY"
    private List<Rooms> rooms;// 产品数据数组
    private String timeZone;// 时区

}
