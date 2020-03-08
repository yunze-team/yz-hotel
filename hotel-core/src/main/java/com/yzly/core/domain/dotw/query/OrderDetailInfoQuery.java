package com.yzly.core.domain.dotw.query;

import lombok.Data;

@Data
public class OrderDetailInfoQuery {

    private String orderCode;
    private String orderId;
    private String hotelId;
    private String startDate;
    private String endDate;
    private String orderStatus;

}
