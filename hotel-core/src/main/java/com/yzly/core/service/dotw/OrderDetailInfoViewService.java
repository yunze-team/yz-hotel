package com.yzly.core.service.dotw;

import com.yzly.core.domain.dotw.BookingOrderInfo;
import com.yzly.core.domain.dotw.OrderDetailInfo;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.query.OrderDetailInfoQuery;
import com.yzly.core.repository.dotw.BookingOrderInfoRepository;
import com.yzly.core.repository.dotw.OrderDetailInfoViewRepository;
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

@Service
public class OrderDetailInfoViewService {

    @Autowired
    private OrderDetailInfoViewRepository orderDetailInfoViewRepository;
    @Autowired
    private BookingOrderInfoRepository bookingOrderInfoRepository;

    public Page<OrderDetailInfo> getOrderDetailInfos(int page, int size, OrderDetailInfoQuery orderDetailInfoQuery){
        Pageable pageable =   new PageRequest(page - 1, size);
        return orderDetailInfoViewRepository.findAll((root, criteriaQuery, criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(orderDetailInfoQuery.getOrderCode())){
                predicates.add(criteriaBuilder.like(root.get("orderCode").as(String.class), "%"+orderDetailInfoQuery.getOrderCode()+"%"));
            }
            Predicate[] p = new Predicate[predicates.size()];
            return criteriaBuilder.and(predicates.toArray(p));
        },pageable);
    }

    public Page<BookingOrderInfo> findAllByPageQuery(int page, int size, OrderDetailInfoQuery orderDetailInfoQuery) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        Pageable pageable =   new PageRequest(page - 1, size, sort);
        return bookingOrderInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(orderDetailInfoQuery.getHotelId())) {
                list.add(criteriaBuilder.equal(root.get("hotelId").as(String.class), orderDetailInfoQuery.getHotelId()));
            }
            if (StringUtils.isNotEmpty(orderDetailInfoQuery.getOrderId())) {
                list.add(criteriaBuilder.equal(root.get("orderId").as(String.class), orderDetailInfoQuery.getOrderId()));
            }
            if (StringUtils.isNotEmpty(orderDetailInfoQuery.getOrderStatus())) {
                OrderStatus orderStatus = OrderStatus.valueOf(orderDetailInfoQuery.getOrderStatus());
                list.add(criteriaBuilder.equal(root.get("orderStatus").as(OrderStatus.class), orderStatus));
            }
            if (org.apache.commons.lang.StringUtils.isNotEmpty(orderDetailInfoQuery.getStartDate())) {
                Date startDate = DateTime.parse(orderDetailInfoQuery.getStartDate()).toDate();
                list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), startDate));
            }
            if (org.apache.commons.lang.StringUtils.isNotEmpty(orderDetailInfoQuery.getEndDate())) {
                Date endDate = DateTime.parse(orderDetailInfoQuery.getEndDate()).toDate();
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), endDate));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }
}
