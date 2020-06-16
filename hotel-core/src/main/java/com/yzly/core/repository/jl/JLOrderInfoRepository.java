package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/6/5
 * @desc
 **/
@Repository
public interface JLOrderInfoRepository extends JpaRepository<JLOrderInfo, Long> {

    JLOrderInfo findByCustomerOrderCode(String customerOrderCode);

}
