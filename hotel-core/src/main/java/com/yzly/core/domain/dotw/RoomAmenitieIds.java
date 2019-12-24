package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * dotw房间便利设施列表
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_room_amenitie_ids")
public class RoomAmenitieIds {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String code;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String meitId;

    @Column(length = 100)
    private String meitDesc;

    @Column(length = 50)
    private String meitValue;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public RoomAmenitieIds(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
