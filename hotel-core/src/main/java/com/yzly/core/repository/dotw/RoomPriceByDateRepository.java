package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomPriceByDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Repository
public interface RoomPriceByDateRepository extends JpaRepository<RoomPriceByDate, Long> {

    RoomPriceByDate findByRoomTypeCodeAndFromDateAndToDate(String roomTypeCode, String fromDate, String toDate);

}
