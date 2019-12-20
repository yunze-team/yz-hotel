package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.MeitTraceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Repository
public interface MeitTraceLogRepository extends JpaRepository<MeitTraceLog, Long> {
}
