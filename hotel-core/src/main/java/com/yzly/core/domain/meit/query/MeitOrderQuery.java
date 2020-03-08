package com.yzly.core.domain.meit.query;

import lombok.Getter;
import lombok.Setter;


/**
 * @author lazyb
 * @create 2020/3/7
 * @desc
 **/
@Setter
@Getter
public class MeitOrderQuery {

    private String hotelId;
    private String orderId;
    private String orderStatus;
    private String startDate;
    private String endDate;

}
