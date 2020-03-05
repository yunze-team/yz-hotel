package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户检索时缓存的
 * @author lazyb
 * @create 2020/3/5
 * @desc
 **/
@Document(collection = "user_cache_room_price_xml")
@Data
@NoArgsConstructor
@ToString
public class UserHotelRoomPriceXml {

    @Id
    private ObjectId id;

    private String hotelId;
    private String fromDate;
    private String toDate;
    private String currency;
    private String roomNumber;
    private String numberOfAdults;
    private String numberOfChildren;
    private String childrenAges;
    private String ratePlanCode;
    private String xmlResp;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
