package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "order_detail_info_view")
public class OrderDetailInfo {
    @Id
    private Long id;

    private String hotelName;

    private String orderCode;

    private String priceValue;

    private String servicePriceValue;

    private String actualAdults;

    private String children;

    @Column(name = "name")
    private String roomName;

    private String orderStatus;

}
