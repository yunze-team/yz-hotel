package com.yzly.core.domain.jielv.models;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class City {
   // 国家编号
    private Integer countryId;
    //省份编号
    private Integer stateId;
   // 城市编号
    private Integer cityId;
}
