package com.yzly.api.controller.jl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.JLHandler;
import com.yzly.api.service.jl.JLOrderApiService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 捷旅订单相关
 * @author lazyb
 * @create 2020/6/5
 * @desc
 **/
@RequestMapping("/api/jl")
@RestController
@CommonsLog
public class JLController {

    @Autowired
    private JLHandler jlHandler;
    @Autowired
    private JLOrderApiService jlOrderApiService;

    /**
     * 用户查询酒店价格
     * @param json
     * @return
     */
    @PostMapping("/query_hotel_price")
    public Object queryHotelPrice(@RequestBody String json) {
        JSONObject req = JSON.parseObject(json);
        log.info("hotelId:" + req.getInteger("hotelId"));
        log.info("checkInDate:" + req.getString("checkInDate"));
        log.info("checkOutDate:" + req.getString("checkOutDate"));
        log.info("roomGroups:" + req.get("roomGroups"));
        return jlHandler.queryHotelPriceByUser(req, true);
    }

    /**
     * 预订单
     * @param json
     * @return
     */
    @PostMapping("/pre_order")
    public Object preOrder(@RequestBody String json) {
        JSONObject req = JSON.parseObject(json);
        return jlOrderApiService.preOrder(req);
    }

    /**
     * 下单
     * @param json
     * @return
     */
    @PostMapping("/order")
    public Object order(@RequestBody String json) {
        JSONObject req = JSON.parseObject(json);
        String orderCode = req.getString("code");
        return jlOrderApiService.finishOrder(orderCode);
    }

    /**
     * 取消订单
     * @param code
     * @return
     */
    @GetMapping("/cancel")
    public Object cancel(@RequestParam String code) {
        return jlOrderApiService.cancelOrder(code);
    }

    /**
     * 查询订单
     * @param code
     * @return
     */
    @GetMapping("/query")
    public Object query(@RequestParam String code) {
        return jlOrderApiService.queryOrder(code);
    }

}
