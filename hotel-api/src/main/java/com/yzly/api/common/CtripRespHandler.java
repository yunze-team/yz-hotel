package com.yzly.api.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.ctrip.CtripOrderInfo;
import com.yzly.core.domain.jl.JLHotelInfo;
import com.yzly.core.service.EventAttrService;
import com.yzly.core.service.ctrip.CtripOrderService;
import com.yzly.core.service.jl.JLAdminService;
import com.yzly.core.util.SnowflakeIdWorker;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private CtripOrderService ctripOrderService;

    private final static String PRICE_RATE = "CTRIP_ROOM_PRICE_RATE";

    @Value("${snowflake.workId}")
    private long workId;
    @Value("${snowflake.datacenterId}")
    private long datacenterId;

    /**
     * 接收携程的查询订单请求，查询订单状态，并返回携程xml报文
     * @param otaRequest
     * @param requestName
     * @param echoToken
     * @return
     * @throws Exception
     */
    public String genReadOrderXmlResp(Element otaRequest, String requestName, String echoToken) throws Exception {
        Element readRequests = otaRequest.element("ReadRequests");
        List<Element> readRequestList = readRequests.elements("ReadRequest");
        List<CtripOrderInfo> clist = new ArrayList<>();
        for (Element readRequest : readRequestList) {
            List<Element> uniqueIDs = readRequest.elements("UniqueID");
            Map<String, String> uniqueIDMap = new HashMap<>();
            for (Element uniqueID : uniqueIDs) {
                uniqueIDMap.put(uniqueID.attributeValue("Type"), uniqueID.attributeValue("ID"));
            }
            clist.add(ctripOrderService.findOneOrderByNumber(uniqueIDMap.get("501"), uniqueIDMap.get("502")));
        }
        Document doc = this.generateTokenResponse(requestName, echoToken);
        Element otaResponse = doc.getRootElement().element("Body").element(requestName);
        otaResponse.addElement("Success");
        Element hotelReservations = otaResponse.addElement("HotelReservations");
        for (CtripOrderInfo ctripOrderInfo : clist) {
            Element hotelReservation = hotelReservations.addElement("HotelReservation").
                    addAttribute("ResStatus", ctripOrderInfo.getStatus());
            Element hotelReservationIDs = hotelReservation.addElement("ResGlobalInfo").addElement("HotelReservationIDs");
            hotelReservationIDs.addElement("HotelReservationID").
                    addAttribute("ResID_Type", "502").
                    addAttribute("ResID_Value", ctripOrderInfo.getHotelConfirmNumber());
            hotelReservationIDs.addElement("HotelReservationID").
                    addAttribute("ResID_Type", "501").
                    addAttribute("ResID_Value", ctripOrderInfo.getCtripUniqueId());
        }
        return doc.asXML();
    }

    /**
     * 接收携程的取消订单请求，完成订单取消操作，并返回携程成功的xml报文
     * @param otaRequest
     * @return
     * @throws Exception
     */
    public String genCancelXmlResp(Element otaRequest, String requestName, String echoToken) throws Exception {
        List<Element> uniqueIds = otaRequest.elements("UniqueID");
        Map<String, String> uniqueIdMap = new HashMap<>();
        for (Element uniqueId : uniqueIds) {
            uniqueIdMap.put(uniqueId.attributeValue("Type"), uniqueId.attributeValue("ID"));
        }
        // 取消订单
        CtripOrderInfo ctripOrderInfo = ctripOrderService.cancelOrder(uniqueIdMap.get("502"), uniqueIdMap.get("501"));
        Document doc = this.generateTokenResponse(requestName, echoToken);
        Element otaResponse = doc.getRootElement().element("Body").element(requestName);
        otaResponse.addElement("Success");
        otaResponse.addElement("UniqueID").
                addAttribute("ID", ctripOrderInfo.getCtripUniqueId()).addAttribute("Type", "501");
        otaResponse.addElement("UniqueID").
                addAttribute("ID", ctripOrderInfo.getHotelConfirmNumber()).addAttribute("Type", "502");
        return doc.asXML();
    }

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
     * 生产携程订单
     * @param otaRequest
     * @return
     * @throws Exception
     */
    public CtripOrderInfo createCtripOrderByXml(Element otaRequest) throws Exception {
        Element uniqueIDEl = otaRequest.element("UniqueID");
        String uniqueId = uniqueIDEl.attributeValue("ID");
        CtripOrderInfo ctripOrderInfo = ctripOrderService.findOneCtripOrder(uniqueId);
        if (ctripOrderInfo == null) {
            ctripOrderInfo = new CtripOrderInfo();
        }
        ctripOrderInfo = this.genCtripOrderByXml(otaRequest, ctripOrderInfo);
        Long logId = new SnowflakeIdWorker(workId, datacenterId).nextId();
        return ctripOrderService.createOrder(ctripOrderInfo, String.valueOf(logId));
    }

    /**
     * 根据携程订单，返回携程xml报文
     * @param ctripOrderInfo
     * @param echoToken
     * @param requestName
     * @return
     */
    public String genRespXmlByCreateOrder(CtripOrderInfo ctripOrderInfo, String echoToken, String requestName) {
        Document doc = this.generateTokenResponse(requestName, echoToken);
        Element otaResponse = doc.getRootElement().element("Body").element(requestName);
        otaResponse.addElement("Success");
        Element hotelReservations = otaResponse.addElement("HotelReservations");
        hotelReservations.addElement("HotelReservation").addAttribute("ResStatus", "S");
        Element hotelReservationIDs = hotelReservations.addElement("ResGlobalInfo").addElement("HotelReservationIDs");
        hotelReservationIDs.addElement("HotelReservationID").
                addAttribute("ResID_Value", ctripOrderInfo.getHotelConfirmNumber()).addAttribute("ResID_Type", "502");
        hotelReservationIDs.addElement("HotelReservationID").
                addAttribute("ResID_Value", ctripOrderInfo.getCtripUniqueId()).addAttribute("ResID_Type", "501");
        return doc.asXML();
    }

    /**
     * 根据携程请求xml封装携程订单信息实体
     * @param otaRequest
     * @return
     */
    public CtripOrderInfo genCtripOrderByXml(Element otaRequest, CtripOrderInfo ctripOrderInfo) {
        Element uniqueIDEl = otaRequest.element("UniqueID");
        String uniqueId = uniqueIDEl.attributeValue("ID");
        String uniqueIdType = uniqueIDEl.attributeValue("Type");
        ctripOrderInfo.setCtripUniqueId(uniqueId);
        ctripOrderInfo.setCtripUniqueType(uniqueIdType);
        Element hotelReservationRequest = otaRequest.element("HotelReservations").element("HotelReservation");
        Element roomStays = hotelReservationRequest.element("RoomStays");
        Element roomStay = roomStays.element("RoomStay");
        String roomTypeCode = roomStay.element("RoomTypes").element("RoomType").attributeValue("RoomTypeCode");
        ctripOrderInfo.setRoomTypeCode(roomTypeCode);
        Element roomRate = roomStay.element("RoomRates").element("RoomRate");
        String ratePlanCode = roomRate.attributeValue("RatePlanCode");
        ctripOrderInfo.setRatePlanCode(ratePlanCode);
        Integer numberOfUnits = Integer.valueOf(roomRate.attributeValue("NumberOfUnits"));
        ctripOrderInfo.setNumberOfUnits(numberOfUnits);
        Element rate = roomRate.element("Rates").element("Rate");
        String effectiveDate = rate.attributeValue("EffectiveDate");
        String expireDate = rate.attributeValue("ExpireDate");
        ctripOrderInfo.setEffectiveDate(effectiveDate);
        ctripOrderInfo.setExpireDate(expireDate);
        Element base = rate.element("Base");
        Double amountAfterTax = Double.valueOf(base.attributeValue("AmountAfterTax"));
        String currencyCode = base.attributeValue("CurrencyCode");
        ctripOrderInfo.setAmountAfterTax(amountAfterTax);
        ctripOrderInfo.setCurrencyCode(currencyCode);
        String hotelCode = roomStay.element("BasicPropertyInfo").attributeValue("HotelCode");
        ctripOrderInfo.setHotelCode(hotelCode);
        Element resGuests = hotelReservationRequest.element("ResGuests").element("ResGuest");
        XMLSerializer xmlSerializer = new XMLSerializer();
        Element customer = resGuests.element("Profiles").element("ProfileInfo").element("Profile").element("Customer");
        String profiles = xmlSerializer.read(customer.asXML()).toString();
        ctripOrderInfo.setPersonInfos(profiles);
        String contactPersion = xmlSerializer.read(customer.element("ContactPerson").asXML()).toString();
        ctripOrderInfo.setContactPerson(contactPersion);
        Element resGloballInfo = hotelReservationRequest.element("ResGlobalInfo");
        String guestCounts = xmlSerializer.read(resGloballInfo.element("GuestCounts").asXML()).toString();
        ctripOrderInfo.setGuestCounts(guestCounts);
        Element timeSpan = resGloballInfo.element("TimeSpan");
        String timeSpanEnd = timeSpan.attributeValue("End");
        String timeSpanStart = timeSpan.attributeValue("Start");
        ctripOrderInfo.setTimeSpanEnd(timeSpanEnd);
        ctripOrderInfo.setTimeSpanStart(timeSpanStart);
        Element total = resGloballInfo.element("Total");
        Double totalAmountAfterTax = Double.valueOf(total.attributeValue("AmountAfterTax"));
        String totalCurrencyCode = total.attributeValue("CurrencyCode");
        ctripOrderInfo.setTotalAmountAfterTax(totalAmountAfterTax);
        ctripOrderInfo.setTotalCurrencyCode(totalCurrencyCode);
        String hotelReservationIDs = xmlSerializer.read(resGloballInfo.element("HotelReservationIDs").asXML()).toString();
        ctripOrderInfo.setHotelReservitaionIDs(hotelReservationIDs);
        return ctripOrderInfo;
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

    /**
     * 创建带token的请求返回头
     * @param requestName
     * @param echoToken
     * @return
     */
    public Document generateTokenResponse(String requestName, String echoToken) {
        Document doc = DocumentHelper.createDocument();
        Element soapRoot = doc.addElement("soap:Envelope");
        soapRoot.addNamespace("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
        soapRoot.addElement("soap:Header");
        Element soapBody = soapRoot.addElement("soap:Body");
        Element otaRequest = soapBody.addElement(requestName,
                "http://www.opentravel.org/OTA/2003/05");
        otaRequest.addAttribute("Version", "2.2").addAttribute("EchoToken", echoToken);
        return doc;
    }

}
