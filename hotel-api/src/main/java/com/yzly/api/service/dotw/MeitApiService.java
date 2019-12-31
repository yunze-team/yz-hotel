package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.dotw.HotelInfoService;
import com.yzly.core.service.meit.MeitService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                    Room room = new Room();
                    Breakfast breakfast = new Breakfast();
                    // 判断房型是否还有早餐
                    if (!roomBookingInfo.getRateBasisId().equals("1331")) {
                        breakfast.setCount(0);
                    } else {
                        breakfast.setCount(2);
                    }
                    JSONObject dates = JSONObject.parseObject(roomBookingInfo.getDates());
                    List<DayInfo> dayInfos = new ArrayList<>();
                    // 获得酒店价格上浮利率
                    Double priceRate = hotelInfoService.findHotelPriceRateByEvent();
                    if (dates.getString("@count").equals("1")) {
                        JSONObject date = dates.getJSONObject("date");
                        DayInfo dayInfo = generateDayInfoByJson(date, priceRate);
                        dayInfos.add(dayInfo);
                    } else {
                        JSONArray dateArray = dates.getJSONArray("date");
                        for (int i = 0; i < dateArray.size(); i++) {
                            JSONObject date = dateArray.getJSONObject(i);
                            DayInfo dayInfo = generateDayInfoByJson(date, priceRate);
                            dayInfos.add(dayInfo);
                        }
                    }
                    room.setDayInfos(dayInfos);
                    room.setBreakfast(breakfast);
                    room.setRoomId(roomBookingInfo.getRoomTypeCode());
                    room.setRoomName(roomBookingInfo.getName());
                    room.setRatePlanCode(roomBookingInfo.getAllocationDetails());
                    room.setCheckInTime(hotelAdditionalInfo.getHotelCheckIn());
                    room.setCheckOutTime(hotelAdditionalInfo.getHotelCheckOut());
                    List<RefundRule> refundRules = new ArrayList<>();
                    // 退订规则组装
                    JSONObject cancellationRules = JSONObject.parseObject(roomBookingInfo.getCancellationRules());
                    if (cancellationRules.getString("@count").equals("1")) {
                        RefundRule refundRule = new RefundRule();
                        refundRule.setReturnable(false);
                        refundRules.add(refundRule);
                    } else {
                        JSONArray cancelArray = cancellationRules.getJSONArray("rule");
                        for (int i = 0; i < cancelArray.size() - 1; i++) {
                            JSONObject rule = cancelArray.getJSONObject(i);
                            RefundRule refundRule = new RefundRule();
                            refundRule.setReturnable(true);
                            refundRule.setRefundType(1);
                            DateTime hotelCheckIn = DateTime.parse(roomBookingInfo.getFromDate() + " " + room.getCheckInTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"));
                            if (StringUtils.isNotEmpty(rule.getString("fromDate"))) {
                                DateTime fromDate = DateTime.parse(rule.getString("fromDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                                long maxCheckInTime = (hotelCheckIn.getMillis() - fromDate.getMillis()) % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
                                Integer maxCheckIn = maxCheckInTime > 0 ? Integer.valueOf(String.valueOf(maxCheckInTime)) : 0;
                                refundRule.setMaxHoursBeforeCheckIn(maxCheckIn);
                            }
                            DateTime toDate = DateTime.parse(rule.getString("toDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                            long minCheckInTime = (hotelCheckIn.getMillis() - toDate.getMillis()) % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
                            Integer minCheckIn = minCheckInTime > 0 ? Integer.valueOf(String.valueOf(minCheckInTime)) : 0;
                            refundRule.setMinHoursBeforeCheckIn(minCheckIn);
                            refundRule.setFine(new BigDecimal(rule.getJSONObject("cancelCharge").getString("#text"))
                                    .multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
                            refundRules.add(refundRule);
                        }
                    }
                    room.setRefundRules(refundRules);
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

    private DayInfo generateDayInfoByJson(JSONObject date, Double priceRate) {
        DayInfo dayInfo = new DayInfo();
        BigDecimal basePrice = new BigDecimal(date.getJSONObject("price").getString("#text"));
        BigDecimal finalPrice = basePrice.multiply(new BigDecimal(100)).multiply(new BigDecimal(1 + priceRate));
        dayInfo.setRoomPrice(finalPrice.setScale(0, RoundingMode.HALF_UP).intValue());
        dayInfo.setDate(date.getString("@datetime"));
        dayInfo.setStatus(1);
        dayInfo.setCounts(1);
        return dayInfo;
    }

}
