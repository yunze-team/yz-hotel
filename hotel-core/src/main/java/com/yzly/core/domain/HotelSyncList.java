package com.yzly.core.domain;

import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SupplierEnum;
import com.yzly.core.enums.SyncStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/12/17
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "hotel_sync_list")
public class HotelSyncList {

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

    @Column(length = 10)
    @Enumerated(EnumType.ORDINAL)
    private SyncStatus syncStatus;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}