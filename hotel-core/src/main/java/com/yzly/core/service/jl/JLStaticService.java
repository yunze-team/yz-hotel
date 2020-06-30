package com.yzly.core.service.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.jl.*;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.repository.jl.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 捷旅api-静态信息同步服务
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Service
@CommonsLog
public class JLStaticService {

    @Autowired
    private JLCityRepository jlCityRepository;
    @Autowired
    private EventAttrRepository eventAttrRepository;
    @Autowired
    private JLHotelInfoRepository jlHotelInfoRepository;
    @Autowired
    private JLHotelDetailRepository jlHotelDetailRepository;
    @Autowired
    private JLRatePlanRepository jlRatePlanRepository;
    @Autowired
    private JLNightlyRateRepository jlNightlyRateRepository;
    @Autowired
    private JLNightlyRateCacheRepository jlNightlyRateCacheRepository;
    @Autowired
    private JLBookingRuleRepository jlBookingRuleRepository;
    @Autowired
    private JLRefundRuleRepository jlRefundRuleRepository;

    private static final String JL_CITY_PAGE = "JL_CITY_PAGE";
    private static final String JL_HOTEL_PAGE = "JL_HOTEL_PAGE";

    /**
     * 按照返回的json存入city
     * @param reJson
     * @throws Exception
     */
    public void syncCityByJson(JSONObject reJson) throws Exception {
        JSONArray cityArray = reJson.getJSONArray("hotelGeoList");
        if (cityArray.size() == 0) {
            throw new Exception("city array is empty");
        }
        for (int i = 0; i < cityArray.size(); i++) {
            JSONObject cityJson = cityArray.getJSONObject(i);
            JLCity jlCity = new JLCity(cityJson.getInteger("countryId"), cityJson.getString("countryCn"), cityJson.getString("countryEn"),
                    cityJson.getInteger("stateId"), cityJson.getString("stateCn"), cityJson.getString("stateEn"),
                    cityJson.getInteger("cityId"), cityJson.getString("cityCn"), cityJson.getString("cityEn"));
            JLCity city = jlCityRepository.findByCityId(jlCity.getCityId());
            if (city != null) {
                continue;
            }
            jlCityRepository.save(jlCity);
        }
        EventAttr attr = eventAttrRepository.findByEventType(JL_CITY_PAGE);
        attr.setEventValue(String.valueOf(Integer.valueOf(attr.getEventValue()) + 1));
        eventAttrRepository.save(attr);
    }

    /**
     * 根据返回的json存入hotel
     * @param reJson
     * @throws Exception
     */
    public void syncHotelByJson(JSONObject reJson) throws Exception {
        JSONArray hotelArray = reJson.getJSONArray("hotels");
        if (hotelArray.size() == 0) {
            throw new Exception("hotel array is empty");
        }
        for (int i = 0; i < hotelArray.size(); i++) {
            JSONObject hotelJson = hotelArray.getJSONObject(i);
            JLHotelInfo jlHotelInfo = new JLHotelInfo(hotelJson.getInteger("hotelId"), hotelJson.getInteger("countryId"), hotelJson.getInteger("stateId"),
                    hotelJson.getInteger("cityId"), hotelJson.getInteger("star"), hotelJson.getString("hotelNameCn"), hotelJson.getString("hotelNameEn"),
                    hotelJson.getString("addressCn"), hotelJson.getString("addressEn"), hotelJson.getString("phone"), hotelJson.getString("longitude"),
                    hotelJson.getString("latitude"), hotelJson.getInteger("instantConfirmation"), hotelJson.getInteger("sellType"), hotelJson.getString("updateTime"));
            JLHotelInfo hotelInfo = jlHotelInfoRepository.findByHotelId(jlHotelInfo.getHotelId());
            if (hotelInfo != null) {
                continue;
            }
            jlHotelInfoRepository.save(jlHotelInfo);
        }
        EventAttr attr = eventAttrRepository.findByEventType(JL_HOTEL_PAGE);
        attr.setEventValue(String.valueOf(Integer.valueOf(attr.getEventValue()) + 1));
        eventAttrRepository.save(attr);
    }

    /**
     * 将酒店详细数据存入
     * @param reJson
     * @throws Exception
     */
    public JLHotelDetail syncHotelDetailByJson(JSONObject reJson) throws Exception {
        JLHotelDetail jlHotelDetail = new JLHotelDetail();
        JSONArray hotelDetailArray = reJson.getJSONArray("hotelDetailList");
        if (hotelDetailArray.size() == 0) {
            throw new Exception("hotel detail array is empty");
        }
        for (int i = 0; i < hotelDetailArray.size(); i++) {
            JSONObject hotelDetailJson = hotelDetailArray.getJSONObject(i);
            Integer hotelId = hotelDetailJson.getInteger("hotelId");
            jlHotelDetail = jlHotelDetailRepository.findByHotelId(hotelId);
            if (jlHotelDetail == null) {
                jlHotelDetail = new JLHotelDetail();
            }
            jlHotelDetail.setHotelId(hotelDetailJson.getInteger("hotelId"));
            jlHotelDetail.setHotelInfo(hotelDetailJson.getJSONObject("hotelInfo"));
            jlHotelDetail.setRoomTypeList(hotelDetailJson.getJSONArray("roomTypeList"));
            jlHotelDetail.setRateTypeList(hotelDetailJson.getJSONArray("rateTypeList"));
            jlHotelDetail.setImageList(hotelDetailJson.getJSONArray("imageList"));
            jlHotelDetailRepository.save(jlHotelDetail);
        }
        return jlHotelDetail;
    }

