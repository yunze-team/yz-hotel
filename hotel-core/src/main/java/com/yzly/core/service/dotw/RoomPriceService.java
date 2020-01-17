package com.yzly.core.service.dotw;

import com.yzly.core.domain.HotelManualSyncList;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import com.yzly.core.domain.dotw.RoomType;
import com.yzly.core.domain.dotw.query.RoomPriceQuery;
import com.yzly.core.domain.dotw.vo.RoomPriceExcelData;
import com.yzly.core.repository.HotelManualSyncListRepository;
import com.yzly.core.repository.dotw.HotelInfoRepository;
import com.yzly.core.repository.dotw.RoomPriceByDateRepository;
import com.yzly.core.repository.dotw.RoomTypeRepository;
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
 * @create 2020/1/17
 * @desc
 **/
@Service
@CommonsLog
public class RoomPriceService {

    @Autowired
    private RoomPriceByDateRepository roomPriceByDateRepository;
    @Autowired
    private HotelManualSyncListRepository hotelManualSyncListRepository;
    @Autowired
    private HotelInfoRepository hotelInfoRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    /**
     * 分页查询酒店房型价格数据
     * @param page
     * @param size
     * @param roomPriceQuery
     * @return
     */
    public Page<RoomPriceByDate> findAllByPageQuery(Integer page, Integer size, RoomPriceQuery roomPriceQuery) {
        Pageable pageable = new PageRequest(page - 1, size);
        return roomPriceByDateRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(roomPriceQuery.getHotelCode())) {
                list.add(criteriaBuilder.equal(root.get("hotelCode").as(String.class), roomPriceQuery.getHotelCode()));
            }
            if (StringUtils.isNotEmpty(roomPriceQuery.getRoomTypeCode())) {
                list.add(criteriaBuilder.equal(root.get("roomTypeCode").as(String.class), roomPriceQuery.getRoomTypeCode()));
            }
            if (StringUtils.isNotEmpty(roomPriceQuery.getFromDate())) {
                list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate").as(String.class), roomPriceQuery.getFromDate()));
            }
            if (StringUtils.isNotEmpty(roomPriceQuery.getToDate())) {
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("toDate").as(String.class), roomPriceQuery.getToDate()));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    /**
     * 通过日期数组查询房型的excel组装数据
     * @param days
     * @return
     */
    public List<RoomPriceExcelData> getAllPriceExcelData(List<String> days) {
        List<RoomPriceExcelData> rlist = new ArrayList<>();
        // 查询所有的需要手工同步价格的酒店列表
        List<HotelManualSyncList> hlist = hotelManualSyncListRepository.findAll();
        // 循环酒店数据组装roomPriceExcelData
        for (HotelManualSyncList h : hlist) {
            RoomPriceExcelData roomPriceExcelData = new RoomPriceExcelData();
            roomPriceExcelData.setHotelCode(h.getHotelId());
            roomPriceExcelData.setHotelName(hotelInfoRepository.findByDotwHotelCode(h.getHotelId()).getHotelName());
            // 通过酒店id获得这些酒店对应的所有房型
            List<RoomType> roomTypeList = roomTypeRepository.findAllByHotelId(h.getHotelId());
            // 循环房型数据组装roomTypePriceData
            roomPriceExcelData.setRoomTypeList(roomTypeList);
            rlist.add(roomPriceExcelData);
        }
        return rlist;
    }

}
