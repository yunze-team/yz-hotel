package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅订单信息实体
 * @author lazyb
 * @create 2020/6/5
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_order_info")
public class JLOrderInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String customerOrderCode;// 客户订单号

    @Column
    private Integer hotelId;// 酒店编号

    @Column(length = 50)
    private String keyId;// 产品编号

    @Column(length = 20)
    private String checkInDate;// 入住日期

    @Column(length = 20)
    private String checkOutDate;// 离店日期

    @Column(length = 100)
    private String nightlyPrices;// 每日价格，200.58|120.6|120.8

    @Column
    private Double totalPrice;// 总金额

    @Column(length = 500)
    private String hotelRemark;// 给酒店备注

    @Column(length = 50)
    private String orderCode;// 订单编号

    @Column
    private Integer orderStauts;// 订单状态，1：待确认，2：已确认，3：已拒单，4：已取消

    @Column
    private Integer bookingMessage;// 预定信息，0:正常可以预订，1:产品问题，2:房量不够，3:价格不符，4:不满足预订规则，5:满房，99:其它

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
