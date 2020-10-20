package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.jl.JLHotelInfo;
import com.yzly.core.service.EventAttrService;
import com.yzly.core.service.jl.JLAdminService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private EventAttrService eventAttrService;
    @Autowired
    private JLAdminService jlAdminService;

    private final static String PRICE_RATE = "CTRIP_ROOM_PRICE_RATE";

    /**
     * 按照携程的请求xml封装创建捷旅订单的请求
     * @param otaRequest
     * @return
     * @throws Exception
     */
    public JSONObject genJLCreateOrderByCtripXml(Element otaRequest) throws Exception {
        JSONObject req = new JSONObject();
        Element uniqueIDEl = otaRequest.element("UniqueID");
        String uniqueId = uniqueIDEl.attributeValue("ID");
        String uniqueIdType = uniqueIDEl.attributeValue("Type");
        Element hotelReservationRequest = otaRequest.element("HotelReservations").element("HotelReservation");
        Element roomStays = hotelReservationRequest.element("RoomStays");
        List<Element> roomStayList = roomStays.elements("RoomStay");
        JSONArray roomGroups = new JSONArray();
        for (Element roomStay : roomStayList) {
            JSONObject roomGroup = new JSONObject();
            Element roomRates = roomStay.element("RoomRates");
            List<Element> roomRateList = roomRates.elements("RoomRate");
            for (Element roomRate : roomRateList) {
                String ratePlanCode = roomRate.attributeValue("RatePlanCode");
                req.put("keyId", ratePlanCode);
                Integer numberOfUnits = Integer.valueOf(roomRate.attributeValue("NumberOfUnits"));
                Element rates = roomRate.element("Rates");
                List<Element> rateList = rates.elements("Rate");
                for (Element rate : rateList) {
                    String effectiveDate = rate.attributeValue("EffectiveDate");
                    String expireDate = rate.attributeValue("ExpireDate");
                    Element base = rate.element("Base");
                    String amountAfterTax = base.attributeValue("AmountAfterTax");
                    String currencyCode = base.attributeValue("CurrencyCode");
                    req.put("checkInDate", effectiveDate);
                    req.put("checkOutDate", expireDate);
                    req.put("totalPrice", Double.valueOf(amountAfterTax));
                }
            }
            String hotelCode = roomStay.element("BasicPropertyInfo").attributeValue("HotelCode");
            req.put("hotelId", hotelCode);
        }
        return req;
    }

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
    public String genCtripXmlByJLRespOnQueryRate(JSONObject resp, String requestName, JSONObject req) {
        Document doc = generateBaseResponse(requestName);
        Element otaResponse = doc.getRootElement().element("Body").element(requestName);
        otaResponse.addElement("Success");
        JSONArray hotelRatePlanList = resp.getJSONArray("hotelRatePlanList");
        if (hotelRatePlanList.size() == 0) {
            return doc.asXML();
        }
        Element roomStays = otaResponse.addElement("RoomStays");
        for (int i = 0; i < hotelRatePlanList.size(); i++) {
            JSONObject hotelRatePlan = hotelRatePlanList.getJSONObject(i);
            JSONArray rooms = hotelRatePlan.getJSONArray("rooms");
            for (int j = 0; j < rooms.size(); j++) {
                JSONObject room = rooms.getJSONObject(j);
                Element roomStay = roomStays.addElement("RoomStay");
                Element roomType = roomStay.addElement("RoomTypes").addElement("RoomType");
                roomType.addAttribute("RoomTypeCode", room.getString("roomTypeId"));
                JSONArray ratePlans = room.getJSONArray("ratePlans");
                Element ratePlansEl = roomStay.addElement("RatePlans");
                Element roomRates = roomStay.addElement("RoomRates");
                for (int k = 0; k < ratePlans.size(); k++) {
                    Element ratePlanEl = ratePlansEl.addElement("RatePlan");
                    JSONObject ratePlan = ratePlans.getJSONObject(k);
                    ratePlanEl.addAttribute("RatePlanCode", ratePlan.getString("keyId"));
                    // 取消惩罚细则，具体待确认
                    Element cancelPenalites = ratePlanEl.addElement("CancelPenalties");
                    Element cancelPenalty = cancelPenalites.addElement("CancelPenalty");
                    cancelPenalty.addElement("Deadline");
                    cancelPenalty.addElement("AmountPercent").addAttribute("NmbrOfNights", "1");
                    cancelPenalty.addElement("PenaltyDescription");
                    // 餐食参数
                    Integer breakfast = ratePlan.getInteger("breakfast");
                    Element mealsIncluded = ratePlanEl.addElement("MealsIncluded");
                    if (breakfast == 0) {
                        mealsIncluded.addAttribute("Breakfast", "False");
                    } else {
                        mealsIncluded.addAttribute("Breakfast", "True");
                    }
                    mealsIncluded.addAttribute("Lunch", "False").
                            addAttribute("Dinner", "False").addAttribute("NumberOfMeal", breakfast.toString());
                    // 封装价格参数
                    Element roomRate = roomRates.addElement("RoomRate").
                            addAttribute("RoomTypeCode", room.getString("roomTypeId")).
                            addAttribute("RatePlanCode", ratePlan.getString("keyId")).
                            addAttribute("RatePlanCategory", "501");
                    Element rates = roomRate.addElement("Rates");
                    JSONArray nightlyRates = ratePlan.getJSONArray("nightlyRates");
                    BigDecimal totalPrice = new BigDecimal(0);
                    for (int l = 0; l < nightlyRates.size(); l++) {
                        JSONObject nightlyRate = nightlyRates.getJSONObject(l);
                        Element rate = rates.addElement("Rate");
                        String date = nightlyRate.getString("date");
                        rate.addAttribute("EffectiveDate", date);
                        String nextDate = DateTime.parse(date).plusDays(1).toString("yyyy-MM-dd");
                        rate.addAttribute("ExpireDate", nextDate);
                        // 价格加上上调浮动利率
                        Double priceRate = Double.valueOf(eventAttrService.findByType(PRICE_RATE).getEventValue());
                        BigDecimal basePrice = new BigDecimal(nightlyRate.getDouble("cose"));
                        BigDecimal finalPrice = basePrice.multiply(new BigDecimal(1 + priceRate)).setScale(2, RoundingMode.HALF_UP);
                        totalPrice = totalPrice.add(finalPrice);
                        Element base = rate.addElement("Base").addAttribute("AmountBeforeTax", finalPrice.toString()).
                                addAttribute("AmountAfterTax", finalPrice.toString()).addAttribute("CurrencyCode", "CNY");
                        Element taxes = base.addElement("Taxes").addAttribute("CurrencyCode", "CNY");
                        // 税费节点
                        taxes.addElement("Tax").addAttribute("Type", "Inclusive").
                                addAttribute("Code", "3").addAttribute("ChargeUnit", "1").
                                addAttribute("Amount", "0");
                    }
                    // 添加总价格节点
                    roomRate.addElement("Total").addAttribute("AmountBeforeTax", totalPrice.toString()).
                            addAttribute("AmountAfterTax", totalPrice.toString()).addAttribute("CurrencyCode", "CNY");
                }
                Element guestCounts = roomStay.addElement("GuestCounts");
                // 读取req中的用户数据，添加用户节点
                JSONArray roomGroups = req.getJSONArray("roomGroups");
                for (int m = 0; m < roomGroups.size(); m++) {
                    JSONObject roomGroup = roomGroups.getJSONObject(m);
                    Integer adults = roomGroup.getInteger("adults");
                    Integer children = roomGroup.getInteger("children");
                    guestCounts.addElement("GuestCount").addAttribute("AgeQualifyingCode", "10").addAttribute("Count", adults.toString());
                    if (children != null) {
                        guestCounts.addElement("GuestCount").addAttribute("AgeQualifyingCode", "8").addAttribute("Count", children.toString());
                    }
                }
            }
        }
        Element hotelStays = otaResponse.addElement("HotelStays");
        Element hotelStay = hotelStays.addElement("HotelStay");
        Integer hotelId = req.getInteger("hotelId");
        JLHotelInfo jlHotelInfo = jlAdminService.findInfoByHotelId(hotelId);
        hotelStay.addElement("BasicPropertyInfo").
                addAttribute("HotelCode", jlHotelInfo.getHotelId().toString()).
                addAttribute("HotelName", jlHotelInfo.getHotelNameEn());
        return doc.asXML();
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
