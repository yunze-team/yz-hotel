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
 * @create 2020/4/26
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_hotel_info")
public class JLHotelInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer hotelId;// 酒店编号

    @Column
    private Integer countryId;// 国家编号

    @Column
    private Integer stateId;// 身份编号

    @Column
    private Integer cityId;// 城市编号

    @Column
    private Integer star;// 酒店星级

    @Column(length = 200)
    private String hotelNameCn;// 酒店中文名

    @Column(length = 200)
    private String hotelNameEn;// 酒店英文名

    @Column(length = 500)
    private String addressCn;// 中文地址

    @Column(length = 500)
    private String addressEn;// 英文地址

    @Column(length = 50)
    private String phone;// 酒店总机

    @Column(length = 50)
    private String longitude;// 经度

    @Column(length = 50)
    private String latitude;// 纬度

    @Column
    private Integer instantConfirmation;// 是否即时确认，0：全部，1：即时确认，2：待查

    @Column
    private Integer sellType;// 是否热销酒店

    @Column(length = 50)
    private String updateTime;// 修改时间

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

}
