package com.yzly.core.service.meit;

import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.domain.meit.query.MeitOrderQuery;
import com.yzly.core.domain.meit.query.MeitResultQuery;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import com.yzly.core.repository.meit.MeitOrderBookingInfoRepository;
import com.yzly.core.repository.meit.MeitResultRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    @Autowired
    private MeitResultRepository meitResultRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

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

    /**
     * 分页查询美团请求日志
     * @param page
     * @param size
     * @param meitResultQuery
     * @return
     */
    public Page<MeitResult> findAllByPageQuery(int page, int size, MeitResultQuery meitResultQuery) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Query query = new Query();
        if (StringUtils.isNotEmpty(meitResultQuery.getTraceId())) {
            query.addCriteria(Criteria.where("traceId").is(meitResultQuery.getTraceId()));
        }
        if (StringUtils.isNotEmpty(meitResultQuery.getReqMethod())) {
            query.addCriteria(Criteria.where("reqMethod").is(meitResultQuery.getReqMethod()));
        }
        if (StringUtils.isNotEmpty(meitResultQuery.getStartDate())) {
            Date startDate = DateTime.parse(meitResultQuery.getStartDate()).toDate();
            query.addCriteria(Criteria.where("createdAt").gte(startDate));
        }
        if (StringUtils.isNotEmpty(meitResultQuery.getEndDate())) {
            Date endDate = DateTime.parse(meitResultQuery.getEndDate()).toDate();
            query.addCriteria(Criteria.where("createdAt").lte(endDate));
        }
        long total = mongoTemplate.count(query, MeitResult.class);
        List<MeitResult> mlist = mongoTemplate.find(query.with(pageable), MeitResult.class);
        Page<MeitResult> mpage = new PageImpl<>(mlist, pageable, total);
        return mpage;
    }

}
