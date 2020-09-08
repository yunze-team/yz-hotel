package com.yzly.core.service.ctrip;

import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.ctrip.CtripXmlLog;
import com.yzly.core.repository.ctrip.CtripXmlLogRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lazyb
 * @create 2020/9/8
 * @desc
 **/
@Service
@CommonsLog
public class CtripXmlLogService {

    @Autowired
    private CtripXmlLogRepository ctripXmlLogRepository;

    /**
     * 通过请求参数保存携程请求日志
     * @param traceId
     * @param requestName
     * @param echoToken
     * @param xml
     * @param req
     * @return
     */
    public CtripXmlLog addLogByReq(String traceId, String requestName,
                                   String echoToken, String xml, JSONObject req) {
        CtripXmlLog ctripXmlLog = new CtripXmlLog();
        ctripXmlLog.setEchoToken(echoToken);
        ctripXmlLog.setRequestName(requestName);
        ctripXmlLog.setTraceId(traceId);
        ctripXmlLog.setReqXml(xml);
        ctripXmlLog.setReqData(req);
        return ctripXmlLogRepository.save(ctripXmlLog);
    }

    /**
     * 通过返回参数更新携程请求日志
     * @param ctripXmlLog
     * @param xml
     * @param resp
     * @param respId
     * @return
     */
    public CtripXmlLog reLogByResp(CtripXmlLog ctripXmlLog, String xml, JSONObject resp, String respId) {
        ctripXmlLog.setRespXml(xml);
        ctripXmlLog.setRespData(resp);
        ctripXmlLog.setRespId(respId);
        return ctripXmlLogRepository.save(ctripXmlLog);
    }

}
