package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_city")
public class JLCity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer countryId;// 国家编号

    @Column(length = 50)
    private String countryCn;// 国家中文名

    @Column(length = 50)
    private String countryEn;// 国家英文名

    @Column
    private Integer stateId;// 省份编号

    @Column(length = 50)
    private String stateCn;// 省份中文名

    @Column(length = 50)
    private String stateEn;// 省份英文名

    @Column
    private Integer cityId;// 城市编号

    @Column(length = 50)
    private String cityCn;// 城市中文名

    @Column(length = 50)
    private String cityEn;// 城市英文名

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public JLCity(Integer countryId, String countryCn, String countryEn, Integer stateId, String stateCn, String stateEn, Integer cityId, String cityCn, String cityEn) {
        this.countryId = countryId;
        this.countryCn = countryCn;
        this.countryEn = countryEn;
        this.stateId = stateId;
        this.stateCn = stateCn;
        this.stateEn = stateEn;
        this.cityId = cityId;
        this.cityCn = cityCn;
        this.cityEn = cityEn;
    }
}
