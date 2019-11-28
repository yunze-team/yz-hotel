package com.yzly.core.service.dotw;

import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.redis.IRedisService;
import com.yzly.core.repository.dotw.HotelInfoRepository;
import com.yzly.core.util.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/11/24
 * @desc
 **/
@Service
@CommonsLog
public class HotelInfoService {

    @Autowired
    private HotelInfoRepository hotelInfoRepository;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private IRedisService redisService;

    @Value("${dotw.hotel.excel}")
    private String dataPath;
    @Value("${dotw.redis.list.key}")
    private String hotelListKey;

    /**
     * 通过dotw给的excel数据更新酒店基础数据
     */
    @Transactional
    public void syncByExcel() throws Exception {
        List<HotelInfo> list = commonUtil.excelToHotelBean(dataPath);
        int i = 0;
        for (HotelInfo hotelInfo : list) {
            if (hotelInfoRepository.findByDotwHotelCode(hotelInfo.getDotwHotelCode()) != null) {
                continue;
            }
            i++;
            hotelInfoRepository.save(hotelInfo);
        }
        log.info("sync hotel items size: " + i);
    }

    /**
     * 10w+批量同步，不考虑重复问题，提高效率
     * @throws Exception
     */
    @Transactional
    public void syncByExcelForBatch() throws Exception {
        List<HotelInfo> list = new ArrayList<>();
        // 先存入redis，重复读取excel很花时间
        list = redisService.getList(hotelListKey, HotelInfo.class);
        if (list == null || list.size() == 0) {
            list = commonUtil.excelToHotelBean(dataPath);
            redisService.setList(hotelListKey, list);
        }
        int i = 0;
        for (HotelInfo hotelInfo : list) {
            i++;
            hotelInfoRepository.save(list);
        }
        log.info("sync batch hotel items size: " + i);
    }

    public Page<HotelInfo> findAllByPageQuery(Integer page, Integer size, HotelQuery hotelQuery) {
        Pageable pageable = new PageRequest(page - 1, size);
        return hotelInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(hotelQuery.getDotwHotelCode())) {
                list.add(criteriaBuilder.equal(root.get("dotwHotelCode").as(String.class), hotelQuery.getDotwHotelCode()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCountry())) {
                list.add(criteriaBuilder.equal(root.get("country").as(String.class), hotelQuery.getCountry()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getCity())) {
                list.add(criteriaBuilder.equal(root.get("city").as(String.class), hotelQuery.getCity()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getBrandName())) {
                list.add(criteriaBuilder.equal(root.get("brandName").as(String.class), hotelQuery.getBrandName()));
            }
            if (StringUtils.isNotEmpty(hotelQuery.getRegion())) {
                list.add(criteriaBuilder.equal(root.get("region").as(String.class), hotelQuery.getRegion()));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

}
