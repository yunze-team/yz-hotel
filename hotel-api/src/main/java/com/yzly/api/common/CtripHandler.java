package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.jl.JLCity;
import com.yzly.core.domain.jl.JLHotelDetail;
import com.yzly.core.repository.jl.JLCityRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JLCityRepository jlCityRepository;

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
        String requestName = "OTA_HotelDescriptiveContentNotifRQ";
        Document doc = generateStaticBaseRequest(requestName);
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("soap:Body").element(requestName);
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
        Element textItemEn = textItems.addElement("TextItem").
                addAttribute("Language", "en-US").
                addAttribute("Title", hotelInfoJson.getString("hotelNameEn"));
        textItemEn.addElement("Description").addText(hotelInfoJson.getString("introduceEn"));
        hotelInfo.addElement("Position").
                addAttribute("Longitude", hotelInfoJson.getString("longitude")).
                addAttribute("Latitude", hotelInfoJson.getString("latitude"));
        Element contractInfo = hotelInfo.addElement("ContactInfos").addElement("ContactInfo");
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

    public Document pushBasicRoome(JLHotelDetail jlHotelDetail) {
        String requestName = "OTA_HotelInvNotifRQ";
        Document doc = generateStaticBaseRequest(requestName);
        Element soapRoot = doc.getRootElement();
        Element otaRequest = soapRoot.element("soap:Body").element(requestName);
        JSONObject hotelInfoJson = jlHotelDetail.getHotelInfo();
        Element sellableProducts = otaRequest.addElement("SellableProducts").
                addAttribute("HotelCode", jlHotelDetail.getHotelId().toString()).
                addAttribute("HotelName", hotelInfoJson.getString("hotelNameCn"));
        JSONArray roomTypeList = jlHotelDetail.getRoomTypeList();
        for (int i = 0; i < roomTypeList.size(); i++) {
            JSONObject roomType = roomTypeList.getJSONObject(i);
            Element sellableProduct = sellableProducts.addElement("SellableProduct").
                    addAttribute("InvTypeCode", roomType.getInteger("roomTypeId").toString()).
                    addAttribute("InvStatusType", "Active");
            Element guestRoom = sellableProduct.addElement("GuestRoom");

        }
        return doc;
    }

}
