package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 携程动态接口报文解析及封装处理类
 * @author lazyb
 * @create 2020/9/2
 * @desc
 **/
@CommonsLog
@Component
public class CtripRespHandler {

    /**
     * 按照携程的请求xml封装查询捷旅的房型费用
     * @param otaRequest
     * @return
     * @throws Exception
     */
    public JSONObject genJLQueryRateReqByCtripXml(Element otaRequest) throws Exception {
        JSONObject req = new JSONObject();
        Element criterionRequest = otaRequest.element("AvailRequestSegments").
                element("AvailRequestSegment").element("HotelSearchCriteria").element("Criterion");
        String hotelCode = criterionRequest.element("HotelRef").attributeValue("HotelCode");
        req.put("hotelId", hotelCode);
        Element stayDateRange = criterionRequest.element("StayDateRange");
        String startDay = stayDateRange.attributeValue("Start");
        String endDay = stayDateRange.attributeValue("End");
        req.put("checkInDate", startDay);
        req.put("checkOutDate", endDay);
        Element roomStayCandidates = criterionRequest.element("RoomStayCandidates");
        JSONArray roomGroups = new JSONArray();
        List<Element> roomStayCandidateList = roomStayCandidates.elements("RoomStayCandidate");
        for (Element roomStayCandidate : roomStayCandidateList) {
            List<Element> guestCounts = roomStayCandidate.element("GuestCounts").elements("GuestCount");
            for (Element guestCount : guestCounts) {
                JSONObject roomGroup = new JSONObject();
                String ageCode = guestCount.attributeValue("AgeQualifyingCode");
                int count = 0;
                if (StringUtils.isNotEmpty(guestCount.attributeValue("Count"))) {
                    count = Integer.valueOf(guestCount.attributeValue("Count"));
                }
                if (ageCode.equals("10")) {
                    if (count == 0) {
                        count = 2;
                    }
                    roomGroup.put("adults", count);
                } else {
                    roomGroup.put("children", count);
                }
                roomGroups.add(roomGroup);
            }
        }
        req.put("roomGroups", roomGroups);
        return req;
    }

    /**
     * 根据捷旅的json返回封装携程的返回xml
     * @param resp
     * @return
     */
    public String genCtripXmlByJLRespOnQueryRate(JSONObject resp, String requestName) {
        Document doc = generateBaseResponse(requestName);
        Element otaResponse = doc.getRootElement().element("Body").element(requestName);
        if (resp.getInteger("code") != 0) {
            otaResponse.addElement("Success");
            return doc.asXML();
        }
        return null;
    }

    /**
     * 创建携程静态通用soap请求头
     * @return
     */
    public Document generateBaseResponse(String requestName) {
        Document doc = DocumentHelper.createDocument();
        Element soapRoot = doc.addElement("soap:Envelope");
        soapRoot.addNamespace("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
        soapRoot.addElement("soap:Header");
        Element soapBody = soapRoot.addElement("soap:Body");
        Element otaRequest = soapBody.addElement(requestName,
                "http://www.opentravel.org/OTA/2003/05");
        otaRequest.addAttribute("Target", "Production").
                addAttribute("PrimaryLangID", "en-us").
                addAttribute("Version", "2.1").
                addAttribute("TimeStamp", DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        return doc;
    }

}
