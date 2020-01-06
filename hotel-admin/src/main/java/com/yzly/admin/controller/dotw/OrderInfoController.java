package com.yzly.admin.controller.dotw;

import com.yzly.core.domain.dotw.OrderDetailInfo;
import com.yzly.core.domain.dotw.query.OrderDetailInfoQuery;
import com.yzly.core.service.dotw.OrderDetailInfoViewService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CommonsLog
public class OrderInfoController {

    @Autowired
    private OrderDetailInfoViewService orderDetailInfoViewService;

    @GetMapping("/order/order_infos")
    public Map<String, Object> getOrderInfo(int page, int rows, OrderDetailInfoQuery orderDetailInfoQuery) {
        Map<String, Object> resMap = new HashMap<>();
        Page<OrderDetailInfo> orderDetailInfos = orderDetailInfoViewService.getOrderDetailInfos(page,rows,orderDetailInfoQuery);
        resMap.put("total", orderDetailInfos.getTotalElements());
        resMap.put("rows", orderDetailInfos.getContent());
        return resMap;
    }
}
