package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author lazyb
 * @create 2019/11/29
 * @desc
 **/
@Data
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dotw_room_type")
public class RoomType {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String hotelId;// 对应dotw_hotel_code

    @Column(length = 50)
    private String roomTypeCode;

    @Column(length = 10)
    private String twin;// 表示是否双床

    @Column(length = 200)
    private String name;

    @Lob
    @Column
    private String roomAmenities;// 房间设施列表

    @Lob
    @Column
    private String roomInfo;// 房间基本信息列表

    @Lob
    @Column
    private String roomCapacityInfo;// 房间容量信息列表

    private String vendor;

}
