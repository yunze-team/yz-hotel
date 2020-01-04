package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.api.service.dotw.MeitApiService;
import com.yzly.api.util.meit.MeitResultUtil;
import com.yzly.api.util.meit.international.AESUtilUsingCommonDecodec;
import com.yzly.api.util.meit.international.AuthValidatorUtil;
import com.yzly.api.util.meit.international.MeitReqUtil;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.GoodsSearchQuery;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.domain.meit.dto.OrderCreateParam;
import com.yzly.core.enums.meit.ResultEnum;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@RestController
@RequestMapping("/meit")
@CommonsLog
public class MeitApiController {

    @Value("${meituan.clientSecret}")
    private String secret;

    @Autowired
    private MeitApiService meitApiService;
    @Autowired
    private DCMLHandler dcmlHandler;

    /**
     * 基础请求传递方法，负责将美团请求进行认证，并解密数据，记录日志
     * @param request
     * @return
     * @throws Exception
     */
    private MeitResult baseRequestTrans(HttpServletRequest request) {
        String encyptData = request.getParameter("param");
        String traceId = request.getHeader("Request-Trace");
        MeitTraceLog traceLog = meitApiService.addTraceLog(traceId, encyptData, null, null);
        log.info("traceLog: " + JSONObject.toJSONString(traceLog));
        ResultEnum resultEnum = new AuthValidatorUtil().validate(request, secret);
        if (!resultEnum.equals(ResultEnum.SUCCESS)) {
            return MeitResultUtil.generateResult(resultEnum, null);
        }
        try {
            String reqData = AESUtilUsingCommonDecodec.decrypt(encyptData);
            // 更新美团调用日志的解密数据
            meitApiService.addTraceLog(traceId, encyptData, reqData, traceLog);
            JSONObject req = JSON.parseObject(reqData);
            MeitResult res = MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
            res.setReqData(req);
            return res;
        } catch (Exception e) {
            log.error(e.getMessage());
            return MeitResultUtil.generateResult(ResultEnum.FAIL, null);
        }
    }

    private String baseResponseTrans(MeitResult meitResult) {
        try {
            return AESUtilUsingCommonDecodec.decrypt(JSONObject.toJSONString(meitResult));
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 同步酒店基础数据
     * @param request
     * @return
     */
    @GetMapping("/hotel_basic")
    public Object hotelBasic(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        int skip = reqData.getInteger("skip");
        int limit = reqData.getInteger("limit");
        Object data = meitApiService.syncHotelBasic(skip, limit);
        result.setData(data);
        return baseResponseTrans(result);
    }

    /**
     * 同步酒店扩展信息
     * @param request
     * @return
     */
    @GetMapping("/hotel_extend")
    public Object hotelExtend(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String hotelIds = reqData.getString("hotelId");
        Object data = meitApiService.syncHotelExtend(hotelIds);
        result.setData(data);
        return baseResponseTrans(result);
    }

    /**
     * 同步房型基础信息
     * @param request
     * @return
     */
    @GetMapping("/room_basic")
    public Object roomBasic(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String hotelIds = reqData.getString("hotelId");
        Object data = meitApiService.syncRoomBasic(hotelIds);
        result.setData(data);
        return baseResponseTrans(result);
    }

    /**
     * 同步房型扩展信息
     * @param request
     * @return
     */
    @GetMapping("/room_extend")
    public Object roomExtend(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String hotelIds = reqData.getString("hotelId");
        Object data = meitApiService.syncHotelExtend(hotelIds);
        result.setData(data);
        return baseResponseTrans(result);
    }

    /**
     * 产品信息搜索
     * @param request
     * @return
     */
    @GetMapping("/goods_search")
    public Object goodsSearch(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
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
        return baseResponseTrans(result);
    }

    /**
     * 订单创建
     * @param request
     * @return
     */
    @PostMapping("/order_create")
    public Object orderCreate(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        OrderCreateParam orderCreateParam = MeitReqUtil.buildOrderParam(reqData);
        Object data = meitApiService.createOrder(orderCreateParam);
        result.setData(data);
        return baseResponseTrans(result);
    }

    /**
     * 订单查询
     * @param request
     * @return
     */
    @PostMapping("/order_query")
    public Object orderQuery(HttpServletRequest request) {
        MeitResult result = baseRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        Object data = meitApiService.orderQueryResult(orderId);
        result.setData(data);
        return baseResponseTrans(result);
    }

}
