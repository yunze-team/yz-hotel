package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.ribbon.proxy.annotation.Http;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@RestController
@RequestMapping("/api/meit")
@CommonsLog
public class MeitApiController {

    @Value("${meituan.clientSecret}")
    private String secret;

    @Autowired
    private MeitApiService meitApiService;
    @Autowired
    private DCMLHandler dcmlHandler;

    /**
     * 从request中获取body 通过getReader()获取
     * @author syx
     * @param request
     */
    private String getBodyStringByReader(HttpServletRequest request){
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            bufferedReader = request.getReader();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!= null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private MeitResult generateByRequest(HttpServletRequest request, String encyptData) {
        String traceId = request.getHeader("Request-Trace");
        log.info("request traceId:" + traceId);
        MeitTraceLog traceLog = meitApiService.addTraceLog(traceId, encyptData, null, null);
        log.info("traceLog: " + JSONObject.toJSONString(traceLog));
        ResultEnum resultEnum = new AuthValidatorUtil().validate(request, secret);
        if (!resultEnum.equals(ResultEnum.SUCCESS)) {
            return MeitResultUtil.generateResult(resultEnum, null);
        }
        try {
            String reqData = AESUtilUsingCommonDecodec.decrypt(encyptData);
            log.info("meit req data:" + reqData);
//            String reqData = encyptData;
            // 更新美团调用日志的解密数据
            meitApiService.addTraceLog(traceId, encyptData, reqData, traceLog);
            JSONObject req = JSON.parseObject(reqData);
            MeitResult res = MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
            res.setReqData(req);
            res.setTraceId(traceId);
            return meitApiService.addMeitRes(res);
        } catch (Exception e) {
            log.error(e.getMessage());
            return MeitResultUtil.generateResult(ResultEnum.FAIL, null);
        }
    }

    private MeitResult postRequestTrans(HttpServletRequest request) {
        String body = this.getBodyStringByReader(request);
        log.info("request body:" + body);
        return this.generateByRequest(request, body);
    }

    /**
     * 基础请求传递方法，负责将美团请求进行认证，并解密数据，记录日志
     * @param request
     * @return
     * @throws Exception
     */
    private MeitResult baseRequestTrans(HttpServletRequest request) {
        log.info("request paramter names:" + JSONObject.toJSONString(request.getParameterNames()));
        String encyptData = request.getParameter("param");
        log.info("request encyptData:" + encyptData);
        return this.generateByRequest(request, encyptData);
    }

    /**
     * 基础美团请求返回方法，负责将返回值封装并加密数据
     * @param meitResult
     * @return
     */
    private String baseResponseTrans(MeitResult meitResult) {
        try {
            String result = JSONObject.toJSONString(meitResult);
            log.info(result);
            meitApiService.addMeitRes(meitResult);
            return AESUtilUsingCommonDecodec.encrypt(result);
//            return JSONObject.toJSONString(meitResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private MeitResult baseRequestTransTest(String json) {
        JSONObject req = JSON.parseObject(json);
        MeitResult res = MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
        res.setReqData(req);
        return res;
    }

    @PostMapping("/test/hotel_basic")
    public Object hotelBasicTest(@RequestBody String json) {
        MeitResult result = baseRequestTransTest(json);
        if (!result.getSuccess()) {
            return result;
        }
        JSONObject reqData = result.getReqData();
        int skip = reqData.getInteger("skip");
        int limit = reqData.getInteger("limit");
        Object data = meitApiService.syncHotelBasic(skip, limit);
        result.setData(data);
        return result;
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
        log.info(data);
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
        log.info(data);
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
        log.info(data);
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
        Object data = meitApiService.syncRoomExtend(hotelIds);
        result.setData(data);
        log.info(data);
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
        List<JSONObject> jlist = dcmlHandler.getRoomsByMeitQuery(goodsSearchQuery, true);
        Object data = meitApiService.syncGoodsSearch(jlist, goodsSearchQuery);
        result.setData(data);
        log.info(data);
        return baseResponseTrans(result);
    }

    /**
     * 订单创建
     * @param request
     * @return
     */
    @PostMapping("/order_create")
    public Object orderCreate(HttpServletRequest request) {
        MeitResult result = postRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        OrderCreateParam orderCreateParam = MeitReqUtil.buildOrderParam(reqData);
        Object data = meitApiService.createOrder(orderCreateParam);
        result.setData(data);
        log.info(data);
        return baseResponseTrans(result);
    }

    /**
     * 订单查询
     * @param request
     * @return
     */
    @PostMapping("/order_query")
    public Object orderQuery(HttpServletRequest request) {
        MeitResult result = postRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        Object data = meitApiService.orderQueryResult(orderId);
        result.setData(data);
        log.info(data);
        return baseResponseTrans(result);
    }

    /**
     * 取消订单
     * @param request
     * @return
     */
    @PostMapping("/order_cancle")
    public Object orderCancel(HttpServletRequest request) {
        MeitResult result = postRequestTrans(request);
        if (!result.getSuccess()) {
            return baseResponseTrans(result);
        }
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        Object data = meitApiService.cancelOrderJudge(orderId);
//        Object data = meitApiService.cancelOrder(orderId);
//        Object data = meitApiService.cancelOrderManaul(orderId);
        result.setData(data);
        log.info(data);
        return baseResponseTrans(result);
    }

}
