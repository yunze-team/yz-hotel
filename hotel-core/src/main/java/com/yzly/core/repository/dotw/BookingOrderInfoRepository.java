package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.BookingOrderInfo;
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

    BookingOrderInfo findByBookingCode(String bookingCode);

}
