package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLRatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/6/1
 * @desc
 **/
@Repository
public interface JLRatePlanRepository extends JpaRepository<JLRatePlan, Long> {
}
