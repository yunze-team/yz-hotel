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
    private String renovationYear;
    private String cityCode;
    private String regionName;
    private String rating;
    private String direct;
    private String hotelCheckOut;
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
    private String leisure;
    private String preferred;
    private String images;
    private String chain;
    private String address;
    private String hotelPreference;
    private String business;
    private String hotelCheckIn;
    private String hotelName;
    private String amenitie;
    private String transportation;
    private String rails;
    private String cruises;
    private String airports;
    private String fireSafety;
    private String attraction;
    private String minAge;
    private String fullAddress;
    private String builtYear;
    private String luxury;
    private String location;
    private String stateCode;
    private String countryName;
    private String noOfRooms;

}
