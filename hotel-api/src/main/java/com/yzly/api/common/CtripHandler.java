package com.yzly.api.common;

import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.jl.JLHotelDetail;
import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * 携程报文组装及解析处理类
 * @author lazyb
 * @create 2020/6/29
 * @desc
 **/
@CommonsLog
@Component
public class CtripHandler {

    @Value("${ctrip.id}")
    private String ctripId;
    @Value("${ctrip.password}")
    private String ctripPwd;
    @Value("${ctrip.static.url}")
    private String ctripUrl;
    @Value("${ctrip.partnerId}")
    private String ctripPartnerId;

    /**
     * 创建携程静态通用soap请求头
     * @return
     */
    public Document generateStaticBaseRequest() {
        Document doc = DocumentHelper.createDocument();
        Element soapRoot = doc.addElement("soap:Envelope");
        soapRoot.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance").
                addNamespace("xsd", "http://www.w3.org/2001/XMLSchema").
                addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        soapRoot.addElement("soap:Header");
        Element soapBody = soapRoot.addElement("soap:Body");
        Element otaRequest = soapBody.addElement("OTA_HotelDescriptiveContentNotifRQ",
                "http://www.opentravel.org/OTA/2003/05");
        otaRequest.addAttribute("Target", "Production").
                addAttribute("PrimaryLangID", "zh-CN").
                addAttribute("Version", "1.0").
                addAttribute("TimeStamp", DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        Element pos = otaRequest.addElement("POS");
        Element source = pos.addElement("Source");
        Element requestorId = source.addElement("RequestorID");
        requestorId.addAttribute("ID", ctripId).addAttribute("MessagePassword", ctripPwd).addAttribute("Type", "1");
        Element companyName = requestorId.addElement("CompanyName");
        companyName.addAttribute("Code", "C").addAttribute("CodeContext", ctripPartnerId);
        return doc;
    }

    /**
     * 推送携程酒店明细方法
     * @param jlHotelDetail
     * @return
     */
    public Document pushHotelDetail(JLHotelDetail jlHotelDetail) {
        Document doc = generateStaticBaseRequest();
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("soap:Body").element("OTA_HotelDescriptiveContentNotifRQ");
        JSONObject hotelInfoJson = jlHotelDetail.getHotelInfo();
        Element hotelContents = otaRequest.addElement("HotelDescriptiveContents").
                addAttribute("ChainName", "YUNZE").
                addAttribute("BrandName", hotelInfoJson.getString("hotelBrand")).
                addAttribute("HotelCode", jlHotelDetail.getHotelId().toString());
        Element hotelContent = hotelContents.addElement("HotelDescriptiveContent");
        Element hotelInfo = hotelContent.addElement("HotelInfo ").
                addAttribute("HotelStatus", "Active").
                addAttribute("HotelCategory", "Hotels");
        Element multiDesc = hotelInfo.addElement("Descriptions").
                addElement("MultimediaDescriptions").addElement("MultimediaDescription");
        return doc;
    }

}
