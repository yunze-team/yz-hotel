package com.yzly.core.domain.meit;

import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 美团创建订单实体
 * @author lazyb
 * @create 2020/1/3
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "meit_order_booking_info")
public class MeitOrderBookingInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hotelId;// 酒店id

    @Column(length = 50)
    private String roomId;// 房型id

    @Column(length = 50)
    private String ratePlanCode;// 售卖计划

    @Column(length = 20)
    private String checkin;// 入住日期

    @Column(length = 20)
    private String checkout;// 离店日期

    @Column
    private Integer numberOfAdults;// 成人数

    @Column
    private Integer numberOfChildren;// 儿童数

    @Column(length = 50)
    private String childrenAges;// 儿童年龄

    @Column
    private Integer roomNum;// 预定房间数

    @Column(length = 50)
    private String totalPrice;// 订单总金额

    @Column(length = 10)
    private String currencyCode;// 币种

    @Lob
    @Column
    private String guestInfo;// 客人信息列表

    @Lob
    @Column
    private String orderInfo;// 订单信息

    @Lob
    @Column
    private String creditCard;// 信用卡信息

    @Column
    private Long orderId;// 美团酒店id

    @Column(length = 50)
    private String partnerOrderId;// 供应商订单id

    @Column(length = 50)
    private String agentOrderId;// 渠道商订单id

    @Column(length = 50)
    private String confirmationNumbers;// 房间确认号

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PlatformOrderStatusEnum orderStatus;// 订单状态

    @Column(length = 50)
    private String orderMessage;// 订单说明

    @Column
    private Integer penalty;// 罚金

    @Column
    private Integer actualTotalPrice;// 实际订单总金额

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    @Column
    private Long dotwOrderId;// 保存dotw_booking_info表的id数值

    @Column(length = 20)
    private String rateBaseId;// 下单时的ratebase

    @Column(length = 10)
    private String orderAvailable;// 表示订单是否确认，0已确认已下单，1未确认，没有去dotw真实下单

}
