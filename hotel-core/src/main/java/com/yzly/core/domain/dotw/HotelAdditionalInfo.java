package com.yzly.core.domain.dotw;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lazyb
 * @create 2019/11/29
 * @desc
 **/
@Document(collection = "dotw_hotel_additional_info")
@Data
@NoArgsConstructor
@ToString
public class HotelAdditionalInfo {

    @Id
    private ObjectId id;

    private String hotelId;
    private String zipCode;
    private String rooms;
    private String renovationYear;// 装修年份
    private String cityCode;
    private String regionName;
    private String rating;// 评分
    private String direct;
    private String hotelCheckOut;// 离店时间
    private String description2;
    private String geoPoint;
    private String description1;
    private String lastUpdated;
    private String regionCode;
    private String floors;
    private String cityName;
    private String stateName;
    private String locationId;
    private String countryCode;
    private String hotelPhone;
    private String leisure;// 悠闲设施
    private String preferred;// 偏好
    private String images;
    private String chain;
    private String address;
    private String hotelPreference;
    private String business;
    private String hotelCheckIn;// 入住时间
    private String hotelName;
    private String amenitie;// 设施
    private String transportation;
    private String rails;
    private String cruises;
    private String airports;
    private String fireSafety;
    private String attraction;
    private String minAge;
    private String fullAddress;
    private String builtYear;// 开业年份
    private String luxury;// 豪华设施
    private String location;
    private String stateCode;
    private String countryName;
    private String noOfRooms;// 房间数量

}
