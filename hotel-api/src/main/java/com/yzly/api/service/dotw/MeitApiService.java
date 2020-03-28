package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.api.util.meit.MeitResultUtil;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.RoomPriceDateXml;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import com.yzly.core.enums.meit.ResultEnum;
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
import java.util.*;

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
    @Autowired
    private DCMLHandler dcmlHandler;
    @Autowired
    private HotelInfoApiService hotelInfoApiService;

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
     * 添加美团调用结果日志
     * @param meitResult
     * @return
     */
    public MeitResult addMeitRes(MeitResult meitResult) {
        return meitService.addOrUpdateRes(meitResult);
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
        hotelInfoApiService.pullHotelAndRoomsInfoByIds(hotelId);
        List<MeitHotelExt> mlist = meitService.getHotelExtListByIds(hotelId);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelExts", mlist);
        return data;
    }

    /**
     * 美团房型基础信息同步
     * @param hotelId
     * @return
     */
    public Object syncRoomBasic(String hotelId) {
        hotelInfoApiService.pullHotelAndRoomsInfoByIds(hotelId);
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
        hotelInfoApiService.pullHotelAndRoomsInfoByIds(hotelId);
        List<RoomTypeExtModelList> hlist = meitService.getRoomExtendsByIds(hotelId);
        if (hlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("roomTypeExtModelList", hlist);
        return data;
    }

    /**
     * 美团产品搜索路由
     * @param goodsSearchQuery
     * @return
     */
    public Object syncGoodsSearchRoute(GoodsSearchQuery goodsSearchQuery) {
        String[] hotelIdsArray = goodsSearchQuery.getHotelIds().split(",");
        Object data = null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime fromDate = DateTime.parse(goodsSearchQuery.getCheckin(), formatter);
        DateTime toDate = DateTime.parse(goodsSearchQuery.getCheckout(), formatter);
        boolean flag = false;
        if (!toDate.minusDays(1).isEqual(fromDate)) {
            flag = true;
        }
        if (hotelIdsArray.length == 1 || flag) {
            List<JSONObject> jlist = dcmlHandler.getRoomsByMeitQuery(goodsSearchQuery);
            data = syncGoodsSearch(jlist, goodsSearchQuery);
        } else {
            data = syncGoodsSearchCash(goodsSearchQuery);
        }
        return data;
    }

    /**
     * 美团产品搜索结果按缓存同步
     * @param goodsSearchQuery
     * @return
     */
    public Object syncGoodsSearchCash(GoodsSearchQuery goodsSearchQuery) {
        log.info("sync goods search cash start.");
        Map<String, List<RoomPriceDateXml>> roomMap = meitService.findAllByGoodsSearch(goodsSearchQuery.getHotelIds(),
                goodsSearchQuery.getCheckin(), goodsSearchQuery.getCheckout());
        Map<String, Map> res = new HashMap<>();
        Map<String, HotelMap> data = new HashMap<>();
        for (String hotelId : roomMap.keySet()) {
            List<RoomPriceDateXml> rlist = roomMap.get(hotelId);
            List<Room> roomList = new ArrayList<>();
            HotelMap hotelMap = new HotelMap();
            hotelMap.setCurrencyCode("CNY");
            for (RoomPriceDateXml priceDateXml : rlist) {
                Room room = meitService.assemblyMeitRoomCash(priceDateXml);
                Rooms rooms = new Rooms();
                rooms.setRoom(room);
                roomList.add(room);
            }
            hotelMap.setRooms(roomList);
            // 封装返回结果集
            data.put(hotelId, hotelMap);
        }
        res.put("hotelMap", data);
        log.info("sync goods search cash end.");
        return res;
    }

    /**
     * 美团产品搜索结果同步
     * @param jlist
     * @return
     */
    public Object syncGoodsSearch(List<JSONObject> jlist, GoodsSearchQuery goodsSearchQuery) {
        log.info("sync goods search start.");
        Map<String, Map> res = new HashMap<>();
        Map<String, HotelMap> data = new HashMap<>();
        for (JSONObject jsonObject : jlist) {
            JSONObject request = jsonObject.getJSONObject("request");
            if (request != null) {
                String successful = request.getString("successful");
                if (StringUtils.isNotEmpty(successful) && "FALSE".equals(successful)) {
                    continue;
                }
            } else {
                log.debug("jsonObject" + jsonObject);
                String hotelId = jsonObject.getJSONObject("hotel").getString("@id");
                log.debug("bookingService addRoomBooking start.");
                List<RoomBookingInfo> rlist = bookingService.addRoomBookingByGetRoomsJson(jsonObject, hotelId, goodsSearchQuery.getCheckin(), goodsSearchQuery.getCheckout());
                log.debug("bookingService addRoomBooking end.");
                HotelMap hotelMap = new HotelMap();
                hotelMap.setCurrencyCode(jsonObject.getString("currencyShort"));
                List<Room> roomList = new ArrayList<>();
                //HotelAdditionalInfo hotelAdditionalInfo = hotelInfoService.findOneById(hotelId);
                for (RoomBookingInfo roomBookingInfo : rlist) {
                    Room room = meitService.assemblyMeitRoom(roomBookingInfo);
                    Rooms rooms = new Rooms();
                    rooms.setRoom(room);
                    // 组装进入房型列表数组
                    roomList.add(room);
                }
                hotelMap.setRooms(roomList);
                // 封装返回结果集
                data.put(hotelId, hotelMap);
            }
        }
        res.put("hotelMap", data);
        log.info("sync goods search end.");
        return res;
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

    /**
     * 查询美团酒店订单
     * @param orderId
     * @return
     */
    public Object orderQueryResult(String orderId) {
        OrderResult orderResult = new OrderResult();
        MeitOrderBookingInfo orderBookingInfo = meitService.getOrderByOrderId(orderId);
        orderResult.setPartnerOrderId(orderBookingInfo.getPartnerOrderId());
        orderResult.setOrderId(orderBookingInfo.getOrderId());
        orderResult.setOrderStatus(orderBookingInfo.getOrderStatus());
        orderResult.setTotalPrice(orderBookingInfo.getActualTotalPrice());
        if (orderBookingInfo.getAgentOrderId() != null) {
            orderResult.setAgentOrderId(orderBookingInfo.getAgentOrderId());
        }
        if (orderBookingInfo.getPenalty() != null) {
            orderResult.setPenalty(orderBookingInfo.getPenalty());
        }
        if (orderBookingInfo.getOrderAvailable().equals("0")) {
            BookingOrderInfo bookingOrderInfo = bookingService.getOneById(String.valueOf(orderBookingInfo.getDotwOrderId()));
            if (bookingOrderInfo != null) {
                if (bookingOrderInfo.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
                    List<String> confirmationNumbers = bookingService.buildConfirmationNumbersByOrders(bookingOrderInfo.getId());
                    orderResult.setConfirmationNumbers(confirmationNumbers);
                }
            }
        } else {
            List<String> confirmationNumbers = Arrays.asList(orderBookingInfo.getConfirmationNumbers().split(","));
            orderResult.setConfirmationNumbers(confirmationNumbers);
        }
        return orderResult;
    }

    /**
     * 去dotw完成美团订单
     * @param orderId
     * @return
     * @throws Exception
     */
    public MeitResult finishOrder(String orderId) {
        BookingOrderInfo bookingOrderInfo;
        try {
            bookingOrderInfo = meitService.saveBookingByMeitOrder(orderId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return MeitResultUtil.generateResult(ResultEnum.FAIL, null);
        }
        // 发往dotw确认
        JSONObject result = dcmlHandler.confirmBookingByOrder(bookingOrderInfo);
        if (!dcmlHandler.judgeResult(result)) {
            bookingService.updateBookingOrderStatus(bookingOrderInfo, OrderStatus.FAILED);
            //meitService.updateOrderFail(orderId);
            return MeitResultUtil.generateResult(ResultEnum.FAIL, null);
        }
        // 获得结果，更新order
        BookingOrderInfo finalOrder = bookingService.updateBookingByJSON(result, bookingOrderInfo);
        //MeitOrderBookingInfo meitOrder = meitService.updateOrderByBookingInfo(orderId, finalOrder);
        return MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
    }

    /**
     * 手动完成美团订单
     * @param orderId
     * @param totalPrice
     * @param confirmNumbers
     * @return
     */
    public MeitResult finishOrderManual(String orderId, String totalPrice, String confirmNumbers) {
        MeitOrderBookingInfo meitOrder = meitService.updateOrderByManual(orderId, confirmNumbers, totalPrice);
        return MeitResultUtil.generateResult(ResultEnum.SUCCESS, meitOrder);
    }

    /**
     * 手工同步dotw成功订单到美团订单
     * @param meitOrderId
     * @param dotwOrderId
     * @return
     */
    public MeitResult syncDotwOrderToMeit(String meitOrderId, String dotwOrderId) {
        BookingOrderInfo dotwOrder = bookingService.getOneById(dotwOrderId);
        MeitOrderBookingInfo meitOrder = meitService.updateOrderByBookingInfo(meitOrderId, dotwOrder);
        return MeitResultUtil.generateResult(ResultEnum.SUCCESS, meitOrder);
    }

    private Object cancelOrderManaul(String orderId) {
        OrderResult orderResult = new OrderResult();
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(orderId);
        //BookingOrderInfo orderInfo = meitService.getBookingByMeitOrder(orderId);
//        BookingOrderInfo orderInfo = bookingService.getOneById(String.valueOf(meitOrder.getDotwOrderId()));
        orderResult.setOrderId(meitOrder.getOrderId());
        orderResult.setPartnerOrderId(meitOrder.getPartnerOrderId());
//        if (orderInfo == null || !orderInfo.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
//            log.error("order is null or order status is not confirmed");
//            orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_FAIL);
//            return orderResult;
//        }
//        orderInfo = bookingService.preCancelOrderManual(orderInfo);
//        bookingService.updateBookingOrderStatus(orderInfo, OrderStatus.CANCELED);
//        Integer penalty = new BigDecimal(orderInfo.getPenaltyApplied()).
//                multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
        orderResult.setPenalty(0);
        orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_SUCCESS);
        // 更新美团订单的取消状态
        meitService.updateCancelOrder(meitOrder, 0);
        return orderResult;
    }

    /**
     * 通过美团的order_available来判断是否发往dotw取消订单
     * @param orderId
     * @return
     */
    public Object cancelOrderJudge(String orderId) {
        Object rest;
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(orderId);
        if (meitOrder.getOrderAvailable().equals("0")) {
           rest = cancelOrder(orderId);
        } else {
            rest = cancelOrderManaul(orderId);
        }
        return rest;
    }

    /**
     * 去dotw取消美团订单
     * @param orderId
     * @return
     */
    private Object cancelOrder(String orderId) {
        OrderResult orderResult = new OrderResult();
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(orderId);
//        BookingOrderInfo orderInfo = meitService.getBookingByMeitOrder(orderId);
        BookingOrderInfo orderInfo = bookingService.getOneById(String.valueOf(meitOrder.getDotwOrderId()));
        orderResult.setOrderId(meitOrder.getOrderId());
        orderResult.setPartnerOrderId(meitOrder.getPartnerOrderId());
        if (orderInfo == null || !orderInfo.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            log.error("order is null or order status is not confirmed");
            orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_FAIL);
            return orderResult;
        }
        // 发往dotw进行预取消订单
        JSONObject preCancel = dcmlHandler.cancelBooking(orderInfo, "no");
        if (!dcmlHandler.judgeResult(preCancel)) {
            log.error("dotw precancel order fail");
            orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_FAIL);
            return orderResult;
        }
        orderInfo = bookingService.preCancelOrder(orderInfo, preCancel);
        // 发往dotw进行取消订单
        JSONObject cancel = dcmlHandler.cancelBooking(orderInfo, "yes");
        if (!dcmlHandler.judgeResult(cancel)) {
            log.error("dotw cancel order fail");
            orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_FAIL);
            return orderResult;
        }
        bookingService.updateBookingOrderStatus(orderInfo, OrderStatus.CANCELED);
        Integer penalty = new BigDecimal(orderInfo.getPenaltyApplied()).
                multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
        orderResult.setPenalty(penalty);
        orderResult.setOrderStatus(PlatformOrderStatusEnum.CANCEL_SUCCESS);
        // 更新美团订单的取消状态
        meitService.updateCancelOrder(meitOrder, penalty);
        return orderResult;
    }

}
