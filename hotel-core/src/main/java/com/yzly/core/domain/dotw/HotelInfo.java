package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by toby on 2019/11/22.
 */
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_hotel_info")
public class HotelInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String region;

    @Column(length = 50)
    private String country;

    @Column(length = 20)
    private String shortCountryName;

    @Column(length = 10)
    private String countryCode;

    @Column(length = 50)
    private String city;

    @Column(length = 10)
    private String cityCode;

    @Column(length = 20)
    private String dotwHotelCode;

    @Column(length = 20)
    private String hotelbedsHotelCode;

    @Column(length = 20)
    private String expediaHotelCode;

    @Column(length = 100)
    private String hotelName;

    @Column(length = 255)
    private String hotelNameCn;

    @Column(length = 500)
    private String hotelAddressCn;

    @Column(length = 50)
    private String starRating;

    @Column(length = 100)
    private String reservationTelephone;

    @Column(length = 500)
    private String hotelAddress;

    @Column(length = 50)
    private String latitude;

    @Column(length = 50)
    private String longitude;

    @Column(length = 100)
    private String chainName;

    @Column(length = 50)
    private String brandName;

    @Column(length = 10)
    private String newProperty;

    @Column(length = 10)
    private String isUpdate;

    @Column(length = 20)
    private String syncRoomPriceDate;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
