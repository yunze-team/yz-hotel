package com.yzly.core.domain.jl;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 捷旅-房型数据实体
 * @author lazyb
 * @create 2021/1/25
 * @desc
 **/
@Data
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "jl_room_type")
public class JLRoomType {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Integer hotelId;// 酒店编号

    @Column(length = 200)
    private String roomTypeEn;// 房型英文名

    @Column(length = 200)
    private String roomTypeCn;// 房型中文名

    @Column
    private Integer basisRoomId;// 基础房型编号

    @Column(length = 10)
    private String roomTypeId;// 房间类型编号

    @Column
    private Integer maximize;// 最大入住人数

    @Column
    private Integer extraBedtState;// 加床状态

    @Column(length = 100)
    private String roomRemark;// 房型备注

    @Column(length = 100)
    private String bedWidth;// 床尺寸

    @Column(length = 50)
    private String bedName;// 床型名称

    @Column(length = 100)
    private String basisRoomCn;// 基础房型中文名称

    @Column(length = 100)
    private String floorDistribute;// 楼层分布

    @Column(length = 100)
    private String facilities;// 设施编号

    @Column(length = 50)
    private String bedType;// 床类型

    @Column
    private Integer bedCount;// 加床数量

    @Column(nullable = false, updatable = false, name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false, name = "updated_at")
    @LastModifiedDate
    private Date updatedAt;

    public JLRoomType(Integer hotelId, String roomTypeEn, String roomTypeCn, Integer basisRoomId,
                      String roomTypeId, Integer maximize, Integer extraBedtState, String roomRemark,
                      String bedWidth, String bedName, String basisRoomCn, String floorDistribute,
                      String facilities, String bedType, Integer bedCount) {
        this.hotelId = hotelId;
        this.roomTypeEn = roomTypeEn;
        this.roomTypeCn = roomTypeCn;
        this.basisRoomId = basisRoomId;
        this.roomTypeId = roomTypeId;
        this.maximize = maximize;
        this.extraBedtState = extraBedtState;
        this.roomRemark = roomRemark;
        this.bedWidth = bedWidth;
        this.bedName = bedName;
        this.basisRoomCn = basisRoomCn;
        this.floorDistribute = floorDistribute;
        this.facilities = facilities;
        this.bedType = bedType;
        this.bedCount = bedCount;
    }
}
