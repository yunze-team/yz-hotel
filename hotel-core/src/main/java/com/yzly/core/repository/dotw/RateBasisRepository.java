package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RateBasis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
@Repository
public interface RateBasisRepository extends JpaRepository<RateBasis, Long> {

    RateBasis findByCode(String code);

}
