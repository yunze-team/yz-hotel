package com.yzly.api.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.util.XmlTool;
import com.yzly.core.util.PasswordEncryption;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/11/19
 * @desc
 **/
@CommonsLog
@Component
public class DCMLHandler {

    @Value("${dotw.username}")
    private String DOTWUserName;
    @Value("${dotw.password}")
    private String DOTWPassword;
    @Value("${dotw.id}")
    private String DOTWCompanyCode;
    @Value("${dotw.url}")
    private String DOTWUrl;

    public Document generateBaseRequest() {
        Document doc = DocumentHelper.createDocument();
        Element customer = doc.addElement("customer");
        Element username = customer.addElement("username");
        username.setText(DOTWUserName);
        Element password = customer.addElement("password");
        password.setText(PasswordEncryption.BCRYPT.MD5(DOTWPassword));
        Element id = customer.addElement("id");
        id.setText(DOTWCompanyCode);
        Element source = customer.addElement("source");
        source.setText("1");
        return doc;
    }

    public Document getAllCountries() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        Element request = customer.addElement("request");
        request.addAttribute("command", "getallcountries");
        Element reEl = request.addElement("return");
        Element fieldsEl = reEl.addElement("fields");
        fieldsEl.addElement("field").setText("regionName");
        fieldsEl.addElement("field").setText("regionCode");
        log.info(doc.asXML());
        return doc;
    }

    public Document getAllCitiesByCountry(String countryCode, String countryName) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        Element request = customer.addElement("request");
        request.addAttribute("command", "getallcities");
        Element reEl = request.addElement("return");
        Element filtersEl = reEl.addElement("filters");
        filtersEl.addElement("countryCode").setText(countryCode);
        filtersEl.addElement("countryName").setText(countryName);
        Element fieldsEl = reEl.addElement("fields");
        fieldsEl.addElement("field").setText("countryName");
        fieldsEl.addElement("field").setText("countryCode");
        log.info(doc.asXML());
        return doc;
    }

    public String sendDotw(Document doc) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> xmlEntity = new HttpEntity<>(doc.asXML(), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://" + DOTWUrl, xmlEntity, String.class);
        log.info(responseEntity);
        return XmlTool.documentToJSONObject(responseEntity.getBody()).toJSONString();
    }

    public Document getAllCurrencies() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        Element request = customer.addElement("request");
        request.addAttribute("command", "getcurrenciesids");
        log.info(doc.asXML());
        return doc;
    }

    public String sendDotwString(Document doc) {
        log.info(doc.asXML());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> xmlEntity = new HttpEntity<>(doc.asXML(), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://" + DOTWUrl, xmlEntity, String.class);
        log.info(responseEntity);
        return responseEntity.getBody();
    }

    /**
     * 通过dotw_hotel_code列表来获取dotw的酒店明细和房间数据
     * @param list
     * @param fromDate
     * @param toDate
     * @return
     */
    public JSONObject searchHotelPriceByID(List<String> list, String fromDate, String toDate) {
        JSONObject result = null;
        Document doc = generateBaseRequest();
        Element returnEl = generateSearchHotelHead(doc, fromDate, toDate);
        Element filters = returnEl.addElement("filters");
        filters.addAttribute("xmlns:a", "http://us.dotwconnect.com/xsd/atomicCondition").
                addAttribute("xmlns:c", "http://us.dotwconnect.com/xsd/complexCondition").
                addNamespace("a", "http://us.dotwconnect.com/xsd/atomicCondition").
                addNamespace("c", "http://us.dotwconnect.com/xsd/complexCondition");
        Element cCondition = filters.addElement("c:condition");
        Element a1 = cCondition.addElement("a:condition");
        a1.addElement("fieldName").setText("onRequest");
        a1.addElement("fieldTest").setText("equals");
        Element a1FieldValues = a1.addElement("fieldValues");
        a1FieldValues.addElement("fieldValue").setText("1");
        cCondition.addElement("operator").setText("AND");
        Element a2 = cCondition.addElement("a:condition");
        a2.addElement("fieldName").setText("hotelId");
        a2.addElement("fieldTest").setText("in");
        Element a2FieldValues = a2.addElement("fieldValues");
        for (String code : list) {
            a2FieldValues.addElement("fieldValue").setText(code);
        }
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        result = JSON.parseObject(resutStr);
        return result;
    }

    public Element generateSearchHotelHead(Document doc, String fromDate, String toDate) {
        Element customer = doc.getRootElement();
        customer.addElement("product").setText("hotel");
        Element request = customer.addElement("request");
        request.addAttribute("command", "searchhotels");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("fromDate").setText(fromDate);
        bookingDetails.addElement("toDate").setText(toDate);
        bookingDetails.addElement("currency").setText("2524");
        Element rooms = bookingDetails.addElement("rooms").addAttribute("no", "1");
        Element room = rooms.addElement("room").addAttribute("runno", "0");
        room.addElement("adultsCode").setText("2");
        room.addElement("children").addAttribute("no", "0");
        room.addElement("rateBasis").setText("-1");
        Element returnEl = request.addElement("return");
        return returnEl;
    }

    public JSONObject searchHotelInfoById(List<String> list, String fromDate, String toDate) {
        JSONObject result = null;
        Document doc = generateBaseRequest();
        Element returnEl = generateSearchHotelHead(doc, fromDate, toDate);
        Element filters = returnEl.addElement("filters");
        filters.addAttribute("xmlns:a", "http://us.dotwconnect.com/xsd/atomicCondition").
                addAttribute("xmlns:c", "http://us.dotwconnect.com/xsd/complexCondition").
                addNamespace("a", "http://us.dotwconnect.com/xsd/atomicCondition").
                addNamespace("c", "http://us.dotwconnect.com/xsd/complexCondition");
        filters.addElement("noPrice").setText("true");
        Element cCondition = filters.addElement("c:condition");
        Element a2 = cCondition.addElement("a:condition");
        a2.addElement("fieldName").setText("hotelId");
        a2.addElement("fieldTest").setText("in");
        Element a2FieldValues = a2.addElement("fieldValues");
        for (String code : list) {
            a2FieldValues.addElement("fieldValue").setText(code);
        }
        Element fields = returnEl.addElement("fields");
        String[] fieldArray = new String[] {"preferred", "builtYear", "renovationYear", "floors", "noOfRooms", "preferred", "luxury", "fullAddress",
                "description1", "description2", "hotelName", "address", "zipCode", "location", "locationId", "location1", "location2", "location3",
                "cityName", "cityCode", "stateName", "stateCode", "countryName", "countryCode", "regionName", "regionCode", "attraction", "amenitie",
                "leisure", "business", "transportation", "hotelPhone", "hotelCheckIn", "hotelCheckOut", "minAge", "rating", "images", "fireSafety",
                "hotelPreference", "direct", "geoPoint", "leftToSell", "chain", "lastUpdated", "priority"};
        for (String field : fieldArray) {
            fields.addElement("field").setText(field);
        }
        String[] roomsArray = new String[] {"name", "roomInfo", "roomAmenities", "twin"};
        for (String room : roomsArray) {
            fields.addElement("roomField").setText(room);
        }
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        result = JSON.parseObject(resutStr);
        return result;
    }

}
