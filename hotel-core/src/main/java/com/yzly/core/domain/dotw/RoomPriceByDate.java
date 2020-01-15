package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * dotw酒店房型价格，按天展示
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_room_price_by_date")
public class RoomPriceByDate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hotelCode;// dotw酒店code

    @Column(length = 50)
    private String roomTypeCode;// 房型code

    @Column(length = 100)
    private String roomName;// 房型名称

    @Column(length = 50)
    private String total;// 总价

    @Column(length = 20)
    private String rateBasis;// 适用价格

    @Column(length = 20)
    private String fromDate;// 开始时间

    @Column(length = 20)
    private String toDate;// 截止时间

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
