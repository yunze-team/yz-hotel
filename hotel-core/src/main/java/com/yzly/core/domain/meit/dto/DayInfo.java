package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * meit产品信息，日期对应产品价格
 */
@Setter
@Getter
public class DayInfo {

    private String date;
    private Integer basePrice;
    private Integer roomPrice;
    private Integer status;
    private Integer counts;

}
