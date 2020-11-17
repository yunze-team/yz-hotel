package com.yzly.core.repository.ctrip;

import com.yzly.core.domain.ctrip.CtripRoomTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/11/17
 * @desc
 **/
@Repository
public interface CtripRoomTypeCodeRepository extends JpaRepository<CtripRoomTypeCode, Long> {

    List<CtripRoomTypeCode> findAllByHotelCodeAndSupplier(String hotelCode, String supplier);

    CtripRoomTypeCode findOneByCtripRoomTypeCodeAndSupplier(String ctripRoomTypeCode, String supplier);

}
