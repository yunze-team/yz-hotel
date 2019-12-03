package com.yzly.core.domain.dotw;

import com.yzly.core.domain.dotw.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单明细表，保存预定单的基本信息及住客的基本信息
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_booking_order_info")
public class BookingOrderInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20)
    private String hotelId;

    @Column(length = 20)
    private String roomTypeCode;

    @Column(length = 100)
    private String allocationDetails;

    @Column(length = 10)
    private String selectedRateBasis;

    @Column(length = 10)
    private String actualAdults;

    @Column(length = 10)
    private String children;

    @Column(length = 10)
    private String passengerNationality;

    @Column(length = 10)
    private String passengerSalutation;

    @Column(length = 50)
    private String passengerFirstName;

    @Column(length = 50)
    private String passengerLastName;

    @Column(length = 10)
    private String specialRequests;

    @Column(length = 50)
    private String returnedCode;

    @Column(length = 50)
    private String bookingCode;

    @Column(length = 50)
    private String bookingReferenceNumber;

    private String servicePrice;

    private String mealsPrice;

    private String price;

    private String currency;

    private String type;

    private String voucher;

    private String paymentGuaranteedBy;

    private String emergencySalutation;

    private String emergencyFullName;

    private String emergencyPhone;

    @Enumerated(EnumType.ORDINAL)
    @Column(length = 10)
    private OrderStatus orderStatus;

    @Column(length = 10)
    private String bookingStatus;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
