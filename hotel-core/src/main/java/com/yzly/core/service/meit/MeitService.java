package com.yzly.core.service.meit;

import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.meit.MeitCity;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.MeitHotel;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SyncStatus;
import com.yzly.core.repository.HotelSyncListRepository;
import com.yzly.core.repository.dotw.HotelInfoRepository;
import com.yzly.core.repository.meit.MeitCityRepository;
import com.yzly.core.repository.meit.MeitTraceLogRepository;
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
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Service
@CommonsLog
public class MeitService {

    @Autowired
    private MeitTraceLogRepository meitTraceLogRepository;
    @Autowired
    private HotelSyncListRepository hotelSyncListRepository;
    @Autowired
    private MeitCityRepository meitCityRepository;
    @Autowired
    private HotelInfoRepository hotelInfoRepository;

    /**
     * 增加或修改美团调用日志
     * @param traceId
     * @param encyptData
     * @param reqData
     * @param meitTraceLog
     * @return
     */
    public MeitTraceLog addOrUpdateTrace(String traceId, String encyptData, String reqData, MeitTraceLog meitTraceLog) {
        if (meitTraceLog == null) {
            meitTraceLog = new MeitTraceLog();
        }
        meitTraceLog.setTraceId(traceId);
        meitTraceLog.setEncryptData(encyptData);
        meitTraceLog.setReqData(reqData);
        return meitTraceLogRepository.save(meitTraceLog);
    }

    /**
     * 按照分销商分页查询出酒店基础数据
     * @param page
     * @param size
     * @param distributor
     * @return
     */
    public List<MeitHotel> getHotelBasicList(int page, int size, DistributorEnum distributor) {
        Pageable pageable = new PageRequest(page - 1, size);
        Page<HotelSyncList> hpage = hotelSyncListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (distributor != null) {
                list.add(criteriaBuilder.equal(root.get("distributor").as(DistributorEnum.class), distributor));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
        if (hpage.getSize() == 0) {
            return null;
        }
        List<MeitHotel> mlist = new ArrayList<>();
        for (HotelSyncList hs : hpage.getContent()) {
            HotelInfo hotelInfo = hotelInfoRepository.findByDotwHotelCode(hs.getHotelId());
            MeitHotel mh = new MeitHotel();
            MeitCity mc = meitCityRepository.findByNameEN(hotelInfo.getCity());
            if (mc == null) {
                continue;
            }
            mh.setHotelId(hs.getHotelId());
            mh.setCityId(Integer.valueOf(mc.getCityId()));
            mh.setNameCn(hotelInfo.getHotelNameCn());
            mh.setNameEn(hotelInfo.getHotelName());
            mh.setAddressCn(hotelInfo.getHotelAddressCn());
            mh.setAddressEn(hotelInfo.getHotelAddress());
            mh.setTel(hotelInfo.getReservationTelephone());
            mh.setLongitude(hotelInfo.getLongitude());
            mh.setLatitude(hotelInfo.getLatitude());
            mh.setRegionType(2);
            mlist.add(mh);
            hs.setSyncStatus(SyncStatus.SYNCED);
            hotelSyncListRepository.save(hs);
        }
        return mlist;
    }

}
