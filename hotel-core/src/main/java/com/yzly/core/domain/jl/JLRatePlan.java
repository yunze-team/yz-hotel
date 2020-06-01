package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅产品售卖计划
 * @author lazyb
 * @create 2020/6/1
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_rate_plan")
public class JLRatePlan {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer hotelId;// 酒店编号

    @Column(length = 50)
    private String roomTypeId;// 房型编号

    @Column(length = 50)
    private String keyId;// 唯一产品编号

    @Column
    private Integer supplierId;// 供应商ID

    @Column(length = 100)
    private String keyName;// 产品名称

    @Column(length = 50)
    private String bedName;// 床型名称

    @Column
    private Integer maxOccupancy;// 最大入住人数

    @Column(length = 10)
    private String currency;// 币种

    @Column(length = 50)
    private String rateTypeId;// 价格类型编号

    @Column
    private Integer paymentType;// 付款类型，0预付

    @Column
    private Integer breakfast;// 早餐，1一份，2两份，..99床位早，-1含早（不确定早餐份数）

    @Column
    private Integer ifInvoice;// 是否开票，1可开票，2不可开票

    @Column(length = 50)
    private String bookingRuleId;// 预定条款编号

    @Column(length = 50)
    private String refundRuleId;// 取消条款编号

    @Column(length = 50)
    private String market;// 适用市场，适用市场|不适用市场，不适用市场，分隔符前或后为-1，表示不限制

    @Column(length = 500)
    private String promotions;// 礼包信息

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
