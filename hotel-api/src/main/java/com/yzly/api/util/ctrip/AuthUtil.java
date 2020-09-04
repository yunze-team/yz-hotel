package com.yzly.api.util.ctrip;

import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lazyb
 * @create 2020/9/2
 * @desc
 **/
@Component
@CommonsLog
public class AuthUtil {

    @Value("${ctrip.auth.id}")
    private String ctripAuthId;
    @Value("${ctrip.auth.pwd}")
    private String ctripAuthPwd;
    @Value("${ctrip.partnerId}")
    private String ctripPartnerId;

    /**
     * 判断识别携程送来的报文头，并返回echotoken
     * @param xml
     * @param requestName
     * @return
     */
    public String judgeCtripAuth(String xml, String requestName) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            log.error(e.getMessage());
            return null;
        }
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("Body").element(requestName);
        String echoToken = otaRequest.attributeValue("EchoToken");
        Element requestorId = otaRequest.element("POS").element("Source").element("RequestorID");
        log.info("RequestorID: " + requestorId.asXML());
        String id = requestorId.attributeValue("ID");
        String pwd = requestorId.attributeValue("MessagePassword");
        String codeContext = requestorId.element("CompanyName").attributeValue("CodeContext");
        // 判断携程的报文头是否匹配，不匹配则返回为空
        if (!ctripAuthId.equals(id) || !ctripAuthPwd.equals(pwd) || !codeContext.equals(ctripPartnerId)) {
            return null;
        }
        return echoToken;
    }

}
