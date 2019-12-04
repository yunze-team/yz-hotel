package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.repository.dotw.BookingOrderInfoRepository;
import com.yzly.core.repository.dotw.RoomBookingInfoRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<RoomBookingInfo> addRoomBookingByGetRoomsJson(JSONObject jsonObject, String hid, String fromDate, String toDate) {
        JSONObject hotelJson = jsonObject.getJSONObject("hotel");
        JSONObject roomJson = hotelJson.getJSONObject("rooms").getJSONObject("room");
        List<RoomBookingInfo> result = new ArrayList<>();
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
        JSONObject rateBaseJson = roomTypeJson.getJSONObject("rateBases");
        List<RoomBookingInfo> rlist = new ArrayList<>();
        if (rateBaseJson.getString("@count").equals("1")) {
            JSONObject rateJson = rateBaseJson.getJSONObject("rateBasis");
            RoomBookingInfo room = getRoomBookingByJson(rateJson, roomTypeJson, hid, fromDate, toDate);
            if (roomBookingInfoRepository.findByAllocationDetails(room.getAllocationDetails()) == null) {
                roomBookingInfoRepository.save(room);
                rlist.add(room);
            }
        } else {
            JSONArray rateArrayJson = rateBaseJson.getJSONArray("rateBasis");
            for (int i = 0; i < rateArrayJson.size(); i++) {
                JSONObject rateJson = rateArrayJson.getJSONObject(i);
                RoomBookingInfo room = getRoomBookingByJson(rateJson, roomTypeJson, hid, fromDate, toDate);
                if (roomBookingInfoRepository.findByAllocationDetails(room.getAllocationDetails()) == null) {
                    roomBookingInfoRepository.save(room);
                    rlist.add(room);
                }
            }
        }
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
        BookingOrderInfo bookingOrderInfo = new BookingOrderInfo();
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
        JSONObject bookingJson = jsonObject.getJSONObject("bookings").getJSONObject("booking");
        orderInfo.setPaymentGuaranteedBy(bookingJson.getString("paymentGuaranteedBy"));
        orderInfo.setServicePrice(bookingJson.getString("servicePrice"));
        orderInfo.setBookingReferenceNumber(bookingJson.getString("bookingReferenceNumber"));
        orderInfo.setPrice(bookingJson.getString("price"));
        orderInfo.setVoucher(bookingJson.getString("voucher"));
        orderInfo.setBookingStatus(bookingJson.getString("bookingStatus"));
        orderInfo.setEmergencyContacts(bookingJson.getString("emergencyContacts"));
        orderInfo.setBookingCode(bookingJson.getString("bookingCode"));
        orderInfo.setMealsPrice(bookingJson.getString("mealsPrice"));
        orderInfo.setType(bookingJson.getString("type"));
        orderInfo.setReturnedCode(jsonObject.getString("returnedCode"));
        orderInfo.setOrderStatus(OrderStatus.CONFIRMED);
        return bookingOrderInfoRepository.save(orderInfo);
    }

}
