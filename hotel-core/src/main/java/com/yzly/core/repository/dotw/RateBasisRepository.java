package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RateBasis;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
public interface RateBasisRepository extends JpaRepository<RateBasis, Long> {

    RateBasis findByCode(String code);

}
