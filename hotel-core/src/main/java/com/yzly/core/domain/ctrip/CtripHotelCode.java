package com.yzly.core.domain.ctrip;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 推送携程酒店编号映射实体
 * @author lazyb
 * @create 2020/11/17
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ctrip_hotel_code")
public class CtripHotelCode {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hotelCode;// 本地酒店code

    @Column(length = 50)
    private String ctripHotelCode;// 携程映射code

    @Column(length = 20)
    private String supplier;// 供应商

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
