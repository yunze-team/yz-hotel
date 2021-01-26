package com.yzly.admin.controller;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lazyb
 * @create 2020/3/8
 * @desc
 **/
@FeignClient("yz-hotel-api")
public interface AdminControllerInter {

    /**
     * 去dotw下美团单
     * @param orderId
     * @return
     */
    @PostMapping("/api/admin/finish_meit_order")
    Object finishMeitOrderByDotw(@RequestParam(name = "orderId") String orderId);

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/api/admin/cancel_order")
    Object cancelMeitOrder(@RequestParam(name = "orderId") String orderId);

    /**
     * 同步捷旅房型明细
     * @param hotelId
     */
    @PostMapping("/api/admin/sync_jl_room_type")
    void syncJLRoomType(@RequestParam(name = "hotelId") Integer hotelId);

}
