package com.yzly.core.domain.dotw.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lazyb
 * @create 2019/11/27
 * @desc
 **/
@Getter
@Setter
public class HotelQuery {

    private String country;
    private String city;
    private String brandName;
    private String region;
    private String dotwHotelCode;
    private String isUpdate;
    private String syncRoomPriceDate;
    private Boolean isUpdateFlag = false;

    private String cityId;
    private String countryId;
    private String hotelId;
    private String hotelNameCn;
    private String hotelNameEn;

    private String cityCn;
    private String countryCn;

}
