package com.yzly.admin.controller.dotw;

import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.service.dotw.HotelInfoService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyb
 * @create 2019/11/26
 * @desc
 **/
@RestController
@CommonsLog
public class HotelController {

    @Autowired
    private HotelInfoService hotelInfoService;

    @GetMapping("/hotel/list")
    public Map<String, Object> getAllHotel(int page, int rows, String country, String city,
                                           String region, String hotelCode, String hotelName) {
        Map<String, Object> resMap = new HashMap<>();
        HotelQuery hotelQuery = new HotelQuery();
        hotelQuery.setHotelNameEn(hotelName);
        hotelQuery.setCity(city);
        hotelQuery.setCountry(country);
        hotelQuery.setRegion(region);
        hotelQuery.setDotwHotelCode(hotelCode);
        Page<HotelInfo> ph = hotelInfoService.findAllByPageQuery(page, rows, hotelQuery);
        resMap.put("total", ph.getTotalElements());
        resMap.put("rows", ph.getContent());
        return resMap;
    }

}