    /**
     * 根据返回的酒店价格json更新本地价格数据
     * @param reJson
     * @return
     * @throws Exception
     */
    public Object syncHotelPriceByJson(JSONObject reJson) throws Exception {
        JSONArray ratePlanListArray = reJson.getJSONArray("hotelRatePlanList");
        if (ratePlanListArray.size() == 0) {
            throw new Exception("hotel price array is empty");
        }
        for (int i = 0; i < ratePlanListArray.size(); i++) {
            JSONObject ratePlanList = ratePlanListArray.getJSONObject(i);
            Integer hotelId = ratePlanList.getInteger("hotelId");
            JSONArray roomsArray = ratePlanList.getJSONArray("rooms");
            for (int j = 0; j < roomsArray.size(); j++) {
                JSONObject rooms = roomsArray.getJSONObject(j);
                String roomTypeId = rooms.getString("roomTypeId");
                JSONArray ratePlansArray = rooms.getJSONArray("ratePlans");
                // 保存每晚价格
                for (int k = 0; k < ratePlansArray.size(); k++) {
                    JSONObject ratePlans = ratePlansArray.getJSONObject(k);
                    JLRatePlan jlRatePlan = buildRatePlanByJSON(ratePlans, hotelId, roomTypeId);
                    JSONArray nightlyRatesArray = ratePlans.getJSONArray("nightlyRates");
                    for (int l = 0; l < nightlyRatesArray.size(); l++) {
                        JSONObject nightlyRates = nightlyRatesArray.getJSONObject(l);
                        buildNightlyRateCacheByJSON(nightlyRates, jlRatePlan);
                    }
                }
            }
            // 保存预定规则
            JSONArray bookingRulesArray = ratePlanList.getJSONArray("bookingRules");
            for (int j = 0; j < bookingRulesArray.size(); j++) {
                JSONObject bookingRules = bookingRulesArray.getJSONObject(j);
                buildBookingRuleByJSON(bookingRules);
            }
            // 保存退订规则
            JSONArray refundRulesArray = ratePlanList.getJSONArray("refundRules");
            for (int j = 0; j < refundRulesArray.size(); j++) {
                JSONObject refundRules = refundRulesArray.getJSONObject(j);
                buildRefundRuleByJSON(refundRules);
            }
        }
        return ratePlanListArray;
    }

    /**
     * 按照json串内容封装保存refundrule实体
     * @param refundRules
     * @return
     */
    public JLRefundRule buildRefundRuleByJSON(JSONObject refundRules) {
        String refundRuleId = refundRules.getString("refundRuleId");
        JLRefundRule jlRefundRule = jlRefundRuleRepository.findOneByRefundRuleId(refundRuleId);
        if (jlRefundRule == null) {
            jlRefundRule = new JLRefundRule();
            jlRefundRule.setRefundRuleId(refundRuleId);
        }
        jlRefundRule.setRefundRuleType(refundRules.getInteger("refundRuleType"));
        jlRefundRule.setRefundRuleHours(refundRules.getInteger("refundRuleHours"));
        jlRefundRule.setDeductType(refundRules.getInteger("deductType"));
        return jlRefundRuleRepository.save(jlRefundRule);
    }

    /**
     * 按照json串内容封装保存bookingrule实体
     * @param bookingRules
     * @return
     */
    public JLBookingRule buildBookingRuleByJSON(JSONObject bookingRules) {
        String bookingRuleId = bookingRules.getString("bookingRuleId");
        JLBookingRule jlBookingRule = jlBookingRuleRepository.findOneByBookingRuleId(bookingRuleId);
        if (jlBookingRule == null) {
            jlBookingRule = new JLBookingRule();
            jlBookingRule.setBookingRuleId(bookingRuleId);
        }
        jlBookingRule.setStartDate(bookingRules.getString("startDate"));
        jlBookingRule.setEndDate(bookingRules.getString("endDate"));
        jlBookingRule.setMinAmount(bookingRules.getInteger("minAmount"));
        jlBookingRule.setMaxAmount(bookingRules.getInteger("maxAmount"));
        jlBookingRule.setMinDays(bookingRules.getInteger("minDays"));
        jlBookingRule.setMaxDays(bookingRules.getInteger("maxDays"));
        jlBookingRule.setMinAdvHours(bookingRules.getInteger("minAdvHours"));
        jlBookingRule.setMaxAdvHours(bookingRules.getInteger("maxAdvHours"));
        jlBookingRule.setWeekSet(bookingRules.getString("weekSet"));
        jlBookingRule.setStartTime(bookingRules.getString("startTime"));
        jlBookingRule.setEndTime(bookingRules.getString("endTime"));
        jlBookingRule.setBookingNotices(bookingRules.getString("bookingNotices"));
        return jlBookingRuleRepository.save(jlBookingRule);
    }

