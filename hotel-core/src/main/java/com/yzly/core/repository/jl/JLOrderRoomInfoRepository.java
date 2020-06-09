package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLOrderRoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/6/5
 * @desc
 **/
@Repository
public interface JLOrderRoomInfoRepository extends JpaRepository<JLOrderRoomInfo, Long> {

    List<JLOrderRoomInfo> findAllByJlOrderId(Long jlOrderId);

}
