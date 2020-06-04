package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLBookingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/6/2
 * @desc
 **/
@Repository
public interface JLBookingRuleRepository extends JpaRepository<JLBookingRule, Long> {

    JLBookingRule findOneByBookingRuleId(String bookingRuleId);

}
