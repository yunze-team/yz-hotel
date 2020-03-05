package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.api.service.dotw.MeitApiService;
import com.yzly.api.util.meit.MeitResultUtil;
import com.yzly.api.util.meit.international.MeitReqUtil;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.domain.meit.dto.GoodsSearchQuery;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.domain.meit.dto.OrderCreateParam;
import com.yzly.core.enums.meit.ResultEnum;
import com.yzly.core.repository.dotw.RoomBookingInfoRepository;
import com.yzly.core.util.SnowflakeIdWorker;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@RestController
@RequestMapping(value = "/api/test")
@CommonsLog
public class TestController {
    @Autowired
    private RoomBookingInfoRepository roomBookingInfoRepository;
    @Autowired
    private DCMLHandler dcmlHandler;
    @Autowired
    private MeitApiService meitApiService;

    @Value("${snowflake.workId}")
    private long workId;
    @Value("${snowflake.datacenterId}")
    private long datacenterId;

//    @PostMapping("/booking")
//    public Object testBooking(String allocationDetails) {
//        RoomBookingInfo roomBookingInfo = roomBookingInfoRepository.findByAllocationDetails(allocationDetails);
//        Passenger passenger1 = new Passenger("147", "YAN", "DONG");
//        Passenger passenger2 = new Passenger("148", "LING", "ZHILING");
//        List<Passenger> plist = new ArrayList<>();
//        plist.add(passenger1);
//        plist.add(passenger2);
//        String fromDate = DateTime.now().toString("yyyy-MM-dd");
//        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
//        JSONObject jsonObject = dcmlHandler.confirmBooking(roomBookingInfo, plist, fromDate, toDate);
//        log.info("booking return: " + jsonObject);
//        return jsonObject;
//    }

    @PostMapping("/search")
    public Object searchBooking(String firstName, String lastName, String city) {
        return dcmlHandler.searchBooking(new Passenger("147", firstName, lastName), city);
    }

    private MeitResult baseRequestTrans(String json) {
        JSONObject req = JSON.parseObject(json);
        MeitResult res = MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
        res.setReqData(req);
        Long logId = new SnowflakeIdWorker(workId, datacenterId).nextId();
        res.setTraceId("YZ_" + logId);
        res.setLocalTraceId(MDC.get("TRACE_ID"));
        return meitApiService.addMeitRes(res);
    }

    @PostMapping("/goods_search")
    public Object goodsSearch(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        JSONObject reqData = result.getReqData();
        String hotelIds = reqData.getString("hotelId");
        String roomId = reqData.getString("roomId");
        String ratePlanCode = reqData.getString("ratePlanCode");
        String checkin = reqData.getString("checkin");
        String checkout = reqData.getString("checkout");
        Integer roomNumber = reqData.getInteger("roomNumber");
        Integer numberOfAdults = reqData.getInteger("numberOfAdults");
        Integer numberOfChildren = reqData.getInteger("numberOfChildren");
        String childrenAges = reqData.getString("childrenAges");
        String currencyCode = reqData.getString("currencyCode");
        GoodsSearchQuery goodsSearchQuery = new GoodsSearchQuery(hotelIds, roomId, ratePlanCode, checkin, checkout,
                roomNumber, numberOfAdults, numberOfChildren, childrenAges, currencyCode);
        List<JSONObject> jlist = dcmlHandler.getRoomsByMeitQuery(goodsSearchQuery);
        Object data = meitApiService.syncGoodsSearch(jlist, goodsSearchQuery);
        result.setData(data);
        return meitApiService.addMeitRes(result);
    }


    @PostMapping("/order_create")
    public Object orderCreate(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        if (!result.getSuccess()) {
            return result;
        }
        JSONObject reqData = result.getReqData();
        OrderCreateParam orderCreateParam = MeitReqUtil.buildOrderParam(reqData);
        Object data = meitApiService.createOrder(orderCreateParam);
        result.setData(data);
        return result;
    }

    @PostMapping("/order_cancel")
    public Object orderCancel(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        if (!result.getSuccess()) {
            return result;
        }
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        Object data = meitApiService.cancelOrderJudge(orderId);
        result.setData(data);
        return result;
    }

    @PostMapping("/order_query")
    public Object orderQuery(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        Object data = meitApiService.orderQueryResult(orderId);
        result.setData(data);
        return result;
    }

    @PostMapping("/hotel_extend")
    public Object hotelExtend(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        if (!result.getSuccess()) {
            return result;
        }
        JSONObject reqData = result.getReqData();
        String hotelIds = reqData.getString("hotelId");
        Object data = meitApiService.syncHotelExtend(hotelIds);
        result.setData(data);
        return result;
    }

    @PostMapping("/cancel")
    public Object cancel(@RequestBody String json) {
        JSONObject req = JSON.parseObject(json);
        String bookingCode = req.getString("bookingCode");
        String penaltyApplied = req.getString("penaltyApplied");
        return dcmlHandler.testCancelBooking(bookingCode, penaltyApplied, "yes");
    }

    @PostMapping("/precancel")
    public Object precancel(@RequestBody String json) {
        JSONObject req = JSON.parseObject(json);
        String bookingCode = req.getString("bookingCode");
        return dcmlHandler.testCancelBooking(bookingCode, null, "no");
    }

}
