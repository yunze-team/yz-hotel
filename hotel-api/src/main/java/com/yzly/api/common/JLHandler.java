package com.yzly.api.common;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.util.MD5Util;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
        return head;
    }

    /**
     * 城市查询
     * @return
     */
    public String cityQuery() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("pageIndex", 0);
        dataMap.put("pageSize", 100);
        JSONObject data = new JSONObject(dataMap);
        String res = sendGetRequest(generateRequestJsonHead(), data, "/api/city/queryCity.json?reqData={1}");
        return res;
    }

    public String sendGetRequest(JSONObject head, JSONObject data, String url) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(JLConnectRequesTimeout);
        httpRequestFactory.setConnectTimeout(JLConnectTimeout);
        httpRequestFactory.setReadTimeout(JLReadTimeout);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        JSONObject reqJson = new JSONObject();
        reqJson.put("head", head);
        reqJson.put("data", data);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(JLApiUrl + url, String.class, reqJson.toJSONString());
        return responseEntity.getBody();
    }

}
