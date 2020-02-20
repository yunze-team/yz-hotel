package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.HotelInfoApiService;
import com.yzly.api.service.dotw.TaskApiService;
import com.yzly.core.domain.dotw.vo.RoomPriceExcelData;
import com.yzly.core.service.dotw.RoomPriceService;
import com.yzly.core.service.meit.TaskService;
import com.yzly.core.util.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${dotw.room.price.excel}")
    private String excelFileName;

    @Autowired
    private HotelInfoApiService hotelInfoApiService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RoomPriceService roomPriceService;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private TaskApiService taskApiService;

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

    /**
     * 抽取room_price表中的数据，并生成30天房价excel
     * @return
     */
    @GetMapping("/room_price_excel")
    public Object generateRoomPriceExcel() {
        Runnable runnable = () -> {
            List<String> days = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                String fromDate = DateTime.now().plusDays(i).toString("yyyy-MM-dd");
                days.add(fromDate);
            }
            List<RoomPriceExcelData> rlist = roomPriceService.getAllPriceExcelData(days);
            commonUtil.generateRoomPriceExcel(rlist, days, excelFileName);
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    /**
     * 根据hotel_sync_list中的数据，按照event_attr中的参数配置，拉取30天的价格，并缓存在mongodb中
     * @return
     */
    @GetMapping("/sync_price_xml")
    public Object syncRoomPriceXmlByDate() {
        Runnable runnable = () -> {
            try {
                // 在重新拉取数据之前，清空30天价格表数据
                taskService.delAllRoomPriceXml();
                // 重新拉取30天价格数据
                taskApiService.syncDotwRoomPrice();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

}
