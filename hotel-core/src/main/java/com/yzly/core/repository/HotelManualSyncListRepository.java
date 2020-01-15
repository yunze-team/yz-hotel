package com.yzly.core.repository;

import com.yzly.core.domain.HotelManualSyncList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Repository
public interface HotelManualSyncListRepository extends JpaRepository<HotelManualSyncList, Long> {
}
