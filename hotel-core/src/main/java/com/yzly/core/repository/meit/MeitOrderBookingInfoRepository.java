package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/1/3
 * @desc
 **/
@Repository
public interface MeitOrderBookingInfoRepository extends JpaRepository<MeitOrderBookingInfo, Long> {

    List<MeitOrderBookingInfo> findAllByOrderId(Long orderId);

    MeitOrderBookingInfo findByOrderId(Long orderId);

}
