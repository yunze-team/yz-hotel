package com.yzly.api.service.jl;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.JLHandler;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.jl.JLHotelDetail;
import com.yzly.core.service.EventAttrService;
import com.yzly.core.service.jl.JLStaticService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Service
@CommonsLog
public class JLStaticApiService {

    @Autowired
    private JLStaticService jlStaticService;
    @Autowired
    private EventAttrService eventAttrService;
    @Autowired
    private JLHandler jlHandler;

    private static final String JL_CITY_PAGE = "JL_CITY_PAGE";
    private static final String JL_HOTEL_PAGE = "JL_HOTEL_PAGE";
    private static final String JL_HOTEL_COUNTRY = "JL_HOTEL_COUNTRY";

    /**
     * 循环获取捷旅城市并入库
     */
    public void syncJlCity() {
        while (1 == 1) {
            EventAttr attr = eventAttrService.findByType(JL_CITY_PAGE);
            try {
                JSONObject reJson = JSONObject.parseObject(jlHandler.cityQuery(Integer.valueOf(attr.getEventValue()))).getJSONObject("result");
                jlStaticService.syncCityByJson(reJson);
            } catch (Exception e) {
                log.error(e.getMessage());
                break;
            }
        }
    }

    /**
     * 根据返回结果存入城市
     */
    public void syncJLHotel() {
        Integer pageIndex = Integer.valueOf(eventAttrService.findByType(JL_HOTEL_PAGE).getEventValue());
        Integer countryId = Integer.valueOf(eventAttrService.findByType(JL_HOTEL_COUNTRY).getEventValue());
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.queryHotelList(countryId, pageIndex)).getJSONObject("result");
            jlStaticService.syncHotelByJson(reJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获得酒店明细数据并存入
     * @param hotelId
     */
    public JLHotelDetail syncJLHotelDetail(Integer hotelId) {
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.queryHotelDetail(hotelId)).getJSONObject("result");
            return jlStaticService.syncHotelDetailByJson(reJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获得酒店每晚价格数据并存入
     * @param hotelId
     * @param checkInDate
     * @param checkOutDate
     * @return
     */
    public Object syncJLHotelPrice(Integer hotelId, String checkInDate, String checkOutDate) {
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.
                    queryHotelPriceByDefault(hotelId, checkInDate, checkOutDate, false)).getJSONObject("result");
            return jlStaticService.syncHotelPriceByJson(reJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
