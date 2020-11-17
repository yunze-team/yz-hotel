package com.yzly.core.repository.ctrip;

import com.yzly.core.domain.ctrip.CtripHotelCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/11/17
 * @desc
 **/
@Repository
public interface CtripHotelCodeRepository extends JpaRepository<CtripHotelCode, Long> {

    CtripHotelCode findOneByCtripHotelCodeAndSupplier(String ctripHotelCode, String supplier);

    CtripHotelCode findOneByHotelCodeAndSupplier(String hotelCode, String supplier);

}
