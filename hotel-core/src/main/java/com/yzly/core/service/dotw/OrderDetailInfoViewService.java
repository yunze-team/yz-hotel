package com.yzly.core.service.dotw;

import com.alibaba.druid.util.StringUtils;
import com.yzly.core.domain.dotw.OrderDetailInfo;
import com.yzly.core.domain.dotw.query.OrderDetailInfoQuery;
import com.yzly.core.repository.dotw.OrderDetailInfoViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailInfoViewService {

    @Autowired
    private OrderDetailInfoViewRepository orderDetailInfoViewRepository;

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
}