    /**
     * 按照json串内容封装保存rateplan实体
     * @param ratePlans
     * @param hotelId
     * @param roomTypeId
     * @return
     */
    public JLRatePlan buildRatePlanByJSON(JSONObject ratePlans, Integer hotelId, String roomTypeId) {
        String keyId = ratePlans.getString("keyId");
        JLRatePlan jlRatePlan = jlRatePlanRepository.findOneByKeyId(keyId);
        if (jlRatePlan == null) {
            jlRatePlan = new JLRatePlan();
            jlRatePlan.setKeyId(keyId);
        }
        jlRatePlan.setHotelId(hotelId);
        jlRatePlan.setRoomTypeId(roomTypeId);
        jlRatePlan.setKeyName(ratePlans.getString("keyName"));
        jlRatePlan.setSupplierId(ratePlans.getInteger("supplierId"));
        jlRatePlan.setBedName(ratePlans.getString("bedName"));
        jlRatePlan.setMaxOccupancy(ratePlans.getInteger("maxOccupancy"));
        jlRatePlan.setCurrency(ratePlans.getString("currency"));
        jlRatePlan.setRateTypeId(ratePlans.getString("rateTypeId"));
        jlRatePlan.setPaymentType(ratePlans.getInteger("paymentType"));
        jlRatePlan.setBreakfast(ratePlans.getInteger("breakfast"));
        jlRatePlan.setBookingRuleId(ratePlans.getString("bookingRuleId"));
        jlRatePlan.setRefundRuleId(ratePlans.getString("refundRuleId"));
        jlRatePlan.setMarket(ratePlans.getString("market"));
        jlRatePlan.setIfInvoice(ratePlans.getInteger("ifInvoice"));
        jlRatePlan.setPromotions(ratePlans.get("promotions") != null ? ratePlans.get("promotions").toString() : "");
        jlRatePlan = jlRatePlanRepository.save(jlRatePlan);
        return jlRatePlan;
    }

    /**
     * 按照json串内容封装保存nightlyRate实体
     * @param nightlyRates
     * @param jlRatePlan
     */
    public void buildNightlyRateByJOSN(JSONObject nightlyRates, JLRatePlan jlRatePlan) {
        String date = nightlyRates.getString("date");
        List<JLNightlyRate> rates = jlNightlyRateRepository.findAllByRatePlanKeyIdAndDate(jlRatePlan.getKeyId(), date);
        JLNightlyRate rate = new JLNightlyRate();
        if (rates.size() != 0) {
            rate = rates.get(0);
        }
        rate.setFormulaTypen(nightlyRates.getString("formulaType"));
        rate.setDate(nightlyRates.getString("date"));
        rate.setCose(nightlyRates.getDouble("cose"));
        rate.setStatus(nightlyRates.getInteger("status"));
        rate.setCurrentAlloment(nightlyRates.getInteger("currentAlloment"));
        rate.setBreakfast(jlRatePlan.getBreakfast());
        rate.setBookingRuleId(jlRatePlan.getBookingRuleId());
        rate.setRefundRuleId(jlRatePlan.getRefundRuleId());
        rate.setHotelId(jlRatePlan.getHotelId());
        rate.setRatePlanKeyId(jlRatePlan.getKeyId());
        rate.setRoomTypeId(jlRatePlan.getRoomTypeId());
        jlNightlyRateRepository.save(rate);
    }

    /**
     * 按照json串内容保存nightlyRateCache实体
     * @param nightlyRates
     * @param jlRatePlan
     */
    public void buildNightlyRateCacheByJSON(JSONObject nightlyRates, JLRatePlan jlRatePlan) {
        String date = nightlyRates.getString("date");
        List<JLNightlyRateCache> rates = jlNightlyRateCacheRepository.findAllByRatePlanKeyIdAndDate(jlRatePlan.getKeyId(), date);
        JLNightlyRateCache rate = new JLNightlyRateCache();
        if (rates.size() != 0) {
            rate = rates.get(0);
        }
        rate.setFormulaTypen(nightlyRates.getString("formulaType"));
        rate.setDate(nightlyRates.getString("date"));
        rate.setCose(nightlyRates.getDouble("cose"));
        rate.setStatus(nightlyRates.getInteger("status"));
        rate.setCurrentAlloment(nightlyRates.getInteger("currentAlloment"));
        rate.setBreakfast(jlRatePlan.getBreakfast());
        rate.setBookingRuleId(jlRatePlan.getBookingRuleId());
        rate.setRefundRuleId(jlRatePlan.getRefundRuleId());
        rate.setHotelId(jlRatePlan.getHotelId());
        rate.setRatePlanKeyId(jlRatePlan.getKeyId());
        rate.setRoomTypeId(jlRatePlan.getRoomTypeId());
        jlNightlyRateCacheRepository.save(rate);
    }

}
