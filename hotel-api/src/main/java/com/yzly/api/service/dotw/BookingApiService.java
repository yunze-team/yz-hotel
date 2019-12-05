package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.service.dotw.BookingService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/4
 * @desc
 **/
@Service
@CommonsLog
public class BookingApiService {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private DCMLHandler dcmlHandler;

    /**
     * 通过getrooms获得的allocationDetails和passenger列表去确认订单
     * @param allocationDetails
     * @param plist
     * @throws Exception
     */
    public Object confirmBooking(String allocationDetails, List<Passenger> plist) throws Exception {
        // 保存订单初始信息
        BookingOrderInfo orderInfo = bookingService.saveBookingByAllocation(allocationDetails, plist);
        // 发往dotw确认
        JSONObject result = dcmlHandler.confirmBookingByOrder(orderInfo, plist);
        if (!dcmlHandler.judgeResult(result)) {
            bookingService.updateBookingOrderStatus(orderInfo, OrderStatus.FAILED);
            return result;
        }
        // 获得结果，更新order
        BookingOrderInfo finalOrder = bookingService.updateBookingByJSON(result, orderInfo);
        return finalOrder;
    }

    /**
     * 预取消订单
     * @param allocationDetails
     * @return
     * @throws Exception
     */
    public Object preCancelBooking(String allocationDetails) throws Exception {
        BookingOrderInfo orderInfo = bookingService.getOneByAllocation(allocationDetails);
        if (orderInfo == null || !orderInfo.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new Exception("order is null or order status is not confirmed");
        }
        // 发往dotw进行预取消订单
        JSONObject preCancel = dcmlHandler.cancelBooking(orderInfo, "no");
        if (!dcmlHandler.judgeResult(preCancel)) {
            return preCancel;
        }
        orderInfo = bookingService.preCancelOrder(orderInfo, preCancel);
        return orderInfo.getPenaltyApplied();
    }

    /**
     * 取消订单
     * @param allocationDetails
     * @return
     * @throws Exception
     */
    public Object cancelBooking(String allocationDetails) throws Exception {
        BookingOrderInfo orderInfo = bookingService.getOneByAllocation(allocationDetails);
        if (orderInfo == null || !orderInfo.getOrderStatus().equals(OrderStatus.PRECANCLED)) {
            throw new Exception("order is null or order status is not precancled");
        }
        // 发往dotw进行取消订单
        JSONObject cancel = dcmlHandler.cancelBooking(orderInfo, "yes");
        if (!dcmlHandler.judgeResult(cancel)) {
            return cancel;
        }
        bookingService.updateBookingOrderStatus(orderInfo, OrderStatus.CANCELED);
        return cancel;
    }

}
