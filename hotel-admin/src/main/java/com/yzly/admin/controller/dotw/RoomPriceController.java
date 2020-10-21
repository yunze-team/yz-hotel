package com.yzly.admin.controller.dotw;

import com.yzly.admin.domain.ReturnT;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import com.yzly.core.domain.dotw.RoomPriceDateXml;
import com.yzly.core.domain.dotw.query.RoomPriceQuery;
import com.yzly.core.domain.dotw.vo.RoomPriceExcelData;
import com.yzly.core.service.dotw.RoomPriceService;
import com.yzly.core.util.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyb
 * @create 2020/1/17
 * @desc
 **/
@RestController
@CommonsLog
public class RoomPriceController {

    @Value("${dotw.room.price.excel}")
    private String excelFileName;

    @Autowired
    private RoomPriceService roomPriceService;
    @Autowired
    private CommonUtil commonUtil;

    @GetMapping("/price/list")
    public Map<String, Object> getAllPrice(int page, int rows, String hotelCode, String roomTypeCode,
                                           String fromDate, String toDate) {
        Map<String, Object> resMap = new HashMap<>();
        RoomPriceQuery roomPriceQuery = new RoomPriceQuery();
        roomPriceQuery.setHotelCode(hotelCode);
        roomPriceQuery.setRoomTypeCode(roomTypeCode);
        roomPriceQuery.setFromDate(fromDate);
        roomPriceQuery.setToDate(toDate);
        Page<RoomPriceByDate> rpage = roomPriceService.findAllByPageQuery(page, rows, roomPriceQuery);
        resMap.put("total", rpage.getTotalElements());
        resMap.put("rows", rpage.getContent());
        log.info(rpage);
        return resMap;
    }

    @GetMapping("/price/date")
    public Map<String, Object> getAllDatePrice(int page, int rows, String hotelCode, String roomTypeCode,
                                               String fromDate, String toDate) {
        Map<String, Object> resMap = new HashMap<>();
        RoomPriceQuery roomPriceQuery = new RoomPriceQuery();
        roomPriceQuery.setHotelCode(hotelCode);
        roomPriceQuery.setRoomTypeCode(roomTypeCode);
        roomPriceQuery.setFromDate(fromDate);
        roomPriceQuery.setToDate(toDate);
        Page<RoomPriceDateXml> rpage = roomPriceService.findAllPriceByPageQuery(page, rows, roomPriceQuery);
        resMap.put("total", rpage.getTotalElements());
        resMap.put("rows", rpage.getContent());
        log.info(rpage);
        return resMap;
    }

    @GetMapping("/price/excel")
    public ReturnT<String> exportExcel() {
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
        return ReturnT.SUCCESS;
    }

}
