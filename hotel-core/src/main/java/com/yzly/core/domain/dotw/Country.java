package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2019/11/20
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_country")
public class Country {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 20)
    private String code;

    @Column(length = 50)
    private String regionName;

    @Column(length = 20)
    private String regionCode;

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public Country(String name, String code, String regionName, String regionCode) {
        this.name = name;
        this.code = code;
        this.regionName = regionName;
        this.regionCode = regionCode;
    }
}
