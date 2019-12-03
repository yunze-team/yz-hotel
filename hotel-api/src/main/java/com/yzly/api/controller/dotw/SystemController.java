package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.HotelInfoApiService;
import com.yzly.api.service.dotw.InternalCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2019/11/24
 * @desc
 **/
@RestController
@RequestMapping(value = "/sys")
public class SystemController {

    @Autowired
    private InternalCodeService internalCodeService;
    @Autowired
    private HotelInfoApiService hotelInfoApiService;

    @GetMapping("/synccountries")
    public String syncCountries() {
        internalCodeService.syncCountries();
        return "SUCCESS";
    }

    @GetMapping("/countries")
    public Object allCountries() {
        return internalCodeService.getAllCountries();
    }

    @GetMapping("/cities")
    public Object allCities() {
        return internalCodeService.getAllCities();
    }

    @GetMapping("/synccities")
    public String syncCities() {
        internalCodeService.syncCities();
        return "SUCCESS";
    }

    @GetMapping("/currencies")
    public Object allcurrencies() {
        return internalCodeService.getAllCurrencies();
    }

    @GetMapping("/synccurrencies")
    public String syncCurrencies() {
        internalCodeService.syncCurrencies();
        return "SUCCESS";
    }

    @GetMapping("/synchotels")
    public String allHotels() {
        hotelInfoApiService.syncBasicData();
        return "SUCCESS";
    }

    @GetMapping("/syncbatchhotels")
    public String batchHotels() {
        hotelInfoApiService.syncBatchData();
        return "SUCCESS";
    }

    @GetMapping("/rate")
    public Object allRate() {
        return internalCodeService.getrate();
    }

    @GetMapping("/syncrate")
    public String syncRate() {
        internalCodeService.syncRate();
        return "SUCCESS";
    }

    @GetMapping("/syncsalu")
    public Object syncSalu() {
        internalCodeService.syncSalu();
        return "SUCCESS";
    }
}
