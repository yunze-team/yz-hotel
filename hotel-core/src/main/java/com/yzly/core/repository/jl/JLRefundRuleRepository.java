package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLRefundRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/6/2
 * @desc
 **/
@Repository
public interface JLRefundRuleRepository extends JpaRepository<JLRefundRule, Long> {

    JLRefundRule findOneByRefundRuleId(String refundRuleId);

}
