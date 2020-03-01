package com.yzly.api.controller.dotw;

import com.yzly.api.service.dotw.MeitApiService;
import com.yzly.core.domain.meit.dto.MeitResult;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
