package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.SubOrder;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.repository.dotw.BookingOrderInfoRepository;
import com.yzly.core.repository.dotw.RoomBookingInfoRepository;
import com.yzly.core.repository.dotw.SubOrderRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**房间预定业务类，处理房型查找，预定和确认以及取消业务
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
@Service
@CommonsLog
public class BookingService {

    @Autowired
    private RoomBookingInfoRepository roomBookingInfoRepository;
    @Autowired
    private BookingOrderInfoRepository bookingOrderInfoRepository;
    @Autowired
    private SubOrderRepository subOrderRepository;

    public BookingOrderInfo getOneByOrderId(String meitOrderId) {
        return bookingOrderInfoRepository.findByOrderId(meitOrderId);
    }

    /**
     * 根据总订单拼接房间确认号
     * @param orderId
     * @return
     */
    public List<String> buildConfirmationNumbersByOrders(Long orderId) {
        List<String> rest = new ArrayList<>();
        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(orderId);
        for (SubOrder subOrder : subOrders) {
            rest.add(subOrder.getBookingReferenceNumber());
        }
        return rest;
    }

    public List<RoomBookingInfo> addRoomBookingByGetRoomsJson(JSONObject jsonObject, String hid, String fromDate, String toDate) {
        JSONObject hotelJson = jsonObject.getJSONObject("hotel");
        if (StringUtils.isEmpty(hid)) {
            hid = hotelJson.getString("@id");
        }
        JSONObject roomsJson = hotelJson.getJSONObject("rooms");
        List<RoomBookingInfo> result = new ArrayList<>();
        if (roomsJson.getString("@count").equals("1")) {
            JSONObject roomJson = roomsJson.getJSONObject("room");
            result = this.generateRoomTypeByJSON(roomJson, result, hid, fromDate, toDate);
        } else {
            JSONArray roomsArrrayJson = roomsJson.getJSONArray("room");
            for (int i = 0; i < roomsArrrayJson.size(); i++) {
                JSONObject roomJson = roomsArrrayJson.getJSONObject(i);
                result = this.generateRoomTypeByJSON(roomJson, result, hid, fromDate, toDate);
            }
        }
        return result;
    }

    private List<RoomBookingInfo> generateRoomTypeByJSON(JSONObject roomJson, List<RoomBookingInfo> result, String hid, String fromDate, String toDate) {
        if (roomJson.getString("@count").equals("1")) {
            JSONObject roomTypeJson = roomJson.getJSONObject("roomType");
            List<RoomBookingInfo> rlist = getRoomBaseInfoByJson(roomTypeJson, hid, fromDate, toDate);
            result.addAll(rlist);
        } else {
            JSONArray roomArrayJson = roomJson.getJSONArray("roomType");
            for (int i = 0; i < roomArrayJson.size(); i++) {
                JSONObject roomTypeJson = roomArrayJson.getJSONObject(i);
                List<RoomBookingInfo> rlist = getRoomBaseInfoByJson(roomTypeJson, hid, fromDate, toDate);
                result.addAll(rlist);
            }
        }
        return result;
    }


