package com.yzly.core.service.ctrip;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.ctrip.CtripHotelCode;
import com.yzly.core.domain.ctrip.CtripRoomTypeCode;
import com.yzly.core.repository.ctrip.CtripHotelCodeRepository;
import com.yzly.core.repository.ctrip.CtripRoomTypeCodeRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 携程通用服务类
 * @author lazyb
 * @create 2020/11/17
 * @desc
 **/
@Service
@CommonsLog
public class CtripService {

    @Autowired
    private CtripHotelCodeRepository ctripHotelCodeRepository;
    @Autowired
    private CtripRoomTypeCodeRepository ctripRoomTypeCodeRepository;

    /**
     * 通过携程映射code，获得捷旅房型code
     * @param ctripRoomCode
     * @return
     */
    public String searchJLRoomCodeByCtrip(String ctripRoomCode) {
        CtripRoomTypeCode ctripRoomTypeCode = ctripRoomTypeCodeRepository.findOneByCtripRoomTypeCodeAndSupplier(ctripRoomCode, "JL");
        return ctripRoomTypeCode.getRoomTypeCode();
    }

    /**
     * 根据携程返回的json，同步携程code映射
     * @param jsonObject
     * @throws Exception
     */
    public void syncCtripCodeByJSON(JSONObject jsonObject) throws Exception {
        JSONObject hotel = jsonObject.getJSONObject("Hotel");
        // 判断携程酒店同步状态，激活状态才同步code
        if (!hotel.getString("@Status").equals("Active")) {
            throw new Exception("ctrip hotel status is not active");
        }
        String ctripHotelCode = hotel.getString("@CtripHotelCode");
        String hotelCode = hotel.getString("@HotelCode");
        // 判断是否存在，存在即更新
        CtripHotelCode hotelCodeDo = ctripHotelCodeRepository.findOneByHotelCodeAndSupplier(hotelCode, "JL");
        if (hotelCodeDo == null) {
            hotelCodeDo = new CtripHotelCode();
        }
        if (hotelCodeDo.getCtripHotelCode() == null || !hotelCodeDo.getCtripHotelCode().equals(ctripHotelCode)) {
            hotelCodeDo.setCtripHotelCode(ctripHotelCode);
            hotelCodeDo.setHotelCode(hotelCode);
            hotelCodeDo.setSupplier("JL");
            ctripHotelCodeRepository.save(hotelCodeDo);
        }
        JSONArray baseRooms = hotel.getJSONObject("BasicRooms").getJSONArray("BasicRoom");
        // 同步房型code
        for (int i = 0; i < baseRooms.size(); i++) {
            JSONObject baseRoom = baseRooms.getJSONObject(i);
            // 判断状态是否正确
            if (!baseRoom.getString("@Status").equals("Active")) {
                continue;
            }
            String roomCode = baseRoom.getString("@RoomCode");
            String ctripRoomCode = baseRoom.getString("@CtripRoomCode");
            // 判断是否存在，存在即更新
            CtripRoomTypeCode ctripRoomTypeCode = ctripRoomTypeCodeRepository.findOneByCtripRoomTypeCodeAndSupplier(ctripRoomCode, "JL");
            if (ctripRoomTypeCode == null) {
                ctripRoomTypeCode = new CtripRoomTypeCode();
            }
            if (ctripRoomTypeCode.getRoomTypeCode() == null || !ctripRoomTypeCode.getRoomTypeCode().equals(roomCode)) {
                ctripRoomTypeCode.setHotelCode(hotelCode);
                ctripRoomTypeCode.setRoomTypeCode(roomCode);
                ctripRoomTypeCode.setCtripRoomTypeCode(ctripRoomCode);
                ctripRoomTypeCode.setSupplier("JL");
                ctripRoomTypeCodeRepository.save(ctripRoomTypeCode);
            }
        }
    }

}
