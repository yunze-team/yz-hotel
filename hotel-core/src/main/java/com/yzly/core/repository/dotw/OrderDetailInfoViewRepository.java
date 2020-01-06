package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.OrderDetailInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailInfoViewRepository extends JpaRepository<OrderDetailInfo, String> {

    Page<OrderDetailInfo> findAll(Specification<OrderDetailInfo> query, Pageable pageable);

}
