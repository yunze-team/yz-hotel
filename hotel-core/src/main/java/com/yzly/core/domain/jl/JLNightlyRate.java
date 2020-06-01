package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅产品夜间价格（每日）
 * @author lazyb
 * @create 2020/6/1
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_nightly_rate")
public class JLNightlyRate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String roomTypeId;// 房型编号

    @Column(length = 50)
    private String RatePlankeyId;// 唯一产品编号

    @Column(length = 50)
    private String formulaTypen;// 配额类型

    @Column(length = 15)
    private String date;// 日期

    @Column
    private Double cose;// 价格

    @Column
    private Integer status;// 房态，1保留房，2待查，3满房，4限时确认

    @Column
    private Integer currentAlloment;// 库存数量

    @Column
    private Integer breakfast;// 早餐

    @Column(length = 50)
    private String bookingRuleId;// 预定条款编号

    @Column(length = 50)
    private String refundRuleId;// 取消条款编号

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
