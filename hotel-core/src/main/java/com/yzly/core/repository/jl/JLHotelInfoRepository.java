package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLHotelInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author lazyb
 * @create 2020/5/7
 * @desc
 **/
public interface JLHotelInfoRepository extends JpaRepository<JLHotelInfo, Long>,
        JpaSpecificationExecutor<JLHotelInfo> {

    JLHotelInfo findByHotelId(Integer hotelId);

}
