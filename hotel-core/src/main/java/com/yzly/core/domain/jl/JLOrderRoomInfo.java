package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅订单关联的房间入住信息
 * @author lazyb
 * @create 2020/6/5
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_order_room_info")
public class JLOrderRoomInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long jlOrderId;// 关联的捷旅订单id

    @Column
    private Integer adults;// 成人数

    @Column
    private Integer children;// 儿童数

    @Column(length = 100)
    private String childAges;// 儿童年龄，多个是用逗号分隔

    @Column(length = 500)
    private String checkInPersions;// 入住人姓名json串，lastName,firstName,nationality

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
