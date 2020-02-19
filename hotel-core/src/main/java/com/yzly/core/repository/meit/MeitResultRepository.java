package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.dto.MeitResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/2/19
 * @desc
 **/
@Repository
public interface MeitResultRepository extends JpaRepository<MeitResult, Long> {
}
