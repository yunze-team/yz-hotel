package com.yzly.api.service.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.JLHandler;
import com.yzly.core.domain.jl.JLOrderInfo;
import com.yzly.core.service.jl.JLOrderService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
