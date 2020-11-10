package com.yzly.core.repository.ctrip;

import com.yzly.core.domain.ctrip.CtripOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/11/3
 * @desc
 **/
@Repository
public interface CtripOrderInfoRepository extends JpaRepository<CtripOrderInfo, Long> {

    List<CtripOrderInfo> findAllByCtripUniqueId(String ctripUniqueId);

    CtripOrderInfo findOneByCtripUniqueIdAndHotelConfirmNumber(String uniqueId, String confirmNumber);

}