    public RoomBookingInfo getRoomBookingByJson(JSONObject rateJson, JSONObject roomTypeJson, String hid, String fromDate, String toDate) {
        RoomBookingInfo roomBookingInfo = new RoomBookingInfo();
        roomBookingInfo.setHotelId(hid);
        roomBookingInfo.setSpecials(roomTypeJson.getString("specials"));
        roomBookingInfo.setName(roomTypeJson.getString("name"));
        roomBookingInfo.setRoomTypeCode(roomTypeJson.getString("@roomtypecode"));
        roomBookingInfo.setTwin(roomTypeJson.getString("twin"));
        roomBookingInfo.setRoomInfo(roomTypeJson.getString("roomInfo"));
        roomBookingInfo.setFromDate(fromDate);
        roomBookingInfo.setToDate(toDate);
        roomBookingInfo.setCancellationRules(rateJson.getString("cancellationRules"));
        roomBookingInfo.setRateDescription(rateJson.getString("@description"));
        roomBookingInfo.setMinStay(rateJson.getString("minStay"));
        roomBookingInfo.setDateApplyMinStay(rateJson.getString("dateApplyMinStay"));
        roomBookingInfo.setAllowsSpecialRequests(rateJson.getString("allowsSpecialRequests"));
        roomBookingInfo.setAllowsBeddingPreference(rateJson.getString("allowsBeddingPreference"));
        roomBookingInfo.setWithinCancellationDeadline(rateJson.getString("withinCancellationDeadline"));
        roomBookingInfo.setDates(rateJson.getString("dates"));
        roomBookingInfo.setAllowsExtraMeals(rateJson.getString("allowsExtraMeals"));
        roomBookingInfo.setTariffNotes(rateJson.getString("tariffNotes"));
        roomBookingInfo.setRateType(rateJson.getString("rateType"));
        roomBookingInfo.setPassengerNamesRequiredForBooking(rateJson.getString("passengerNamesRequiredForBooking"));
        roomBookingInfo.setTotal(rateJson.getString("total"));
        roomBookingInfo.setIsBookable(rateJson.getString("isBookable"));
        roomBookingInfo.setLeftToSell(rateJson.getString("leftToSell"));
        roomBookingInfo.setAllocationDetails(rateJson.getString("allocationDetails"));
        roomBookingInfo.setStatus(rateJson.getString("status"));
        roomBookingInfo.setRateBasisId(rateJson.getString("@id"));
        return roomBookingInfo;
    }

    public List<RoomBookingInfo> getRoomBaseInfoByJson(JSONObject roomTypeJson, String hid, String fromDate, String toDate) {
        log.debug("getRoomBaseInfo start.");
        JSONObject rateBaseJson = roomTypeJson.getJSONObject("rateBases");
        List<RoomBookingInfo> rlist = new ArrayList<>();
        if (rateBaseJson.getString("@count").equals("1")) {
            JSONObject rateJson = rateBaseJson.getJSONObject("rateBasis");
            // 判断价格中是否有changedOccupancy，有则过滤掉
            if (StringUtils.isNotEmpty(rateJson.getString("changedOccupancy"))) {
                return rlist;
            }
            RoomBookingInfo room = getRoomBookingByJson(rateJson, roomTypeJson, hid, fromDate, toDate);
            // 需要改造判断方法，根据roomtype和fromdate和todate判断唯一，如果有值，更新此值
            log.debug("findroomtypecode start.");
            RoomBookingInfo sroom = roomBookingInfoRepository.findByRoomTypeCodeAndAllocationDetails(room.getRoomTypeCode(), room.getAllocationDetails());
            log.debug("findroomtypecode end.");
            if (sroom != null) {
                room.setId(sroom.getId());
            }
            roomBookingInfoRepository.save(room);
            rlist.add(room);
        } else {
            JSONArray rateArrayJson = rateBaseJson.getJSONArray("rateBasis");
            for (int i = 0; i < rateArrayJson.size(); i++) {
                JSONObject rateJson = rateArrayJson.getJSONObject(i);
                // 判断价格中是否有changedOccupancy，有则过滤掉
                if (StringUtils.isNotEmpty(rateJson.getString("changedOccupancy"))) {
                    continue;
                }
                RoomBookingInfo room = getRoomBookingByJson(rateJson, roomTypeJson, hid, fromDate, toDate);
                log.debug("findroomtypecode start.");
                RoomBookingInfo sroom = roomBookingInfoRepository.findByRoomTypeCodeAndAllocationDetails(room.getRoomTypeCode(), room.getAllocationDetails());
                log.debug("findroomtypecode end.");
                if (sroom != null) {
                    room.setId(sroom.getId());
                }
                roomBookingInfoRepository.save(room);
                rlist.add(room);
            }
        }
        log.debug("getRoomBaseInfo end.");
        return rlist;
    }

