package com.yzly.core.service.jl;

import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.domain.jl.JLCity;
import com.yzly.core.domain.jl.JLHotelInfo;
import com.yzly.core.repository.jl.JLCityRepository;
import com.yzly.core.repository.jl.JLHotelInfoRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 给后管的业务处理类
 * @author lazyb
 * @create 2020/6/19
 * @desc
 **/
@Service
@CommonsLog
public class JLAdminService {

    @Autowired
    private JLHotelInfoRepository jlHotelInfoRepository;
    @Autowired
    private JLCityRepository jlCityRepository;

    /**
     * 分页查询捷旅酒店列表信息
     * @param page
     * @param size
     * @param hotelQuery
     * @return
     */
    public Page<JLHotelInfo> findAllHotelByPage(Integer page, Integer size, HotelQuery hotelQuery) {
        Pageable pageable = new PageRequest(page - 1, size);
        return jlHotelInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(hotelQuery.getCityId())) {
                list.add(criteriaBuilder.equal(root.get("cityId").as(String.class), hotelQuery.getCityId()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCountryId())) {
                list.add(criteriaBuilder.equal(root.get("countryId").as(String.class), hotelQuery.getCountryId()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getHotelId())) {
                list.add(criteriaBuilder.equal(root.get("hotelId").as(String.class), hotelQuery.getHotelId()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getHotelNameCn())) {
                list.add(criteriaBuilder.like(root.get("hotelNameCn").as(String.class), "%" + hotelQuery.getHotelNameCn() + "%"));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getHotelNameEn())) {
                list.add(criteriaBuilder.like(root.get("hotelNameEn").as(String.class), "%" + hotelQuery.getHotelNameEn() + "%"));
            }
            Predicate[] p = new Predicate[list.size()];
            log.info("jl hotel page query: " + p);
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    /**
     * 分页查询捷旅的城市信息
     * @param page
     * @param size
     * @param hotelQuery
     * @return
     */
    public Page<JLCity> findAllCityByPage(Integer page, Integer size, HotelQuery hotelQuery) {
        Pageable pageable = new PageRequest(page - 1, size);
        return jlCityRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(hotelQuery.getCountryId())) {
                list.add(criteriaBuilder.equal(root.get("countryId").as(String.class), hotelQuery.getCountryId()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCountryCn())) {
                list.add(criteriaBuilder.like(root.get("countryCn").as(String.class), "%" + hotelQuery.getCountryCn() + "%"));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCityId())) {
                list.add(criteriaBuilder.equal(root.get("cityId").as(String.class), hotelQuery.getCityId()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCityCn())) {
                list.add(criteriaBuilder.like(root.get("cityCn").as(String.class), "%" + hotelQuery.getCityCn() + "%"));
            }
            Predicate[] p = new Predicate[list.size()];
            log.info("jl city page query: " + p);
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

}
