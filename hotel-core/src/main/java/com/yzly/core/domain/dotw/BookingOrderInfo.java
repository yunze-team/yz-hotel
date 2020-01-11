package com.yzly.core.domain.dotw;

import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.vo.Passenger;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Column(length = 100)
    private String childrenAges;

    @Column(length = 10)
    private String passengerNationality;

    @Column(length = 10)
    private String fromDate;

    @Column(length = 10)
    private String toDate;

    @Lob
    @Column
    private String passengerInfos;

    @Lob
    @Column
    private String specialRequests;

    @Column(length = 50)
    private String returnedCode;

    @Column(length = 50)
    private String bookingCode;

    @Column(length = 50)
    private String bookingReferenceNumber;

    @Column(length = 20)
    private String servicePrice;

    @Column(length = 20)
    private String servicePriceValue;

    @Column(length = 20)
    private String mealsPrice;

    @Column(length = 20)
    private String mealsPriceValue;

    @Column(length = 20)
    private String price;

    @Column(length = 20)
    private String priceValue;

    @Column(length = 20)
    private String penaltyApplied;

    @Column(length = 10)
    private String currency;

    @Column(length = 10)
    private String type;

    @Lob
    @Column
    private String voucher;

    @Lob
    @Column
    private String tariffNotes;

    @Column(length = 200)
    private String paymentGuaranteedBy;

    @Column(length = 200)
    private String emergencyContacts;

    @Lob
    @Column
    private String confirmationText;

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

    @Transient
    private List<Passenger> passengerList;

}