    /**
     * 通过allocationDetails保存订单初始信息
     * @param allocationDetails
     * @param plist
     * @return
     * @throws Exception
     */
    public BookingOrderInfo saveBookingByAllocation(String allocationDetails, List<Passenger> plist) throws Exception {
        BookingOrderInfo bookingOrderInfo = bookingOrderInfoRepository.findByAllocationDetails(allocationDetails);
        if (bookingOrderInfo == null) {
            bookingOrderInfo = new BookingOrderInfo();
        }
        RoomBookingInfo roomBookingInfo = roomBookingInfoRepository.findByAllocationDetails(allocationDetails);
        if (roomBookingInfo == null) {
            throw new Exception("room is null");
        }
        bookingOrderInfo.setHotelId(roomBookingInfo.getHotelId());
        bookingOrderInfo.setRoomTypeCode(roomBookingInfo.getRoomTypeCode());
        bookingOrderInfo.setAllocationDetails(allocationDetails);
        bookingOrderInfo.setSelectedRateBasis(roomBookingInfo.getRateBasisId());
        bookingOrderInfo.setActualAdults("2");
        bookingOrderInfo.setChildren("0");
        bookingOrderInfo.setPassengerNationality("168");
        bookingOrderInfo.setFromDate(roomBookingInfo.getFromDate());
        bookingOrderInfo.setToDate(roomBookingInfo.getToDate());
        bookingOrderInfo.setPassengerInfos(JSONObject.toJSONString(plist));
        bookingOrderInfo.setCurrency(JSON.parseObject(roomBookingInfo.getRateType()).getString("@currencyid"));
        bookingOrderInfo.setOrderStatus(OrderStatus.SAVED);
        return bookingOrderInfoRepository.save(bookingOrderInfo);
    }

    public void updateBookingOrderStatus(BookingOrderInfo orderInfo, OrderStatus orderStatus) {
        orderInfo.setOrderStatus(orderStatus);
        bookingOrderInfoRepository.save(orderInfo);
    }

    /**
     * 根据dotw返回报文更新order信息
     * @param jsonObject
     * @param orderInfo
     * @return
     */
    public BookingOrderInfo updateBookingByJSON(JSONObject jsonObject, BookingOrderInfo orderInfo) {
        orderInfo.setConfirmationText(jsonObject.getString("confirmationText"));
        JSONObject bookingJson = null;
        JSONArray bookingArray = null;
        try {
            bookingJson = jsonObject.getJSONObject("bookings").getJSONObject("booking");
        } catch (Exception e) {
            bookingArray = jsonObject.getJSONObject("bookings").getJSONArray("booking");
        }
//        orderInfo.setPaymentGuaranteedBy(bookingJson.getString("paymentGuaranteedBy"));
//        orderInfo.setServicePrice(bookingJson.getString("servicePrice"));
//        orderInfo.setServicePriceValue(bookingJson.getJSONObject("servicePrice").getString("#text"));
//        orderInfo.setBookingReferenceNumber(bookingJson.getString("bookingReferenceNumber"));
//        orderInfo.setPrice(bookingJson.getString("price"));
//        orderInfo.setPriceValue(bookingJson.getJSONObject("price").getString("#text"));
//        orderInfo.setVoucher(bookingJson.getString("voucher"));
//        orderInfo.setBookingStatus(bookingJson.getString("bookingStatus"));
//        orderInfo.setEmergencyContacts(bookingJson.getString("emergencyContacts"));
//        orderInfo.setBookingCode(bookingJson.getString("bookingCode"));
//        orderInfo.setMealsPrice(bookingJson.getString("mealsPrice"));
//        orderInfo.setMealsPriceValue(bookingJson.getJSONObject("mealsPrice").getString("#text"));
//        orderInfo.setType(bookingJson.getString("type"));
        // 根据订单json保存子订单信息
        String referenceNumber = "";
        String price = "";
        String priceValue = "";
        if (bookingJson != null) {
            SubOrder subOrder = this.generateSubOrder(bookingJson, orderInfo);
            referenceNumber = subOrder.getBookingReferenceNumber();
            price = subOrder.getPrice();
            priceValue = subOrder.getPriceValue();
        } else if (bookingArray != null) {
            BigDecimal priceB = new BigDecimal(0);
            for (int i = 0; i < bookingArray.size(); i++) {
                JSONObject bookJson = bookingArray.getJSONObject(i);
                SubOrder subOrder = this.generateSubOrder(bookJson, orderInfo);
                referenceNumber = referenceNumber + subOrder.getBookingReferenceNumber() + ",";
                price = price + subOrder.getPrice() + ",";
                priceB.add(new BigDecimal(subOrder.getPriceValue()));
            }
            priceValue = priceB.toString();
        }
        orderInfo.setReturnedCode(jsonObject.getString("returnedCode"));
        orderInfo.setTariffNotes(jsonObject.getString("tariffNotes"));
        orderInfo.setBookingReferenceNumber(referenceNumber);
        orderInfo.setOrderStatus(OrderStatus.CONFIRMED);
        orderInfo.setPrice(price);
        orderInfo.setPriceValue(priceValue);
        return bookingOrderInfoRepository.save(orderInfo);
    }

