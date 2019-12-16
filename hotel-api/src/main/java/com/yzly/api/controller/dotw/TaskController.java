package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.HotelInfoApiService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务调用controller
 * @author lazyb
 * @create 2019/12/16
 * @desc
 **/
@RestController
@RequestMapping(value = "/task")
@CommonsLog
public class TaskController {

    @Autowired
    private HotelInfoApiService hotelInfoApiService;

    /**
     * 拉取酒店信息和房型信息的定时方法
     * @return
     */
    @GetMapping("/pull_hotel")
    public Object pullHotelAndRoom() {
        Runnable runnable = () -> {
            try {
                hotelInfoApiService.pullHotelAndRoomsInfo();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    /**
     * 拉取房型指定日期价格的定时方法
     * @return
     */
    @GetMapping("/pull_price")
    public Object pullRoomPrice() {
        Runnable runnable = () -> {
            try {
                hotelInfoApiService.pullHotelRoomPrice();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

}
