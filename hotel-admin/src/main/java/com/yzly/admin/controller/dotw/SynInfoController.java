package com.yzly.admin.controller.dotw;

import com.yzly.core.domain.dotw.HotelSyncDetailInfoList;
import com.yzly.core.domain.dotw.query.HotelSycnListQuery;
import com.yzly.core.service.admin.SynInfoService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CommonsLog
@RequestMapping("/syn_info")
public class SynInfoController {

    @Autowired
    private SynInfoService synInfoService;

    @GetMapping("/syn_hotel_info")
    public Map<String, Object> getSynHotel(int page, int rows, HotelSycnListQuery hotelSycnListQuery) {
        Map<String, Object> resMap = new HashMap<>();
        Page<HotelSyncDetailInfoList> hotelSyncLists = synInfoService.getSyncListInfo(page,rows,hotelSycnListQuery);
        resMap.put("total", hotelSyncLists.getTotalElements());
        resMap.put("rows", hotelSyncLists.getContent());
        return resMap;
    }

    @PostMapping("/syn_excel")
    public Object addSyncHotelByExcel(String path) {
        Runnable runnable = () -> {
            try {
                synInfoService.saveSyncListByExcel(path);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }
}
