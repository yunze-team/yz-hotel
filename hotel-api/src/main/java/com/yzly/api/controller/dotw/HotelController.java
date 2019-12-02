package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.HotelInfoApiService;
import com.yzly.core.service.jltour.JLHotelService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2019/11/27
 * @desc
 **/
@RestController
@RequestMapping(value = "/hotel")
@CommonsLog
public class HotelController {

    @Autowired
    private HotelInfoApiService hotelInfoApiService;
    @Autowired
    private JLHotelService jlHotelService;

    @GetMapping("/search")
    public Object searchHotel(String ids) {
        return hotelInfoApiService.searchHotel(ids);
    }

    @GetMapping("/all")
    public Object allHotel(String country, String city, int page, int size) {
        return hotelInfoApiService.searchHotelByCountryAndCity(country, city, page, size);
    }

    @GetMapping("/info")
    public Object allHotelInfo(String country, String city, int page, int size) {
        return hotelInfoApiService.searchHotelInfo(country, city, page, size);
    }

    @GetMapping("/rooms")
    public Object getRooms(String hid, String rid) {
        return hotelInfoApiService.getRoomsByHid(hid, rid);
    }

    @GetMapping("/allrooms")
    public Object allRooms(String city, int page, int size) {
        return hotelInfoApiService.searchRoomsForCity(city, page, size);
    }

    @PostMapping("/pull")
    public Object pullAll(String country, String city, int page, int size) {
        hotelInfoApiService.updateHotelRoomsAndInfo(country, city, page, size);
        return "SUCCESS";
    }

    @GetMapping("/jl")
    public Object jlsync() {
        try {
            jlHotelService.syncByExcel();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "SUCCESS";
    }

}
