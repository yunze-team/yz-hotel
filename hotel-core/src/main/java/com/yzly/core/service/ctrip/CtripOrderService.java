package com.yzly.core.service.ctrip;

import com.yzly.core.domain.ctrip.CtripOrderInfo;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.repository.ctrip.CtripOrderInfoRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/11/10
 * @desc
 **/
@Service
@CommonsLog
public class CtripOrderService {

    @Autowired
    private CtripOrderInfoRepository ctripOrderInfoRepository;

    /**
     * 通过携程订单id和订单确认号，获得一个携程订单
     * @param uniqueId
     * @param confirmCode
     * @return
     */
    public CtripOrderInfo findOneOrderByNumber(String uniqueId, String confirmCode) {
        return ctripOrderInfoRepository.findOneByCtripUniqueIdAndHotelConfirmNumber(uniqueId, confirmCode);
    }

    /**
     * 通过携程唯一请求号查找一个订单
     * @param uniqueId
     * @return
     */
    public CtripOrderInfo findOneCtripOrder(String uniqueId) {
        List<CtripOrderInfo> clist = ctripOrderInfoRepository.findAllByCtripUniqueId(uniqueId);
        if (clist != null && clist.size() > 0) {
            return clist.get(0);
        }
        return null;
    }

    /**
     * 保存携程订单
     * @param ctripOrderInfo
     * @param confirmNumber
     * @return
     */
    public CtripOrderInfo createOrder(CtripOrderInfo ctripOrderInfo, String confirmNumber) {
        ctripOrderInfo.setHotelConfirmNumber(confirmNumber);
        ctripOrderInfo.setStatus("S");
        ctripOrderInfo.setOrderStatus(OrderStatus.SAVED);
        return ctripOrderInfoRepository.save(ctripOrderInfo);
    }

    /**
     * 取消携程订单
     * @param confirmCode
     * @param uniqueId
     * @return
     */
    public CtripOrderInfo cancelOrder(String confirmCode, String uniqueId) {
        CtripOrderInfo ctripOrderInfo = ctripOrderInfoRepository.findOneByCtripUniqueIdAndHotelConfirmNumber(uniqueId, confirmCode);
        ctripOrderInfo.setStatus("C");
        ctripOrderInfo.setOrderStatus(OrderStatus.CANCELED);
        return ctripOrderInfoRepository.save(ctripOrderInfo);
    }

}
