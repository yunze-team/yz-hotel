package com.yzly.api.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.util.XmlTool;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.util.PasswordEncryption;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
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
        Element returnEl = generateSearchHotelHead(doc, fromDate, toDate, true);
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

    /**
     * 创建酒店搜索报文的通用xml头
     * @param doc
     * @param fromDate
     * @param toDate
     * @param hasPassenger
     * @return
     */
    public Element generateSearchHotelHead(Document doc, String fromDate, String toDate, boolean hasPassenger) {
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
        if (hasPassenger) {
            room.addElement("passengerNationality").setText("168");
            room.addElement("passengerCountryOfResidence").setText("168");
        }
        Element returnEl = request.addElement("return");
        return returnEl;
    }

    /**
     * 通过酒店id列表获取酒店详细信息，包含图片等
     * @param list
     * @param fromDate
     * @param toDate
     * @return
     */
    public JSONObject searchHotelInfoById(List<String> list, String fromDate, String toDate) {
        JSONObject result = null;
        Document doc = generateBaseRequest();
        Element returnEl = generateSearchHotelHead(doc, fromDate, toDate, false);
        returnEl.addElement("getRooms").setText("true");
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

    /**
     * 根据酒店id获得房态
     * @param hotelId
     * @param fromDate
     * @param toDate
     * @return
     */
    public JSONObject getRoomsByHotelId(String hotelId, String rateBasis, String fromDate, String toDate) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("product").setText("hotel");
        Element request = customer.addElement("request");
        request.addAttribute("command", "getrooms");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("fromDate").setText(fromDate);
        bookingDetails.addElement("toDate").setText(toDate);
        bookingDetails.addElement("currency").setText("2524");
        Element rooms = bookingDetails.addElement("rooms").addAttribute("no", "1");
        Element room = rooms.addElement("room").addAttribute("runno", "0");
        room.addElement("adultsCode").setText("2");
        room.addElement("children").addAttribute("no", "0");
        room.addElement("rateBasis").setText(rateBasis);
        room.addElement("passengerNationality").setText("168");
        room.addElement("passengerCountryOfResidence").setText("168");
        bookingDetails.addElement("productId").setText(hotelId);
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    public JSONObject confirmBookingByOrder(BookingOrderInfo orderInfo, List<Passenger> plist) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("product").setText("hotel");
        Element request = customer.addElement("request");
        request.addAttribute("command", "confirmbooking");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("fromDate").setText(orderInfo.getFromDate());
        bookingDetails.addElement("toDate").setText(orderInfo.getToDate());
        bookingDetails.addElement("currency").setText(orderInfo.getCurrency());
        bookingDetails.addElement("productId").setText(orderInfo.getHotelId());
        Element rooms = bookingDetails.addElement("rooms").addAttribute("no", "1");
        Element room = rooms.addElement("room").addAttribute("runno", "0");
        room.addElement("roomTypeCode").setText(orderInfo.getRoomTypeCode());
        room.addElement("selectedRateBasis").setText(orderInfo.getSelectedRateBasis());
        room.addElement("allocationDetails").setText(orderInfo.getAllocationDetails());
        room.addElement("adultsCode").setText(orderInfo.getActualAdults());
        room.addElement("actualAdults").setText(orderInfo.getActualAdults());
        room.addElement("children").addAttribute("no", "0");
        room.addElement("actualChildren").addAttribute("no", "0");
        room.addElement("extraBed").setText("0");
        room.addElement("passengerNationality").setText("168");
        room.addElement("passengerCountryOfResidence").setText("168");
        Element passengersDetails = room.addElement("passengersDetails");
        for (int i = 0; i < plist.size(); i++) {
            Passenger passenger = plist.get(i);
            Element passengerEl = passengersDetails.addElement("passenger");
            if (i == 0) {
                passengerEl.addAttribute("leading", "yes");
            }
            passengerEl.addElement("salutation").setText(passenger.getSalutationCode());
            passengerEl.addElement("firstName").setText(passenger.getFirstName());
            passengerEl.addElement("lastName").setText(passenger.getLastName());
        }
        room.addElement("specialRequests").addAttribute("count", "0");
        room.addElement("beddingPreference").setText("0");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 去dotw发起预订单信息
     * @param roomBookingInfo
     * @param fromDate
     * @param toDate
     * @return
     */
    public JSONObject confirmBooking(RoomBookingInfo roomBookingInfo, List<Passenger> plist,
                                  String fromDate, String toDate) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("product").setText("hotel");
        Element request = customer.addElement("request");
        request.addAttribute("command", "confirmbooking");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("fromDate").setText(fromDate);
        bookingDetails.addElement("toDate").setText(toDate);
        bookingDetails.addElement("currency").setText("2524");
        bookingDetails.addElement("productId").setText(roomBookingInfo.getHotelId());
        Element rooms = bookingDetails.addElement("rooms").addAttribute("no", "1");
        Element room = rooms.addElement("room").addAttribute("runno", "0");
        room.addElement("roomTypeCode").setText(roomBookingInfo.getRoomTypeCode());
        room.addElement("selectedRateBasis").setText(roomBookingInfo.getRateBasisId());
        room.addElement("allocationDetails").setText(roomBookingInfo.getAllocationDetails());
        room.addElement("adultsCode").setText("2");
        room.addElement("actualAdults").setText("2");
        room.addElement("children").addAttribute("no", "0");
        room.addElement("actualChildren").addAttribute("no", "0");
        room.addElement("extraBed").setText("0");
        room.addElement("passengerNationality").setText("168");
        room.addElement("passengerCountryOfResidence").setText("168");
        Element passengersDetails = room.addElement("passengersDetails");
        for (int i = 0; i < plist.size(); i++) {
            Passenger passenger = plist.get(i);
            Element passengerEl = passengersDetails.addElement("passenger");
            if (i == 0) {
                passengerEl.addAttribute("leading", "yes");
            }
            passengerEl.addElement("salutation").setText(passenger.getSalutationCode());
            passengerEl.addElement("firstName").setText(passenger.getFirstName());
            passengerEl.addElement("lastName").setText(passenger.getLastName());
        }
        room.addElement("specialRequests").addAttribute("count", "0");
        room.addElement("beddingPreference").setText("0");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 获取dotw_ratebasis
     * @return
     */
    public JSONObject getRateBasis() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getratebasisids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 获取dotw_salutations_ids
     * @return
     */
    public JSONObject getSalutationsIds() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getsalutationsids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 去dotw取消订单，分两步，第一步confirm送no，第二步confirm送yes，才能完成取消
     * @param orderInfo
     * @param isConfirm
     * @return
     */
    public JSONObject cancelBooking(BookingOrderInfo orderInfo, String isConfirm) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        Element request = customer.addElement("request").addAttribute("command", "cancelbooking");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("bookingType").setText("1");
        bookingDetails.addElement("bookingCode").setText(orderInfo.getBookingCode());
        bookingDetails.addElement("confirm").setText(isConfirm);
        if (isConfirm.equals("yes")) {
            Element testPricesAndAllocation = bookingDetails.addElement("testPricesAndAllocation");
            Element service = testPricesAndAllocation.addElement("service").addAttribute("referencenumber", orderInfo.getBookingCode());
            service.addElement("penaltyApplied").setText(orderInfo.getPenaltyApplied());
        }
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 判断返回结果是否正确
     * @param result
     * @return
     */
    public boolean judgeResult(JSONObject result) {
        JSONObject request = result.getJSONObject("request");
        if (request != null) {
            String successful = request.getString("successful");
            if (StringUtils.isNotEmpty(successful) && "FALSE".equals(successful)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 搜索订单
     * @param passenger
     * @param city
     * @return
     */
    public JSONObject searchBooking(Passenger passenger, String city) {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("product").setText("hotel");
        Element request = customer.addElement("request").addAttribute("command", "searchbookings");
        Element bookingDetails = request.addElement("bookingDetails");
        bookingDetails.addElement("passengerFirstName").setText(passenger.getFirstName());
        bookingDetails.addElement("passengerLastName").setText(passenger.getLastName());
        bookingDetails.addElement("bookedCity").setText(city);
        bookingDetails.addElement("bookingType").setText("1");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    /**
     * 拉取leisureids列表
     * @return
     */
    public JSONObject getLeisureids() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getleisureids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    public JSONObject getBusinessIds() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getbusinessids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    public JSONObject getAmenitieIds() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getamenitieids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    public JSONObject getRoomAmenitieIds() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "getroomamenitieids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

    public JSONObject getHotelClassification() {
        Document doc = generateBaseRequest();
        Element customer = doc.getRootElement();
        customer.addElement("request").addAttribute("command", "gethotelclassificationids");
        String xmlResp = this.sendDotwString(doc);
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resutStr = xmlSerializer.read(xmlResp).toString();
        return JSON.parseObject(resutStr);
    }

}
