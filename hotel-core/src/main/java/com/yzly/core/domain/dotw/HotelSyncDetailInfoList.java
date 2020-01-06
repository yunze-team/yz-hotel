package com.yzly.core.domain.dotw;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "sync_hotel_info_view")
public class HotelSyncDetailInfoList {

    @Id
    private String hotelId;

    private String hotelName;

    @Column(name = "dotw_hotel_code")
    private String dotwHotelCode;

    private String supplier;

    private String distributor;

    private String syncStatus;

}
