package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.config.meit.RestTemplateConfig;
import com.yzly.core.domain.dotw.DotwXmlLog;
import com.yzly.core.domain.jl.JLCity;
import com.yzly.core.domain.jl.JLHotelDetail;
import com.yzly.core.repository.dotw.DotwXmlLogRepository;
import com.yzly.core.repository.jl.JLCityRepository;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


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
    private String ctripStaticUrl;
    @Value("${ctrip.partnerId}")
    private String ctripPartnerId;

    @Autowired
    private JLCityRepository jlCityRepository;
    @Autowired
    private DotwXmlLogRepository dotwXmlLogRepository;

    /**
     * 创建携程静态通用soap请求头
     * @return
     */
    public Document generateStaticBaseRequest(String requestName) {
        Document doc = DocumentHelper.createDocument();
        Element soapRoot = doc.addElement("soap:Envelope");
        soapRoot.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance").
                addNamespace("xsd", "http://www.w3.org/2001/XMLSchema").
                addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        soapRoot.addElement("soap:Header");
        Element soapBody = soapRoot.addElement("soap:Body");
        Element otaRequest = soapBody.addElement(requestName,
                "http://www.opentravel.org/OTA/2003/05");
        otaRequest.addAttribute("Target", "Production").
                addAttribute("PrimaryLangID", "zh-CN").
                addAttribute("Version", "1.0").
                addAttribute("TimeStamp", DateTime.now().toString("yyyy-MM-dd'T'HH:mm:ss'Z'"));
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
        String requestName = "OTA_HotelDescriptiveContentNotifRQ";
        Document doc = generateStaticBaseRequest(requestName);
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("Body").element(requestName);
        JSONObject hotelInfoJson = jlHotelDetail.getHotelInfo();
        Element hotelContents = otaRequest.addElement("HotelDescriptiveContents").
                addAttribute("ChainName", "YUNZE").
                addAttribute("BrandName", hotelInfoJson.getString("hotelBrand")).
                addAttribute("HotelCode", jlHotelDetail.getHotelId().toString());
        Element hotelContent = hotelContents.addElement("HotelDescriptiveContent");
        Element hotelInfo = hotelContent.addElement("HotelInfo ").
                addAttribute("HotelStatus", "Active").
                addAttribute("HotelCategory", "Hotels");
        Element textItems = hotelInfo.addElement("Descriptions").
                addElement("MultimediaDescriptions").
                addElement("MultimediaDescription").addElement("TextItems");
        Element textItemCn = textItems.addElement("TextItem").
                addAttribute("Language", "zh-CN").
                addAttribute("Title", hotelInfoJson.getString("hotelNameCn"));
        textItemCn.addElement("Description").addText(hotelInfoJson.getString("introduceCn"));
        if (StringUtils.isNotEmpty(hotelInfoJson.getString("introduceEn"))) {
            Element textItemEn = textItems.addElement("TextItem").
                    addAttribute("Language", "en-US").
                    addAttribute("Title", hotelInfoJson.getString("hotelNameEn"));
            textItemEn.addElement("Description").addText(hotelInfoJson.getString("introduceEn"));
        }
        hotelInfo.addElement("Position").
                addAttribute("Longitude", hotelInfoJson.getString("longitude")).
                addAttribute("Latitude", hotelInfoJson.getString("latitude"));
        Element contractInfo = hotelContent.addElement("ContactInfos").addElement("ContactInfo");
        Element addresses = contractInfo.addElement("Addresses").addAttribute("Visible", "true");
        JLCity jlCity = jlCityRepository.findByCityId(hotelInfoJson.getInteger("cityId"));
        Element addressCn = addresses.addElement("Address").addAttribute("Language", "zh-CN");
        addressCn.addElement("CityName").addText(jlCity.getCityCn().substring(1));
        addressCn.addElement("AddressLine").addText(hotelInfoJson.getString("addressCn"));
        Element addressEn = addresses.addElement("Address").addAttribute("Language", "en-US");
        addressEn.addElement("CityName").addText(jlCity.getCityEn());
        addressEn.addElement("AddressLine").addText(hotelInfoJson.getString("addressEn"));
        Element phones = contractInfo.addElement("Phones");
        phones.addElement("Phone").addAttribute("PhoneNumber", hotelInfoJson.getString("phone")).
                addAttribute("PhoneTechType", "Voice");
        return doc;
    }

    /**
     * 推送携程房型明细方法
     * @param jlHotelDetail
     * @return
     */
    public Document pushBasicRoome(JLHotelDetail jlHotelDetail) {
        String requestName = "OTA_HotelInvNotifRQ";
        Document doc = generateStaticBaseRequest(requestName);
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("Body").element(requestName);
        JSONObject hotelInfoJson = jlHotelDetail.getHotelInfo();
        Element sellableProducts = otaRequest.addElement("SellableProducts").
                addAttribute("HotelCode", jlHotelDetail.getHotelId().toString()).
                addAttribute("HotelName", hotelInfoJson.getString("hotelNameCn"));
        JSONArray roomTypeList = jlHotelDetail.getRoomTypeList();
        // 添加售卖产品报文
        for (int i = 0; i < roomTypeList.size(); i++) {
            JSONObject roomType = roomTypeList.getJSONObject(i);
            Element sellableProduct = sellableProducts.addElement("SellableProduct").
                    addAttribute("InvTypeCode", roomType.getInteger("roomTypeId").toString()).
                    addAttribute("InvStatusType", "Active");
            // 添加房型数据
            Element guestRoom = sellableProduct.addElement("GuestRoom");
            // 添加可住人数数据，10-adult,8-child
            guestRoom.addElement("Occupancy").addAttribute("AgeQualifyingCode", "10").
                    addAttribute("MinOccupancy", "1").
                    addAttribute("MaxOccupancy", roomType.getInteger("maximize").toString());
            guestRoom.addElement("Currency").addAttribute("Code", "CNY");
            // 添加房型描述数据
            Element description = guestRoom.addElement("Description");
            description.addElement("Text").addAttribute("Language", "zh-CN").
                    addText(roomType.getString("roomTypeCn"));
            description.addElement("Text").addAttribute("Language", "en-US").
                    addText(roomType.getString("roomTypeEn"));
        }
        return doc;
    }

    /**
     * 封装查询携程酒店同步状态报文
     * @param hotelCodes
     * @return
     */
    public Document queryHotelStatus(List<String> hotelCodes) {
        String requestName = "OTA_HotelStatsNotifRQ";
        Document doc = generateStaticBaseRequest(requestName);
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("Body").element(requestName);
        Element statictics = otaRequest.addElement("Statistics");
        for (String hotelCode : hotelCodes) {
            statictics.addElement("Statistic").addAttribute("HotelCode", hotelCode);
        }
        return doc;
    }

    /**
     * 推送酒店静态信息到携程
     * @param doc
     * @return
     */
    public String sendCtripStatic(Document doc) throws Exception {
        String traceId = MDC.get("TRACE_ID");
        DotwXmlLog xmlLog = new DotwXmlLog();
        xmlLog.setTraceId(traceId);
        xmlLog.setReqXml(doc.asXML());
        log.info("发往ctrip的报文：" + doc.asXML());
        xmlLog = dotwXmlLogRepository.save(xmlLog);
        // 设置到dotw的超时时间
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> xmlEntity = new HttpEntity<>(doc.asXML(), headers);
        RestTemplate restTemplate = new RestTemplate(RestTemplateConfig.generateHttpRequestFactory());
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(ctripStaticUrl, xmlEntity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("接收ctrip的返回报文：" + responseEntity);
        if (responseEntity != null) {
            xmlLog.setRespXml(responseEntity.getBody());
            XMLSerializer xmlSerializer = new XMLSerializer();
            String resutStr = xmlSerializer.read(responseEntity.getBody()).toString();
            xmlLog.setRespData(JSONObject.parseObject(resutStr));
            dotwXmlLogRepository.save(xmlLog);
            return responseEntity.getBody();
        } else {
            return null;
        }
    }

}
