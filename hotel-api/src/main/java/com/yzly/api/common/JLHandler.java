package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.util.MD5Util;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.jl.JLOrderInfo;
import com.yzly.core.domain.jl.JLOrderRoomInfo;
import com.yzly.core.service.EventAttrService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 捷旅请求处理类
 * @author lazyb
 * @create 2020/4/14
 * @desc
 **/
@CommonsLog
@Component
public class JLHandler {

    @Value("${jl.api.appkey}")
    private String JLApiAppKey;
    @Value("${jl.api.secretkey}")
    private String JLApiSecretKey;
    @Value("${jl.api.url}")
    private String JLApiUrl;
    @Value("${jl.connect-request.time-out}")
    private int JLConnectRequesTimeout;
    @Value("${jl.connect.time-out}")
    private int JLConnectTimeout;
    @Value("${jl.read.time-out}")
    private int JLReadTimeout;

    @Autowired
    private EventAttrService eventAttrService;

    private static final String JL_PAGE_SIZE = "JL_PAGE_SIZE";

    /**
     * 捷旅通用请求头部封装方法
     * @return
     */
    public JSONObject generateRequestJsonHead() {
        Map<String, Object> headMap = new HashMap<>();
        String timestamp = String.valueOf(System.currentTimeMillis());
        headMap.put("appKey", JLApiAppKey);
        headMap.put("timestamp", timestamp);
        String sign = MD5Util.toMD5(MD5Util.toMD5(JLApiSecretKey + JLApiAppKey) + timestamp);
        headMap.put("sign", sign);
        headMap.put("version", "3.0.0");
        JSONObject head = new JSONObject(headMap);
        log.info("head:" + head);
        return head;
    }

    /**
     * 城市查询
     * @return
     */
    public String cityQuery(int pageIndex) {
        EventAttr pageAttr = eventAttrService.findByType(JL_PAGE_SIZE);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("pageIndex", pageIndex);
        dataMap.put("pageSize", Integer.valueOf(pageAttr.getEventValue()));
        JSONObject data = new JSONObject(dataMap);
        String res = sendGetRequest(generateRequestJsonHead(), data, "/api/city/queryCity.json?reqData={1}");
        return res;
    }

    /**
     * 酒店列表查询
     * @param countryId
     * @param pageIndex
     * @return
     */
    public String queryHotelList(Integer countryId, Integer pageIndex) {
        EventAttr pageAttr = eventAttrService.findByType(JL_PAGE_SIZE);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("countryId", countryId);
        dataMap.put("pageIndex", pageIndex);
        dataMap.put("pageSize", Integer.valueOf(pageAttr.getEventValue()));
        JSONObject data = new JSONObject(dataMap);
        String res = sendGetRequest(generateRequestJsonHead(), data, "/api/hotel/queryHotelList.json?reqData={1}");
        return res;
    }

    /**
     * 酒店明细查询
     * @param hotelId
     * @return
     */
    public String queryHotelDetail(Integer hotelId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hotelId", hotelId);
        dataMap.put("params", "1,2,3,4");
        JSONObject data = new JSONObject(dataMap);
        String res = sendGetRequest(generateRequestJsonHead(), data, "/api/hotel/queryHotelDetail.json?reqData={1}");
        return res;
    }

