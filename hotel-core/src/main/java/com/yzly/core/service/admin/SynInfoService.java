package com.yzly.core.service.admin;

import com.yzly.core.domain.dotw.HotelSyncDetailInfoList;
import com.yzly.core.domain.dotw.query.HotelSycnListQuery;
import com.yzly.core.repository.HotelSyncDetailListRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@CommonsLog
public class SynInfoService {

    @Autowired
    private HotelSyncDetailListRepository hotelSyncDetailListRepository;


    @Transactional
    public Page<HotelSyncDetailInfoList> getSyncListInfo(int page, int size, HotelSycnListQuery hotelSycnListQuery){
        Pageable pageable = new PageRequest(page - 1, size);
        return hotelSyncDetailListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (hotelSycnListQuery.getHotelName() != null) {
                list.add(criteriaBuilder.like(root.get("hotelName"), "%"+hotelSycnListQuery.getHotelName()+"%"));
            }
            if (hotelSycnListQuery.getHotelCode() != null){
                list.add(criteriaBuilder.like(root.get("dotwHotelCode"), "%"+hotelSycnListQuery.getHotelCode()+"%"));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

}
