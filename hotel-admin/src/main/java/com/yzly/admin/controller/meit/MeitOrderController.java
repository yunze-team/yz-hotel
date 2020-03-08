package com.yzly.admin.controller.meit;

import com.yzly.admin.controller.AdminControllerInter;
import com.yzly.admin.domain.ReturnT;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.query.MeitOrderQuery;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import com.yzly.core.service.dotw.BookingService;
import com.yzly.core.service.meit.MeitQueryService;
import com.yzly.core.service.meit.MeitService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lazyb
 * @create 2020/3/7
 * @desc
 **/
@RestController
@CommonsLog
public class MeitOrderController {

    @Autowired
    private MeitQueryService meitQueryService;
    @Autowired
    private MeitService meitService;
    @Autowired
    private AdminControllerInter adminControllerInter;
    @Autowired
    private BookingService bookingService;

    @GetMapping("/meit/order")
    public Map<String, Object> getPageOrder(int page, int rows, MeitOrderQuery meitOrderQuery) {
        Map<String, Object> resMap = new HashMap<>();
        Page<MeitOrderBookingInfo> mpage = meitQueryService.findAllByPageQuery(page, rows, meitOrderQuery);
        resMap.put("total", mpage.getTotalElements());
        resMap.put("rows", mpage.getContent());
        return resMap;
    }

    /**
     * 修改订单，并去dotw下单
     * @param meitOrderBookingInfo
     * @return
     */
    @PostMapping("/meit/order/dotw")
    public ReturnT dotwOrder(MeitOrderBookingInfo meitOrderBookingInfo) {
        MeitOrderBookingInfo meitOrder = null;
        try {
            meitOrder = meitQueryService.editByGuestAndPlanCode(meitOrderBookingInfo);
            adminControllerInter.finishMeitOrderByDotw(String.valueOf(meitOrder.getOrderId()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ReturnT(500, e.getMessage());
        }
        return new ReturnT(meitOrder);
    }

    /**
     * 通过填入总价和房间确认号，手动完成美团订单
     * @param meitOrderBookingInfo
     * @return
     */
    @PostMapping("/meit/order/manual")
    public ReturnT manualOrder(MeitOrderBookingInfo meitOrderBookingInfo) {
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(String.valueOf(meitOrderBookingInfo.getOrderId()));
        if (!meitOrder.getOrderStatus().equals(PlatformOrderStatusEnum.BOOKING)) {
            return new ReturnT(500, "订单状态不为BOOKING，无法执行此操作！");
        }
        meitOrder = meitService.updateOrderByManual(String.valueOf(meitOrderBookingInfo.getOrderId()),
                meitOrderBookingInfo.getConfirmationNumbers(), String.valueOf(meitOrderBookingInfo.getActualTotalPrice()));
        return new ReturnT(meitOrder);
    }

    /**
     * 通过dotw成功下单的订单id，手动同步美团订单的信息和状态
     * @param orderId
     * @param dotwOrderId
     * @return
     */
    @PostMapping("/meit/order/sync")
    public ReturnT syncManualOrderByDotw(@RequestParam(name = "orderId") String orderId,
                                         @RequestParam(name = "dotwOrderId") String dotwOrderId) {
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(orderId);
        if (!meitOrder.getOrderStatus().equals(PlatformOrderStatusEnum.BOOKING)) {
            return new ReturnT(500, "订单状态不为BOOKING，无法执行此操作！");
        }
        BookingOrderInfo dotwOrder = bookingService.getOneById(dotwOrderId);
        meitOrder = meitService.updateOrderByBookingInfo(orderId, dotwOrder);
        return new ReturnT(meitOrder);
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @PostMapping("/meit/order/cancel")
    public ReturnT cancelOrder(@RequestParam(name = "orderId") String orderId) {
        MeitOrderBookingInfo meitOrder = meitService.getOrderByOrderId(orderId);
        if (!meitOrder.getOrderStatus().equals(PlatformOrderStatusEnum.BOOK_SUCCESS)) {
            return new ReturnT(500, "订单状态不为BOOK_SUCCESS，无法执行此操作！");
        }
        Object rest = adminControllerInter.cancelMeitOrder(orderId);
        return new ReturnT(rest);
    }

}
