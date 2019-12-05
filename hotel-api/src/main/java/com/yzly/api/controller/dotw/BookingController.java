package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.BookingApiService;
import com.yzly.core.domain.dotw.vo.Passenger;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lazyb
 * @create 2019/12/4
 * @desc
 **/
@RestController
@RequestMapping("/booking")
@CommonsLog
public class BookingController {

    @Autowired
    private BookingApiService bookingApiService;

    @PostMapping("/confirm/{allocation}")
    public Object confirm(@PathVariable String allocation, @RequestBody List<Passenger> plist) {
        try {
            return bookingApiService.confirmBooking(allocation, plist);
        } catch (Exception e) {
            log.info(e.getMessage());
            return "FAIL";
        }
    }

    @PostMapping("/precancel")
    public Object preCancel(String allocation) {
        try {
            return bookingApiService.preCancelBooking(allocation);
        } catch (Exception e) {
            log.info(e.getMessage());
            return "FAIL";
        }
    }

    @PostMapping("/cancel")
    public Object cancel(String allocation) {
        try {
            return bookingApiService.cancelBooking(allocation);
        } catch (Exception e) {
            log.info(e.getMessage());
            return "FAIL";
        }
    }

}
