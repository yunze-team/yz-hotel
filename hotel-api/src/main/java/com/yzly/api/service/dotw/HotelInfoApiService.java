package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.service.dotw.HotelInfoService;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by toby on 2019/11/22.
 */
@Service
@CommonsLog
public class HotelInfoApiService {

    @Autowired
    private HotelInfoService hotelInfoService;
    @Autowired
    private DCMLHandler dcmlHandler;

    public void syncBasicData() {
        try {
            hotelInfoService.syncByExcel();
            log.info("hotel sync success");
        } catch(Exception e) {
            log.info(e.getMessage());
        }
    }

    public void syncBatchData() {
        try {
            hotelInfoService.syncByExcelForBatch();
            log.info("hotel sync batch success");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public JSONObject searchHotel(String ids) {
        String fromDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(30).toString("yyyy-MM-dd");
        List<String> idArray = Arrays.asList(ids.split(","));
        return dcmlHandler.searchHotelPriceByID(idArray, fromDate, toDate);
    }

    public JSONObject searchHotelByCountryAndCity(String country, String city, int page, int size) {
        HotelQuery query = new HotelQuery();
        query.setCountry(country);
        query.setCity(city);
        Page<HotelInfo> hp = hotelInfoService.findAllByPageQuery(page, size, query);
        List<HotelInfo> hlist = hp.getContent();
        List<String> ids = new ArrayList<>();
        for (HotelInfo h : hlist) {
            ids.add(h.getDotwHotelCode());
        }
        String fromDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(30).toString("yyyy-MM-dd");
        return dcmlHandler.searchHotelPriceByID(ids, fromDate, toDate);
    }

    public JSONObject searchHotelInfo(String country, String city, int page, int size) {
        HotelQuery query = new HotelQuery();
        query.setCountry(country);
        query.setCity(city);
        Page<HotelInfo> hp = hotelInfoService.findAllByPageQuery(page, size, query);
        List<HotelInfo> hlist = hp.getContent();
        List<String> ids = new ArrayList<>();
        for (HotelInfo h : hlist) {
            ids.add(h.getDotwHotelCode());
        }
        String fromDate = DateTime.now().toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        return dcmlHandler.searchHotelInfoById(ids, fromDate, toDate);
    }

    public JSONObject getRoomsByHid(String hotelId) {
        String fromDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(30).toString("yyyy-MM-dd");
        return dcmlHandler.getRoomsByHotelId(hotelId, fromDate, toDate);
    }

}
