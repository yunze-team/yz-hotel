package com.yzly.api.controller.jl;

import com.yzly.api.service.jl.JLStaticApiService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**捷旅api-静态信息接口
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@RequestMapping("/api/jl/static")
@RestController
@CommonsLog
public class JLStaticController {

    @Autowired
    private JLStaticApiService jlStaticApiService;

    /**
     * 同步城市
     * @return
     */
    @GetMapping("/sync_city")
    public Object syncCity() {
        Runnable runnable = () -> {
            jlStaticApiService.syncJlCity();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    /**
     * 同步酒店列表
     * @return
     */
    @GetMapping("/sync_hotel")
    public Object syncHotel() {
        Runnable runnable = () -> {
            jlStaticApiService.syncJLHotel();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    /**
     * 同步酒店明细
     * @param hotelId
     * @return
     */
    @GetMapping("/sync_hotel_detail")
    public Object syncHotelDetail(@RequestParam Integer hotelId) {
        return jlStaticApiService.syncJLHotelDetail(hotelId);
    }

    /**
     * 同步酒店每晚价格
     * @param hotelId
     * @param checkInDate
     * @param checkOutDate
     * @return
     */
    @GetMapping("/sync_hotel_price")
    public Object syncHotelPrice(@RequestParam Integer hotelId,
                                 @RequestParam String checkInDate, @RequestParam String checkOutDate) {
        return jlStaticApiService.syncJLHotelPrice(hotelId, checkInDate, checkOutDate);
    }

}
