package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.HotelInfoApiService;
import com.yzly.core.service.meit.TaskService;
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
    @Autowired
    private TaskService taskService;

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

    @GetMapping("/update_date")
    public Object updatePullDate() {
        hotelInfoApiService.plusDaysWithPullDate();
        return "SUCCESS";
    }

    /**
     * 根据hotel_manual_sync_list的数据去dotw同步酒店基础信息
     * @return
     */
    @GetMapping("/sync_list")
    public Object syncHotelAndRoomByList() {
        Runnable runnable = () -> {
            try {
                String ids = taskService.getManualSyncHotelIds();
                hotelInfoApiService.pullHotelAndRoomsInfoByIds(ids);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    /**
     * 根据时间同步30天的价格，并保存在dotw_room_price_by_date表中
     * @return
     */
    @GetMapping("/sync_price")
    public Object syncRoomPriceByDate() {
        Runnable runnable = () -> {
            try {
                // 在重新拉取数据之前，清空30天价格表数据
                taskService.delAllRoomPrice();
                // 重新拉取30天价格数据
                String ids = taskService.getManualSyncHotelIds();
                hotelInfoApiService.syncRoomPriceByIdsAndDays(ids, 30);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

}
