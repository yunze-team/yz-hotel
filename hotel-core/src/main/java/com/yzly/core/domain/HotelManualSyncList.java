package com.yzly.core.domain;

import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SupplierEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 人工同步酒店列表
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "hotel_manual_sync_list")
public class HotelManualSyncList {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hotelId;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private SupplierEnum supplier;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private DistributorEnum distributor;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
