package com.yzly.core.service.meit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.HotelManualSyncList;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import com.yzly.core.repository.HotelManualSyncListRepository;
import com.yzly.core.repository.dotw.RateBasisRepository;
import com.yzly.core.repository.dotw.RoomPriceByDateRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Service
@CommonsLog
public class TaskService {

    @Autowired
    private HotelManualSyncListRepository hotelManualSyncListRepository;
    @Autowired
    private RoomPriceByDateRepository roomPriceByDateRepository;
    @Autowired
    private RateBasisRepository rateBasisRepository;

    /**
     * 获得所有的需要手工同步的酒店ids
     * @return
     */
    public String getManualSyncHotelIds() {
        String ids = "";
        List<HotelManualSyncList> hlist = hotelManualSyncListRepository.findAll();
        for (int i = 0; i < hlist.size(); i++) {
            if (i != hlist.size() - 1) {
                ids += hlist.get(i).getHotelId() + ",";
            } else {
                ids += hlist.get(i).getHotelId();
            }
        }
        return ids;
    }

    /**
     * 根据日期和dotw返回的报文，入库或更新roomprice
     * @param priceObject
     * @param fromDate
     * @param toDate
     */
    public void syncRoomPriceByDate(JSONObject priceObject, String fromDate, String toDate) {
        JSONArray hotelArray = priceObject.getJSONArray("hotels");
        for (int i = 0; i < hotelArray.size(); i++) {
            JSONObject hotelObject = hotelArray.getJSONObject(i);
            String hotelId = hotelObject.getString("@hotelid");
            Object roomTypeObject = hotelObject.getJSONObject("rooms").getJSONObject("room").getObject("roomType", Object.class);
            if (roomTypeObject instanceof JSONArray) {
                JSONArray roomTypeArray = (JSONArray) roomTypeObject;
                for (int j = 0; j < roomTypeArray.size(); j++) {
                    JSONObject rateBasesObject = roomTypeArray.getJSONObject(j);
                    executeRoomTypeJson(rateBasesObject, hotelId, fromDate, toDate);
                }
            } else if (roomTypeObject instanceof JSONObject) {
                JSONObject roomTypeJson = (JSONObject) roomTypeObject;
                executeRoomTypeJson(roomTypeJson, hotelId, fromDate, toDate);
            }
        }
    }

    /**
     * 处理返回的roomtype报文
     * @param roomTypeJson
     * @param hotelId
     * @param fromDate
     * @param toDate
     */
    private void executeRoomTypeJson(JSONObject roomTypeJson, String hotelId, String fromDate, String toDate) {
        JSONObject rateBasesObject = roomTypeJson;
        String roomName = rateBasesObject.getString("name");
        String roomTypeCOde = rateBasesObject.getString("@roomtypecode");
        Object rateBases = rateBasesObject.getObject("rateBases", Object.class);
        if (rateBases instanceof JSONObject) {
            JSONObject rateJSON = ((JSONObject) rateBases).getJSONObject("rateBasis");
            buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
        } else if (rateBases instanceof JSONArray) {
            JSONArray rateArray = (JSONArray) rateBases;
            for (int k = 0; k < rateArray.size(); k++) {
                JSONObject rateJSON = rateArray.getJSONObject(k);
                buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
            }
        }
    }

    /**
     * 根据解析的报文，入库或更新roomprice
     * @param rateJSON
     * @param hotelId
     * @param roomName
     * @param roomTypeCode
     * @param fromDate
     * @param toDate
     * @return
     */
    private RoomPriceByDate buildRoomPriceByJSON(JSONObject rateJSON, String hotelId, String roomName,
                                                 String roomTypeCode, String fromDate, String toDate) {
        String rateBasis = rateBasisRepository.findByCode(rateJSON.getString("@id")).getName();
        RoomPriceByDate roomPrice = roomPriceByDateRepository.findByRoomTypeCodeAndRateBasisAndFromDateAndToDate(roomTypeCode, rateBasis, fromDate, toDate);
        if (roomPrice == null) {
            roomPrice = new RoomPriceByDate();
        }
        roomPrice.setHotelCode(hotelId);
        roomPrice.setFromDate(fromDate);
        roomPrice.setToDate(toDate);
        roomPrice.setRoomName(roomName);
        roomPrice.setRoomTypeCode(roomTypeCode);
        roomPrice.setTotal(rateJSON.getString("total"));
        roomPrice.setRateBasis(rateBasis);
        return roomPriceByDateRepository.save(roomPrice);
    }

}
