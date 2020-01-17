package com.yzly.core.domain.dotw.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lazyb
 * @create 2020/1/17
 * @desc
 **/
@Getter
@Setter
public class RoomPriceQuery {

    private String hotelCode;
    private String roomTypeCode;
    private String fromDate;
    private String toDate;

}