    /**
     * 按照默认请求参数获取酒店价格数据
     * @param hotelId
     * @param checkInDate
     * @param checkOutDate
     * @return
     */
    public String queryHotelPriceByDefault(Integer hotelId, String checkInDate, String checkOutDate, boolean abroad) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hotelId", hotelId);
        dataMap.put("checkInDate", checkInDate);
        dataMap.put("checkOutDate", checkOutDate);
        JSONObject data = new JSONObject(dataMap);
        String res = "";
        if (!abroad) {
            res = sendGetRequest(generateRequestJsonHead(), data, "/api/hotel/queryRatePlan.json?reqData={1}");
        } else {
            res = sendGetRequest(generateRequestJsonHead(), data, "/api/hotel/queryAbroadeRatePlan.json?reqData={1}");
        }
        return res;
    }

    /**
     * 根据用户上送的查询请求json串去获取酒店价格数据
     * @param req
     * @param abroad
     * @return
     */
    public String queryHotelPriceByUser(JSONObject req, boolean abroad) {
        String res = "";
        if (!abroad) {
            res = sendGetRequest(generateRequestJsonHead(), req, "/api/hotel/queryRatePlan.json?reqData={1}");
        } else {
            res = sendGetRequest(generateRequestJsonHead(), req, "/api/hotel/queryAbroadeRatePlan.json?reqData={1}");
        }
        return res;
    }

    /**
     * 订单报价接口
     * @param jlOrderInfo
     * @param roomInfos
     * @return
     */
    public String queryOrderPrice(JLOrderInfo jlOrderInfo, JSONArray roomInfos) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hotelId", jlOrderInfo.getHotelId());
        dataMap.put("keyId", jlOrderInfo.getKeyId());
        dataMap.put("checkInDate", jlOrderInfo.getCheckInDate());
        dataMap.put("checkOutDate", jlOrderInfo.getCheckOutDate());
        dataMap.put("nightlyPrices", jlOrderInfo.getNightlyPrices());
        if (roomInfos != null) {
            dataMap.put("roomGroups", roomInfos);
        }
        JSONObject data = new JSONObject(dataMap);
        String res = sendPostRequest(generateRequestJsonHead(), data, "/api/hotel/queryOrderPrice.json");
        return res;
    }

    /**
     * 创建订单接口
     * @param jlOrderInfo
     * @param jlOrderRoomInfos
     * @return
     */
    public String createOrder(JLOrderInfo jlOrderInfo, List<JLOrderRoomInfo> jlOrderRoomInfos) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("customerOrderCode", jlOrderInfo.getCustomerOrderCode());
        dataMap.put("hotelId", jlOrderInfo.getHotelId());
        dataMap.put("keyId", jlOrderInfo.getKeyId());
        dataMap.put("checkInDate", jlOrderInfo.getCheckInDate());
        dataMap.put("checkOutDate", jlOrderInfo.getCheckOutDate());
        dataMap.put("nightlyPrices", jlOrderInfo.getNightlyPrices());
        dataMap.put("totalPrice", jlOrderInfo.getTotalPrice());
        if (!StringUtils.isEmpty(jlOrderInfo.getHotelRemark())) {
            dataMap.put("hotelRemark", jlOrderInfo.getHotelRemark());
        }
        List<Object> rooms = new ArrayList<>();
        for (JLOrderRoomInfo orderRoomInfo : jlOrderRoomInfos) {
            Map<String, Object> room = new HashMap<>();
            room.put("adults", orderRoomInfo.getAdults());
            if (orderRoomInfo.getChildren() != null) {
                room.put("children", orderRoomInfo.getChildren());
                room.put("childAges", orderRoomInfo.getChildAges());
            }
            room.put("checkInPersions", JSONArray.parseArray(orderRoomInfo.getCheckInPersions()));
            rooms.add(room);
        }
        dataMap.put("roomGroups", rooms);
        JSONObject data = new JSONObject(dataMap);
        String res = sendPostRequest(generateRequestJsonHead(), data, "/api/order/createOrder.json");
        return res;
    }

    /**
     * 发送get方法到捷旅
     * @param head
     * @param data
     * @param url
     * @return
     */
    public String sendGetRequest(JSONObject head, JSONObject data, String url) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(JLConnectRequesTimeout);
        httpRequestFactory.setConnectTimeout(JLConnectTimeout);
        httpRequestFactory.setReadTimeout(JLReadTimeout);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        JSONObject reqJson = new JSONObject();
        reqJson.put("head", head);
        reqJson.put("data", data);
        log.info("jl req:" + reqJson.toJSONString());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(JLApiUrl + url, String.class, reqJson.toJSONString());
        String resp = responseEntity.getBody();
        log.info("jl resp:" + resp);
        return resp;
    }

    /**
     * 发送post方法到捷旅
     * @param head
     * @param data
     * @param url
     * @return
     */
    public String sendPostRequest(JSONObject head, JSONObject data, String url) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(JLConnectRequesTimeout);
        httpRequestFactory.setConnectTimeout(JLConnectTimeout);
        httpRequestFactory.setReadTimeout(JLReadTimeout);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        JSONObject reqJson = new JSONObject();
        reqJson.put("head", head);
        reqJson.put("data", data);
        log.info("jl req:" + reqJson.toJSONString());
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("reqData", reqJson.toJSONString());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(JLApiUrl + url, request, String.class);
        String resp = response.getBody();
        log.info("jl resp:" + resp);
        return resp;
    }

}
