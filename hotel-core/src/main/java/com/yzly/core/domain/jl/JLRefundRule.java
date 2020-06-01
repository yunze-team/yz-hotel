package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅取消条款信息
 * @author lazyb
 * @create 2020/6/1
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_refund_rule")
public class JLRefundRule {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String refundRuleId;// 取消条款编号

    @Column
    private Integer refundRuleType;// 取消条款规则，1不可退，2限时取消

    @Column
    private Integer refundRuleHours;// 入住前n小时

    @Column
    private Integer deductType;// 取消客人罚金，1扣全额，0扣首晚房费

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
