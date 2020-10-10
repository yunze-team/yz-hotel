package com.yzly.api.service.ctrip;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.CtripRespHandler;
import com.yzly.api.common.JLHandler;
import com.yzly.api.util.ctrip.AuthUtil;
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
        ctripXmlLogService.reLogByResp(ctripXmlLog, resXml, respJson, respJson.getString("respId"));
        return resXml;
    }

}
