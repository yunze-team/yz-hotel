package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.dotw.HotelInfoService;
import com.yzly.core.service.meit.TaskService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
    @Autowired
    private TaskService taskService;

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

    /**
     * 根据酒店ids和时间期限，同步房间价格并入库
     * @param ids 酒店ids
     * @param days 时间期限
     */
    public void syncRoomPriceByIdsAndDays(String ids, Integer days) {
        List<String> hotelIds = Arrays.asList(ids.split(","));
        for (int i = 0; i < days; i++) {
            String fromDate = DateTime.now().plusDays(i).toString("yyyy-MM-dd");
            String toDate = DateTime.now().plusDays(i).plusDays(1).toString("yyyy-MM-dd");
            JSONObject priceObject = dcmlHandler.searchHotelPriceByID(hotelIds, fromDate, toDate);
            taskService.syncRoomPriceByDate(priceObject, fromDate, toDate);
        }
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

    /**
     * 用来定时任务拉取同步酒店信息的方法
     * 更新完成后，会更新hotel_info表的is_updated为1
     * 会插入room_type数据
     * 同时会插入mongodb的相应酒店明细数据，包含图片信息
     * @throws Exception
     */
    public void pullHotelAndRoomsInfo() throws Exception {
        List<HotelInfo> hlist = hotelInfoService.findHotelListByEventAttr();
        if (hlist == null || hlist.size() == 0) {
            throw new Exception("hotel info list is null, please confirm all hotel is updated.");
        }
        List<String> ids = new ArrayList<>();
        for (HotelInfo h : hlist) {
            ids.add(h.getDotwHotelCode());
        }
        String fromDate = DateTime.now().toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        JSONObject jsonObject = dcmlHandler.searchHotelInfoById(ids, fromDate, toDate);
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
        } else {
            hotelInfoService.updateBatchHotel(hlist);
        }
    }

    /**
     * 用户美团任务拉取dotw酒店和房型数据
     * @param hotelIds
     */
    public void pullHotelAndRoomsInfoByIds(String hotelIds) {
        List<String> ids = Arrays.asList(hotelIds.split(","));
        // 判断本地酒店附属信息和房型信息缓存是否存在，如果存在，则不去dotw查询
//        if (hotelInfoService.judgeHotelAdditionalAndRoomType(ids)) {
//            return;
//        }
        String fromDate = DateTime.now().toString("yyyy-MM-dd");
        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
        JSONObject jsonObject = dcmlHandler.searchHotelInfoById(ids, fromDate, toDate);
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

    /**
     * 用来定时任务拉取酒店当天房型价格数据
     * @throws Exception
     */
    public void pullHotelRoomPrice() throws Exception {
        List<HotelInfo> hlist = hotelInfoService.findUpdatedHotelList();
        if (hlist == null || hlist.size() == 0) {
            hotelInfoService.updatePullDateAttr();
            throw new Exception("hotel info list is null, please confirm all hotel is updated.");
        }
        DateTime date = DateTime.parse(hotelInfoService.getDotwRoomDate(), DateTimeFormat.forPattern("yyyy-MM-dd"));
        String fromDate = date.toString("yyyy-MM-dd");
        String toDate = date.plusDays(1).toString("yyyy-MM-dd");
        for (HotelInfo h : hlist) {
            hotelInfoService.updateHotelSyncDate(h, fromDate);
            JSONObject result = dcmlHandler.getRoomsByHotelId(h.getDotwHotelCode(), "-1", fromDate, toDate);
            JSONObject request = result.getJSONObject("request");
            if (request != null) {
                String successful = request.getString("successful");
                if (StringUtils.isNotEmpty(successful) && "FALSE".equals(successful)) {
                    continue;
                }
            } else {
                log.info("hotelId:" + h.getDotwHotelCode());
                List<RoomBookingInfo> rlist = bookingService.addRoomBookingByGetRoomsJson(result, h.getDotwHotelCode(), fromDate, toDate);
                log.info("pull room price list:" + JSONObject.toJSONString(rlist));
            }
        }
    }

    public void plusDaysWithPullDate() {
        hotelInfoService.updatePullDateAttr();
    }

}
