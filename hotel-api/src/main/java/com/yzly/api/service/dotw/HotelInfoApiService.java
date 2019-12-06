package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.dotw.HotelInfoService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private BookingService bookingService;

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

    public JSONObject searchHotel(String ids, String fromDate, String toDate) {
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
        String fromDate = DateTime.now().toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
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

    public JSONObject getRoomsByHid(String hotelId, String rateId) {
        String fromDate = DateTime.now().toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        return dcmlHandler.getRoomsByHotelId(hotelId, rateId, fromDate, toDate);
    }

    /**
     * 根据城市获取可用的房型和价格
     * @param city
     * @param page
     * @param size
     * @return
     */
    public List<RoomBookingInfo> searchRoomsForCity(String city, int page, int size, String fromDate, String toDate) {
        List<RoomBookingInfo> rlist = new ArrayList<>();
        HotelQuery query = new HotelQuery();
        query.setCountry("CHINA");
        query.setCity(city);
        Page<HotelInfo> hp = hotelInfoService.findAllByPageQuery(page, size, query);
        for (HotelInfo h : hp.getContent()) {
            JSONObject result = dcmlHandler.getRoomsByHotelId(h.getDotwHotelCode(), "-1", fromDate, toDate);
            JSONObject request = result.getJSONObject("request");
            if (request != null) {
                String successful = request.getString("successful");
                if (StringUtils.isNotEmpty(successful) && "FALSE".equals(successful)) {
                    continue;
                }
            } else {
                log.info("hotelId:" + h.getDotwHotelCode());
                rlist = bookingService.addRoomBookingByGetRoomsJson(result, h.getDotwHotelCode(), fromDate, toDate);
            }
        }
        return rlist;
    }

    /**
     * 查询酒店详细信息，并更新酒店更新状态和入库附加信息及房型信息
     * @param country
     * @param city
     * @param page
     * @param size
     */
    public void updateHotelRoomsAndInfo(String country, String city, int page, int size) {
        JSONObject jsonObject = searchHotelInfo(country, city, page, size);
        //根据hotel的类属性进行处理，判断如果不等于空并且count条目等于1则转换为jsonobjectcount>1则转换为jsonarray
        if (jsonObject != null && !jsonObject.getJSONObject("hotels").getString("@count").equals("0")) {
            JSONObject hotelJSON = null;
            if (jsonObject.getJSONObject("hotels").getString("@count").equals("1")) {
                hotelJSON = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                hotelInfoService.addRoomsAndHotelAdditionalInfoByHotelJson(hotelJSON);
            } else {
                JSONArray hotelArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                for (int arrIndex = 0; arrIndex < hotelArray.size(); arrIndex++) {
                    hotelJSON = hotelArray.getJSONObject(arrIndex);
                    hotelInfoService.addRoomsAndHotelAdditionalInfoByHotelJson(hotelJSON);
                }
            }
        }
    }

}
