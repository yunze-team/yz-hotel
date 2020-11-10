package com.yzly.core.domain.ctrip;

import com.yzly.core.domain.dotw.enums.OrderStatus;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 携程订单信息实体
 * @author lazyb
 * @create 2020/10/29
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ctrip_order_info")
public class CtripOrderInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String ctripUniqueId;

    @Column(length = 30)
    private String ctripUniqueType;

    @Column(length = 50)
    private String roomTypeCode;

    @Column(length = 100)
    private String ratePlanCode;

    @Column
    private Integer numberOfUnits;

    @Column(length = 30)
    private String effectiveDate;

    @Column(length = 30)
    private String expireDate;

    @Column
    private Double amountAfterTax;

    @Column(length = 10)
    private String currencyCode;

    @Column(length = 50)
    private String hotelCode;

    @Column(length = 500)
    private String personInfos;

    @Column(length = 300)
    private String contactPerson;

    @Column(length = 300)
    private String guestCounts;

    @Column(length = 50)
    private String timeSpanEnd;

    @Column(length = 50)
    private String timeSpanStart;

    @Column
    private Double totalAmountAfterTax;

    @Column(length = 50)
    private String totalCurrencyCode;

    @Column(length = 500)
    private String hotelReservitaionIDs;

    @Column(length = 100)
    private String hotelConfirmNumber;

    @Column(length = 10)
    private String status;

    @Enumerated(EnumType.ORDINAL)
    @Column(length = 10)
    private OrderStatus orderStatus;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
