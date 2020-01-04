package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.dotw.HotelInfoService;
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
    @Autowired
    private HotelInfoService hotelInfoService;

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
                List<RoomBookingInfo> rlist = bookingService.addRoomBookingByGetRoomsJson(request, hotelId, goodsSearchQuery.getCheckin(), goodsSearchQuery.getCheckout());
                HotelMap hotelMap = new HotelMap();
                hotelMap.setCurrencyCode(request.getString("currencyShort"));
                List<Room> roomList = new ArrayList<>();
                HotelAdditionalInfo hotelAdditionalInfo = hotelInfoService.findOneById(hotelId);
                for (RoomBookingInfo roomBookingInfo : rlist) {
                    Room room = meitService.assemblyMeitRoom(roomBookingInfo);
                    // 组装进入房型列表数组
                    roomList.add(room);
                }
                hotelMap.setRooms(roomList);
                // 封装返回结果集
                data.put(hotelId, hotelMap);
            }
        }
        return data;
    }

    /**
     * 创建美团酒店订单
     * @param orderCreateParam
     * @return
     */
    public Object createOrder(OrderCreateParam orderCreateParam) {
        OrderResult orderResult = new OrderResult();
        MeitOrderBookingInfo orderBookingInfo = meitService.createMeitOrder(orderCreateParam);
        orderResult.setPartnerOrderId(orderBookingInfo.getPartnerOrderId());
        orderResult.setOrderId(orderBookingInfo.getOrderId());
        orderResult.setOrderStatus(orderBookingInfo.getOrderStatus());
        orderResult.setTotalPrice(Integer.valueOf(orderBookingInfo.getTotalPrice()));
        return orderResult;
    }

}
