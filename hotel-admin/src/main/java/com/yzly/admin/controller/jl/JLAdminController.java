package com.yzly.admin.controller.jl;

import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.domain.jl.JLCity;
import com.yzly.core.domain.jl.JLHotelInfo;
import com.yzly.core.service.jl.JLAdminService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyb
 * @create 2020/6/19
 * @desc
 **/
@RestController
@CommonsLog
public class JLAdminController {

    @Autowired
    private JLAdminService jlAdminService;

    @GetMapping("/jl/hotel/list")
    public Map<String, Object> getAllHotel(int page, int rows, String countryId, String cityId,
                                           String nameCn, String nameEn, String hotelId) {
        Map<String, Object> resMap = new HashMap<>();
        HotelQuery hotelQuery = new HotelQuery();
        hotelQuery.setCountryId(countryId);
        hotelQuery.setCityId(cityId);
        hotelQuery.setHotelNameCn(nameCn);
        hotelQuery.setHotelNameEn(nameEn);
        hotelQuery.setHotelId(hotelId);
        Page<JLHotelInfo> ph = jlAdminService.findAllHotelByPage(page, rows, hotelQuery);
        resMap.put("total", ph.getTotalElements());
        resMap.put("rows", ph.getContent());
        return resMap;
    }

    @GetMapping("/jl/city/list")
    public Map<String, Object> getAllCity(int page, int rows, String countryId,
                                          String countryCn, String cityId, String cityCn) {
        Map<String, Object> resMap = new HashMap<>();
        HotelQuery hotelQuery = new HotelQuery();
        hotelQuery.setCountryId(countryId);
        hotelQuery.setCountryCn(countryCn);
        hotelQuery.setCityId(cityId);
        hotelQuery.setCityCn(cityCn);
        Page<JLCity> pc = jlAdminService.findAllCityByPage(page, rows, hotelQuery);
        resMap.put("total", pc.getTotalElements());
        resMap.put("rows", pc.getContent());
        return resMap;
    }

}
