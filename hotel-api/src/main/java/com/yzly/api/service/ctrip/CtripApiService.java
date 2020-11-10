package com.yzly.api.service.ctrip;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.CtripRespHandler;
import com.yzly.api.common.JLHandler;
import com.yzly.api.util.ctrip.AuthUtil;
import com.yzly.core.domain.ctrip.CtripOrderInfo;
import com.yzly.core.domain.ctrip.CtripXmlLog;
import com.yzly.core.service.ctrip.CtripXmlLogService;
import com.yzly.core.service.jl.JLStaticService;
import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.Element;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lazyb
 * @create 2020/9/8
 * @desc
 **/
@Service
@CommonsLog
public class CtripApiService {

    private static final String ECHO_TOKEN = "echoToken";
    private static final String ELEMENT = "otaRequest";

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CtripRespHandler ctripRespHandler;
    @Autowired
    private JLHandler jlHandler;
    @Autowired
    private CtripXmlLogService ctripXmlLogService;
    @Autowired
    private JLStaticService jlStaticService;

    /**
     * 处理携程check接口请求
     * @param xml
     * @return
     * @throws Exception
     */
    public String executeCtripCheckApi(String xml) throws Exception {
        String traceId = MDC.get("TRACE_ID");
        String requestName = "OTA_HotelAvailRQ";
        Map<String, Object> reqMap = authUtil.judgeCtripAuth(xml, requestName);
        String echoToken = reqMap.get(ECHO_TOKEN).toString();
        Element otaRequest = (Element) reqMap.get(ELEMENT);
        JSONObject reqJson = ctripRespHandler.genJLQueryRateReqByCtripXml(otaRequest);
        CtripXmlLog ctripXmlLog = ctripXmlLogService.addLogByReq(traceId, requestName, echoToken, xml, reqJson);
        String res = jlHandler.queryHotelPriceByUser(reqJson, false);
        JSONObject respJson = JSONObject.parseObject(res).getJSONObject("result");
        // 保存捷旅价格数据
        jlStaticService.syncHotelPriceByJson(respJson);
        String resXml = ctripRespHandler.genCtripXmlByJLRespOnQueryRate(respJson, requestName, reqJson);
        log.info(resXml);
        ctripXmlLogService.reLogByResp(ctripXmlLog, resXml, respJson, respJson.getString("respId"));
        return resXml;
    }

    /**
     * 处理携程create接口请求
     * @param xml
     * @return
     * @throws Exception
     */
    public String executeCtripCreateApi(String xml) throws Exception {
        String traceId = MDC.get("TRACE_ID");
        String requestName = "OTA_HotelResRQ";
        Map<String, Object> reqMap = authUtil.judgeCtripAuth(xml, requestName);
        String echoToken = reqMap.get(ECHO_TOKEN).toString();
        Element otaRequest = (Element) reqMap.get(ELEMENT);
        CtripXmlLog ctripXmlLog = ctripXmlLogService.addLogByReq(traceId, requestName, echoToken, xml, null);
        // 保存携程订单
        CtripOrderInfo ctripOrderInfo = ctripRespHandler.createCtripOrderByXml(otaRequest);
        String resXml = ctripRespHandler.genRespXmlByCreateOrder(ctripOrderInfo, echoToken, requestName);
        log.info(resXml);
        ctripXmlLogService.reLogByResp(ctripXmlLog, resXml, null, null);
        return resXml;
    }

    /**
     * 处理携程cancel接口请求
     * @param xml
     * @return
     * @throws Exception
     */
    public String executeCtripCancelApi(String xml) throws Exception {
        String traceId = MDC.get("TRACE_ID");
        String requestName = "OTA_CancelRQ";
        Map<String, Object> reqMap = authUtil.judgeCtripAuth(xml, requestName);
        String echoToken = reqMap.get(ECHO_TOKEN).toString();
        Element otaRequest = (Element) reqMap.get(ELEMENT);
        CtripXmlLog ctripXmlLog = ctripXmlLogService.addLogByReq(traceId, requestName, echoToken, xml, null);
        // 取消携程订单并生成返回xml报文
        String resXml = ctripRespHandler.genCancelXmlResp(otaRequest, requestName, echoToken);
        log.info(resXml);
        ctripXmlLogService.reLogByResp(ctripXmlLog, resXml, null, null);
        return resXml;
    }

    /**
     * 处理携程read接口请求
     * @param xml
     * @return
     * @throws Exception
     */
    public String executeCtripReadApi(String xml) throws Exception {
        String traceId = MDC.get("TRACE_ID");
        String requestName = "OTA_ReadRQ";
        Map<String, Object> reqMap = authUtil.judgeCtripAuth(xml, requestName);
        String echoToken = reqMap.get(ECHO_TOKEN).toString();
        Element otaRequest = (Element) reqMap.get(ELEMENT);
        CtripXmlLog ctripXmlLog = ctripXmlLogService.addLogByReq(traceId, requestName, echoToken, xml, null);
        String resXml = ctripRespHandler.genReadOrderXmlResp(otaRequest, requestName, echoToken);
        log.info(resXml);
        ctripXmlLogService.reLogByResp(ctripXmlLog, resXml, null, null);
        return resXml;
    }

}
