package com.yzly.core.service.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.jl.JLOrderInfo;
import com.yzly.core.domain.jl.JLOrderRoomInfo;
import com.yzly.core.repository.jl.JLOrderInfoRepository;
import com.yzly.core.repository.jl.JLOrderRoomInfoRepository;
import com.yzly.core.util.SnowflakeIdWorker;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/6/9
 * @desc
 **/
@Service
@CommonsLog
public class JLOrderService {

    @Autowired
    private JLOrderInfoRepository jlOrderInfoRepository;
    @Autowired
    private JLOrderRoomInfoRepository jlOrderRoomInfoRepository;

    @Value("${snowflake.workId}")
    private long workId;
    @Value("${snowflake.datacenterId}")
    private long datacenterId;

    /**
     * 根据预定json串保存预定单信息
     * @param preJson
     * @return
     */
    public JLOrderInfo saveOrderByPreJson(JSONObject preJson) {
        // 保存预订单信息
        JLOrderInfo jlOrderInfo = new JLOrderInfo();
        jlOrderInfo.setHotelId(preJson.getInteger("hotelId"));
        jlOrderInfo.setKeyId(preJson.getString("keyId"));
        jlOrderInfo.setCheckInDate(preJson.getString("checkInDate"));
        jlOrderInfo.setCheckOutDate(preJson.getString("checkOutDate"));
        jlOrderInfo.setNightlyPrices(preJson.getString("nightlyPrices"));
        jlOrderInfo = jlOrderInfoRepository.save(jlOrderInfo);
        // 判断是否有房间信息，如有，保存
        JSONArray roomGroups = preJson.getJSONArray("roomGroups");
        if (roomGroups != null) {
            for (int i = 0; i < roomGroups.size(); i++) {
                JSONObject room = roomGroups.getJSONObject(i);
                JLOrderRoomInfo orderRoomInfo = new JLOrderRoomInfo();
                orderRoomInfo.setAdults(room.getInteger("adults"));
                orderRoomInfo.setChildren(room.getInteger("children"));
                orderRoomInfo.setChildAges(room.getString("childAges"));
                orderRoomInfo.setCheckInPersions(room.getString("checkInPersion"));
                orderRoomInfo.setJlOrderId(jlOrderInfo.getId());
                jlOrderRoomInfoRepository.save(orderRoomInfo);
            }
        }
        return jlOrderInfo;
    }

    /**
     * 根据orderid获得预订单的房间信息
     * @param orderId
     * @return
     */
    public JSONArray getPreOrderRoomInfo(Long orderId) {
        List<JLOrderRoomInfo> roomInfos = jlOrderRoomInfoRepository.findAllByJlOrderId(orderId);
        JSONArray roomArray = new JSONArray();
        if (roomInfos != null && roomInfos.size() > 0) {
            for (JLOrderRoomInfo roomInfo : roomInfos) {
                JSONObject roomJson = new JSONObject();
                roomJson.put("adults", roomInfo.getAdults());
                roomJson.put("children", roomInfo.getChildren());
                roomJson.put("childAges", roomInfo.getChildAges());
                roomArray.add(roomJson);
            }
            return roomArray;
        }
        return null;
    }

    /**
     * 根据返回的预定消息更新预订单信息
     * @param reJson
     * @param jlOrderInfo
     * @return
     */
    public JLOrderInfo finishPreOrderByJson(JSONObject reJson, JLOrderInfo jlOrderInfo) {
        JSONObject orderPrice = reJson.getJSONObject("orderPrice");
        JSONObject bookingMessage = orderPrice.getJSONObject("bookingMessage");
        jlOrderInfo.setBookingCode(bookingMessage.getInteger("code"));
        jlOrderInfo.setBookingMsg(bookingMessage.getString("message"));
        jlOrderInfo.setCustomerOrderCode(String.valueOf(new SnowflakeIdWorker(workId, datacenterId).nextId()));
        jlOrderInfo.setTotalPrice(sumOrderTotalPrice(jlOrderInfo.getNightlyPrices()));
        return jlOrderInfoRepository.save(jlOrderInfo);
    }

    /**
     * 根据订单号获取下单房间数据
     * @param orderCode
     * @return
     */
    public List<JLOrderRoomInfo> getAllRoomByOrder(String orderCode) {
        JLOrderInfo jlOrderInfo = jlOrderInfoRepository.findByCustomerOrderCode(orderCode);
        return jlOrderRoomInfoRepository.findAllByJlOrderId(jlOrderInfo.getId());
    }

    /**
     * 完成下单
     * @param reJson
     * @param jlOrderInfo
     * @return
     */
    public JLOrderInfo finishOrderByJson(JSONObject reJson, JLOrderInfo jlOrderInfo) {
        JSONObject createOrder = reJson.getJSONObject("createOrder");
        jlOrderInfo.setOrderCode(createOrder.getString("orderCode"));
        jlOrderInfo.setOrderStauts(createOrder.getInteger("orderStatus"));
        return jlOrderInfoRepository.save(jlOrderInfo);
    }

    public JLOrderInfo getOrderByCode(String orderCode) {
        return jlOrderInfoRepository.findByCustomerOrderCode(orderCode);
    }

    private Double sumOrderTotalPrice(String nightlyPrices) {
        String[] prices = nightlyPrices.split("\\|");
        Double totalPrice = new Double(0);
        for (String price : prices) {
            totalPrice += new Double(price);
        }
        return totalPrice;
    }

}
