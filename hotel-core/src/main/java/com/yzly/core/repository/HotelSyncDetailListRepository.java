package com.yzly.core.repository;

import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.HotelSyncDetailInfoList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelSyncDetailListRepository extends JpaRepository<HotelSyncDetailInfoList, String> {

    Page<HotelSyncDetailInfoList> findAll(Specification<HotelSyncList> query, Pageable pageable);
}
