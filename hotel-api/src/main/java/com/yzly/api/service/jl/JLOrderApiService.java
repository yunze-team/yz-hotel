package com.yzly.api.service.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.JLHandler;
import com.yzly.core.domain.jl.JLOrderInfo;
import com.yzly.core.domain.jl.JLOrderRoomInfo;
import com.yzly.core.service.jl.JLOrderService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/6/9
 * @desc
 **/
@Service
@CommonsLog
public class JLOrderApiService {

    @Autowired
    private JLHandler jlHandler;
    @Autowired
    private JLOrderService jlOrderService;

    /**
     * 生成预订单，并去捷旅报价查询
     * @param orderReq
     * @return
     */
    public Object preOrder(JSONObject orderReq) {
        JLOrderInfo jlOrderInfo = jlOrderService.saveOrderByPreJson(orderReq);
        JSONArray roomArray = jlOrderService.getPreOrderRoomInfo(jlOrderInfo.getId());
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.queryOrderPrice(jlOrderInfo, roomArray)).getJSONObject("result");
            return jlOrderService.finishPreOrderByJson(reJson, jlOrderInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 完成到捷旅的下单，并根据返回更新本地订单状态
     * @param customerOrderCode
     * @return
     */
    public Object finishOrder(String customerOrderCode) {
        JLOrderInfo jlOrderInfo = jlOrderService.getOrderByCode(customerOrderCode);
        List<JLOrderRoomInfo> orderRoomInfos = jlOrderService.getAllRoomByOrder(customerOrderCode);
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.createOrder(jlOrderInfo, orderRoomInfos)).getJSONObject("result");
            return jlOrderService.finishOrderByJson(reJson, jlOrderInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 完成到捷旅的取消订单
     * @param customerOrderCode
     * @return
     */
    public Object cancelOrder(String customerOrderCode) {
        JLOrderInfo jlOrderInfo = jlOrderService.getOrderByCode(customerOrderCode);
        try {
            JSONObject reJson = JSONObject.parseObject(jlHandler.cancelOrder(jlOrderInfo.getOrderCode(),
                    jlOrderInfo.getCustomerOrderCode())).getJSONObject("result");
            return jlOrderService.cancelOrderByJson(reJson, jlOrderInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 查询捷旅订单
     * @param customerOrderCode
     * @return
     */
    public Object queryOrder(String customerOrderCode) {
        JLOrderInfo jlOrderInfo = jlOrderService.getOrderByCode(customerOrderCode);
        JSONObject reJson = JSONObject.parseObject(jlHandler.queryOrder(jlOrderInfo.getOrderCode(),
                jlOrderInfo.getCustomerOrderCode(), null, null)).getJSONObject("result");
        return reJson;
    }

}
