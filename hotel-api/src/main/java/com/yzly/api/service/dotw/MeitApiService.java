package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.meit.MeitService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Service
@CommonsLog
public class MeitApiService {

    @Autowired
    private MeitService meitService;
    @Autowired
    private BookingService bookingService;

    /**
     * 添加美团调用日志记录
     * @param traceId
     * @param encyptData
     * @param reqData
     * @param meitTraceLog
     * @return
     */
    public MeitTraceLog addTraceLog(String traceId, String encyptData, String reqData, MeitTraceLog meitTraceLog) {
        return meitService.addOrUpdateTrace(traceId, encyptData, reqData, meitTraceLog);
    }

    /**
     * 美团酒店同步方法
     * @param skip
     * @param limit
     * @return
     */
    public Object syncHotelBasic(int skip, int limit) {
        int page = skip / limit + 1;
        List<MeitHotel> mlist = meitService.getHotelBasicList(page, limit, DistributorEnum.MEIT);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotels", mlist);
        return data;
    }

    /**
     * 美团酒店扩展信息同步
     * @param hotelId
     * @return
     */
    public Object syncHotelExtend(String hotelId) {
        List<MeitHotelExt> mlist = meitService.getHotelExtListByIds(hotelId);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelExt", mlist);
        return data;
    }

    /**
     * 美团房型基础信息同步
     * @param hotelId
     * @return
     */
    public Object syncRoomBasic(String hotelId) {
        List<HotelRoomTypeBasic> hlist = meitService.getRoomBasicsByIds(hotelId);
        if (hlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelRoomTypeBasics", hlist);
        return data;
    }

    /**
     * 美团房型扩展信息同步
     * @param hotelId
     * @return
     */
    public Object syncRoomExtend(String hotelId) {
        List<RoomTypeExtModelList> hlist = meitService.getRoomExtendsByIds(hotelId);
        if (hlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("roomTypeExtModelList", hlist);
        return data;
    }

    /**
     * 美团产品搜索结果同步
     * @param jlist
     * @return
     */
    public Object syncGoodsSearch(List<JSONObject> jlist, GoodsSearchQuery goodsSearchQuery) {
        Map<String, HotelMap> data = new HashMap<>();
        List<RoomBookingInfo> rlist = new ArrayList<>();
        for (JSONObject jsonObject : jlist) {
            JSONObject request = jsonObject.getJSONObject("request");
            if (request != null) {
                String successful = request.getString("successful");
                if (StringUtils.isNotEmpty(successful) && "FALSE".equals(successful)) {
                    continue;
                }
            } else {
                log.info("jlist" + jlist);
                String hotelId = request.getJSONObject("hotel").getString("@id");
                rlist = bookingService.addRoomBookingByGetRoomsJson(request, hotelId, goodsSearchQuery.getCheckin(), goodsSearchQuery.getCheckout());
                HotelMap hotelMap = new HotelMap();
                hotelMap.setCurrencyCode(request.getString("currencyShort"));
                List<Room> roomList = new ArrayList<>();
                for (RoomBookingInfo roomBookingInfo : rlist) {
                    Room room = new Room();
                    Breakfast breakfast = new Breakfast();
                    // 判断房型是否还有早餐
                    if (!roomBookingInfo.getRateBasisId().equals("1331")) {
                        breakfast.setCount(0);
                    } else {
                        breakfast.setCount(2);
                    }
                    // TODO: add day info price by rate
                }
            }
        }
        return data;
    }

}
