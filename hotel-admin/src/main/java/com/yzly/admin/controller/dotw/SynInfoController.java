package com.yzly.admin.controller.dotw;

import com.yzly.core.domain.dotw.HotelSyncDetailInfoList;
import com.yzly.core.domain.dotw.query.HotelSycnListQuery;
import com.yzly.core.service.admin.SynInfoService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CommonsLog
public class SynInfoController {

    @Autowired
    private SynInfoService synInfoService;

    @GetMapping("/syn_info/syn_hotel_info")
    public Map<String, Object> getSynHotel(int page, int rows, HotelSycnListQuery hotelSycnListQuery) {
        Map<String, Object> resMap = new HashMap<>();
        Page<HotelSyncDetailInfoList> hotelSyncLists = synInfoService.getSyncListInfo(page,rows,hotelSycnListQuery);
        resMap.put("total", hotelSyncLists.getTotalElements());
        resMap.put("rows", hotelSyncLists.getContent());
        return resMap;
    }
}
