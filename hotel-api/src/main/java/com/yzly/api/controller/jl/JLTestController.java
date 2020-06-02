package com.yzly.api.controller.jl;

import com.yzly.api.common.JLHandler;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2020/4/15
 * @desc
 **/
@RestController
@RequestMapping(value = "/api/jl/test")
@CommonsLog
public class JLTestController {

    @Autowired
    private JLHandler jlHandler;

    @GetMapping("/city")
    @ResponseBody
    public Object cityQuery(int page) {
        return jlHandler.cityQuery(page);
    }

    @GetMapping("/hotel")
    @ResponseBody
    public Object hotelQuery(int country, int page) {
        return jlHandler.queryHotelList(country, page);
    }

    @GetMapping("/price")
    @ResponseBody
    public Object priceQuery(int hotelId, String checkInDate, String checkOutDate) {
        return jlHandler.queryHotelPriceByDefault(hotelId, checkInDate, checkOutDate, false);
    }

}
