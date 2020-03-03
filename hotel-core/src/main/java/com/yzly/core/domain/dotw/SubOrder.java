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
 * 子订单表
 * @author lazyb
 * @create 2020/3/3
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_sub_order")
public class SubOrder {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long orderId;

    @Column(length = 200)
    private String paymentGuaranteedBy;

    @Column(length = 200)
    private String servicePrice;

    @Column(length = 200)
    private String servicePriceValue;

    @Column(length = 50)
    private String bookingCode;

    @Column(length = 50)
    private String bookingReferenceNumber;

    @Column(length = 200)
    private String price;

    @Column(length = 200)
    private String priceValue;

    @Column(length = 200)
    private String penaltyApplied;

    @Lob
    @Column
    private String voucher;

    @Column(length = 200)
    private String emergencyContacts;

    @Column(length = 10)
    private String bookingStatus;

    @Column(length = 200)
    private String mealsPrice;

    @Column(length = 200)
    private String mealsPriceValue;

    @Column(length = 10)
    private String type;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
