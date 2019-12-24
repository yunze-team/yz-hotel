package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.HotelClassificationIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@Repository
public interface HotelClassificationIdsRepository extends JpaRepository<HotelClassificationIds, Long> {

    HotelClassificationIds findByCode(String code);

}
