package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLHotelInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lazyb
 * @create 2020/5/7
 * @desc
 **/
public interface JLHotelInfoRepository extends JpaRepository<JLHotelInfo, Long> {

    JLHotelInfo findByHotelId(Integer hotelId);

}
