package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Repository
public interface RoomPriceByDateRepository extends JpaRepository<RoomPriceByDate, Long> {

    RoomPriceByDate findByRoomTypeCodeAndFromDateAndToDate(String roomTypeCode, String fromDate, String toDate);

    RoomPriceByDate findByRoomTypeCodeAndRateBasisAndFromDateAndToDate(String roomTypeCode, String rateBasis, String fromDate, String toDate);

    Page<RoomPriceByDate> findAll(Specification<RoomPriceByDate> list, Pageable pageable);

    List<RoomPriceByDate> findAllByRoomTypeCodeAndFromDate(String roomTypeCode, String fromDate);

}
