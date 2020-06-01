package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅预定条款信息
 * @author lazyb
 * @create 2020/6/1
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_booking_rule")
public class JLBookingRule {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String bookingRuleId;// 预定条款编号

    @Column(length = 30)
    private String startDate;// 开始日期

    @Column(length = 30)
    private String endDate;// 结束日期

    @Column
    private Integer minAmount;// 预定最少数量

    @Column
    private Integer maxAmount;// 预定最多数量

    @Column
    private Integer minAdvHours;// 最少提前预定时间

    @Column
    private Integer maxAdvHours;// 最大提前预定时间

    @Column(length = 50)
    private String weekSet;// 有效星期

    @Column(length = 30)
    private String startTime;// 每日开始销售时间

    @Column(length = 30)
    private String endTime;// 每日结束销售时间

    @Column(length = 300)
    private String bookingNotices;// 预定说明

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
