package com.yzly.core.service.meit;

import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.query.MeitOrderQuery;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import com.yzly.core.repository.meit.MeitOrderBookingInfoRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/3/7
 * @desc
 **/
@Service
@CommonsLog
public class MeitQueryService {

    @Autowired
    private MeitOrderBookingInfoRepository meitOrderBookingInfoRepository;

    /**
     * 修改订单的客户信息和plancode
     * @param meitOrderBookingInfo
     * @return
     */
    public MeitOrderBookingInfo editByGuestAndPlanCode(MeitOrderBookingInfo meitOrderBookingInfo) throws Exception {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findOne(meitOrderBookingInfo.getId());
        if (!meitOrder.getOrderStatus().equals(PlatformOrderStatusEnum.BOOKING)) {
            throw new Exception("order status is not booking.");
        }
        meitOrder.setRatePlanCode(meitOrderBookingInfo.getRatePlanCode());
        meitOrder.setGuestInfo(meitOrderBookingInfo.getGuestInfo());
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 分页检索美团订单信息
     * @param page
     * @param size
     * @param meitOrderQuery
     * @return
     */
    public Page<MeitOrderBookingInfo> findAllByPageQuery(int page, int size, MeitOrderQuery meitOrderQuery) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        return meitOrderBookingInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(meitOrderQuery.getHotelId())) {
                list.add(criteriaBuilder.equal(root.get("hotelId").as(String.class), meitOrderQuery.getHotelId()));
            }
            if (StringUtils.isNotEmpty(meitOrderQuery.getOrderId())) {
                list.add(criteriaBuilder.equal(root.get("orderId").as(String.class), meitOrderQuery.getOrderId()));
            }
            if (StringUtils.isNotEmpty(meitOrderQuery.getOrderStatus())) {
                PlatformOrderStatusEnum queryStatus = PlatformOrderStatusEnum.valueOf(meitOrderQuery.getOrderStatus());
                list.add(criteriaBuilder.equal(root.get("orderStatus").as(PlatformOrderStatusEnum.class), queryStatus));
            }
            if (StringUtils.isNotEmpty(meitOrderQuery.getStartDate())) {
                Date startDate = DateTime.parse(meitOrderQuery.getStartDate()).toDate();
                list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), startDate));
            }
            if (StringUtils.isNotEmpty(meitOrderQuery.getEndDate())) {
                Date endDate = DateTime.parse(meitOrderQuery.getEndDate()).toDate();
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), endDate));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

}