    // 构建并保存子订单信息
    private SubOrder generateSubOrder(JSONObject bookingJson, BookingOrderInfo bookingOrderInfo) {
        SubOrder orderInfo = new SubOrder();
        orderInfo.setOrderId(bookingOrderInfo.getId());
        orderInfo.setPaymentGuaranteedBy(bookingJson.getString("paymentGuaranteedBy"));
        orderInfo.setServicePrice(bookingJson.getString("servicePrice"));
        orderInfo.setServicePriceValue(bookingJson.getJSONObject("servicePrice").getString("#text"));
        orderInfo.setBookingReferenceNumber(bookingJson.getString("bookingReferenceNumber"));
        orderInfo.setPrice(bookingJson.getString("price"));
        orderInfo.setPriceValue(bookingJson.getJSONObject("price").getString("#text"));
        orderInfo.setVoucher(bookingJson.getString("voucher"));
        orderInfo.setBookingStatus(bookingJson.getString("bookingStatus"));
        orderInfo.setEmergencyContacts(bookingJson.getString("emergencyContacts"));
        orderInfo.setBookingCode(bookingJson.getString("bookingCode"));
        orderInfo.setMealsPrice(bookingJson.getString("mealsPrice"));
        orderInfo.setMealsPriceValue(bookingJson.getJSONObject("mealsPrice").getString("#text"));
        orderInfo.setType(bookingJson.getString("type"));
        return subOrderRepository.save(orderInfo);
    }

    /**
     * 预取消订单方法
     * @param orderInfo
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public BookingOrderInfo preCancelOrder(BookingOrderInfo orderInfo, JSONObject jsonObject) {
        JSONObject penaltyApplied = jsonObject.getJSONObject("services").
                getJSONObject("service").getJSONObject("cancellationPenalty").getJSONObject("charge");
        orderInfo.setPenaltyApplied(penaltyApplied.getString("#text"));
        orderInfo.setOrderStatus(OrderStatus.PRECANCLED);
        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(orderInfo.getId());
        for (SubOrder subOrder : subOrders) {
            subOrder.setPenaltyApplied(penaltyApplied.getString("#text"));
            subOrderRepository.save(subOrder);
        }
        return bookingOrderInfoRepository.save(orderInfo);
    }

    public BookingOrderInfo getOneByAllocation(String allocation) {
        return bookingOrderInfoRepository.findByAllocationDetails(allocation);
    }

    public BookingOrderInfo preCancelOrderManual(BookingOrderInfo orderInfo) {
        orderInfo.setPenaltyApplied("0");
        orderInfo.setOrderStatus(OrderStatus.PRECANCLED);
        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(orderInfo.getId());
        for (SubOrder subOrder : subOrders) {
            subOrder.setPenaltyApplied("0");
            subOrderRepository.save(subOrder);
        }
        return bookingOrderInfoRepository.save(orderInfo);
    }

    public BookingOrderInfo getOneById(String id) {
        return bookingOrderInfoRepository.findOne(Long.valueOf(id));
    }

}
