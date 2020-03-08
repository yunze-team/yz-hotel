package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.BookingOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@Repository
public interface BookingOrderInfoRepository extends JpaRepository<BookingOrderInfo, Long> {

    BookingOrderInfo  findByAllocationDetails(String allocationDetails);

    BookingOrderInfo findByBookingCode(String bookingCode);

    BookingOrderInfo findByOrderId(String orderId);

    Page<BookingOrderInfo> findAll(Specification<BookingOrderInfo> list, Pageable pageable);

}
