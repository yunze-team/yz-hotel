package com.yzly.core.domain.ctrip;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 推送携程房型code映射实体
 * @author lazyb
 * @create 2020/11/17
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ctrip_room_type_code")
public class CtripRoomTypeCode {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String roomTypeCode;

    @Column(length = 50)
    private String ctripRoomTypeCode;

    @Column(length = 50)
    private String hotelCode;

    @Column(length = 20)
    private String supplier;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
