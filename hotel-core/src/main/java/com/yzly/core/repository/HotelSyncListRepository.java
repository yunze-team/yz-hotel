package com.yzly.core.repository;

import com.yzly.core.domain.HotelSyncList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Repository
public interface HotelSyncListRepository extends JpaRepository<HotelSyncList, Long> {

    Page<HotelSyncList> findAll(Specification<HotelSyncList> query, Pageable pageable);

}
