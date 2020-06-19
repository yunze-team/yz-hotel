package com.yzly.admin.controller.jl;

import com.yzly.core.domain.dotw.query.HotelQuery;
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
public class JLHotelController {

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

}
