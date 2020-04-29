package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    Page<MeitOrderBookingInfo> findAll(Specification<MeitOrderBookingInfo> list, Pageable pageable);

    List<MeitOrderBookingInfo> findAllByRoomIdAndRatePlanCodeAndCheckinAndCheckoutAndOrderStatus(
            String RoomId, String ratePlanCode, String checkIn, String checkOut, PlatformOrderStatusEnum orderStatus);

}
