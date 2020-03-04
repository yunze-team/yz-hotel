package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.service.dotw.MeitApiService;
import com.yzly.api.util.meit.MeitResultUtil;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.meit.ResultEnum;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 给后管远程调用
 * @author lazyb
 * @create 2020/1/4
 * @desc
 **/
@RestController
@RequestMapping("/api/admin")
@CommonsLog
public class AdminController {

    @Autowired
    private MeitApiService meitApiService;

    private MeitResult baseRequestTrans(String json) {
        JSONObject req = JSON.parseObject(json);
        MeitResult res = MeitResultUtil.generateResult(ResultEnum.SUCCESS, null);
        res.setReqData(req);
        return res;
    }

    /**
     * 完成美团订单，去dotw下单
     * @param orderId
     * @return
     */
    @PostMapping("/finish_meit_order")
    public Object finishMeitOrderByDotw(String orderId) {
        MeitResult meitResult = meitApiService.finishOrder(orderId);
        log.info(meitResult);
        return meitResult;
    }

    /**
     * 手动同步成功下单的dotw订单
     * @param json
     * @return
     */
    @PostMapping("/sync_meit_order")
    public Object syncMeitOrderByDotw(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        JSONObject reqData = result.getReqData();
        String meitOrderId = reqData.getString("meitOrderId");
        String dotwOrderId = reqData.getString("dotwOrderId");
        result = meitApiService.syncDotwOrderToMeit(meitOrderId, dotwOrderId);
        log.info(result);
        return result;
    }

    /**
     * 手动成功美团订单
     * @param json
     * @return
     */
    @PostMapping("/finish_meit_order_manual")
    public Object finishMeitOrderByManual(@RequestBody String json) {
        MeitResult result = baseRequestTrans(json);
        JSONObject reqData = result.getReqData();
        String orderId = reqData.getString("orderId");
        String confirmNumbers = reqData.getString("confirmNumbers");
        String totalPrice = reqData.getString("totalPrice");
        result = meitApiService.finishOrderManual(orderId, totalPrice, confirmNumbers);
        log.info(result);
        return result;
    }

}
