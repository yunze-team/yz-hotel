package com.yzly.api.util.meit.international;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.meit.dto.CreditCard;
import com.yzly.core.domain.meit.dto.GuestInfo;
import com.yzly.core.domain.meit.dto.OrderCreateParam;
import com.yzly.core.domain.meit.dto.OrderInfo;
import lombok.extern.apachecommons.CommonsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * meit请求参数封装工具类
 * @author lazyb
 * @create 2020/1/3
 * @desc
 **/
@CommonsLog
public class MeitReqUtil {

    /**
     * 根据美团json请求，封装订单创建参数
     * @param reqData
     * @return
     */
    public static OrderCreateParam buildOrderParam(JSONObject reqData) {
        OrderCreateParam orderCreateParam = new OrderCreateParam();
        orderCreateParam.setHotelId(reqData.getString("hotelId"));
        orderCreateParam.setRoomId(reqData.getString("roomId"));
        orderCreateParam.setRatePlanCode(reqData.getString("ratePlanCode"));
        orderCreateParam.setCheckin(reqData.getString("checkin"));
        orderCreateParam.setCheckout(reqData.getString("checkout"));
        orderCreateParam.setNumberOfAdults(reqData.getInteger("numberOfAdults"));
        orderCreateParam.setNumberOfChildren(reqData.getInteger("numberOfChildren"));
        orderCreateParam.setChildrenAges(reqData.getString("childrenAges"));
        orderCreateParam.setRoomNum(reqData.getInteger("roomNum"));
        orderCreateParam.setTotalPrice(reqData.get("totalPrice").toString());
        orderCreateParam.setCurrencyCode(reqData.getString("currencyCode"));
        JSONArray guestInfoArray = reqData.getJSONArray("guestInfo");
        List<GuestInfo> guestInfos = new ArrayList<>();
        for (int i = 0; i < guestInfoArray.size(); i++) {
            JSONObject infoObject = guestInfoArray.getJSONObject(i);
            GuestInfo guestInfo = new GuestInfo();
            guestInfo.setSeq(infoObject.getInteger("seq"));
            guestInfo.setRoomSeq(infoObject.getInteger("roomSeq"));
            guestInfo.setFirstName(infoObject.getString("firstName"));
            guestInfo.setLastName(infoObject.getString("lastName"));
            guestInfo.setGender(infoObject.getString("gender"));
            guestInfos.add(guestInfo);
        }
        orderCreateParam.setGuestInfo(guestInfos);
        JSONObject orderObject = reqData.getJSONObject("orderInfo");
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderObject.getLong("orderId"));
        orderInfo.setMobile(orderObject.getString("mobile"));
        orderInfo.setPersonName(orderObject.getString("personName"));
        orderCreateParam.setOrderInfo(orderInfo);
        CreditCard creditCard = new CreditCard();
        JSONObject creditObject = reqData.getJSONObject("creditCard");
        creditCard.setCreditCardNumber(creditObject.getString("creditCardNumber"));
        creditCard.setHolderName(creditObject.getString("holderName"));
        creditCard.setExpire(creditObject.getString("expire"));
        creditCard.setCreditCardIdentifier(creditObject.getString("creditCardIdentifier"));
        creditCard.setValidFrom(creditObject.getString("validFrom"));
        creditCard.setValidTo(creditObject.getString("validTo"));
        creditCard.setTimeZone(creditObject.getString("timeZone"));
        orderCreateParam.setCreditCard(creditCard);
        return orderCreateParam;
    }

}
