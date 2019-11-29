package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomType;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.enums.VendorEnum;
import com.yzly.core.redis.IRedisService;
import com.yzly.core.repository.dotw.HotelInfoRepository;
import com.yzly.core.repository.dotw.RoomTypeRepository;
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
    private RoomTypeRepository roomTypeRepository;
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

    public void addRoomsAndHotelAdditionalInfoByHotelJson(JSONObject hotelJson) {
        String hid = hotelJson.getString("@hotelid");
        // 根据isUpdate来判断是否已经更新了房型和附加信息，如已更新，跳过此流程
        long count = hotelInfoRepository.countByDotwHotelCodeAndIsUpdate(hid, "1");
        if (count > 0) {
            return;
        }
        JSONObject roomJson = null;
        // 根据count数量判断是通过JSONObject还是JSONArray来获取json串
        if (hotelJson.getJSONObject("rooms").getJSONObject("room").getString("@count").equals("1")) {
            roomJson = hotelJson.getJSONObject("rooms").getJSONObject("room").getJSONObject("roomType");
            RoomType roomType = getRoomTypeByRoomJSON(roomJson, hid);
            if (roomTypeRepository.findByRoomTypeCode(roomType.getRoomTypeCode()) == null) {
                roomTypeRepository.save(roomType);
            }
        } else {
            JSONArray roomArray = hotelJson.getJSONObject("rooms").getJSONObject("room").getJSONArray("roomType");
            for (int roomIndex = 0; roomIndex < roomArray.size(); roomIndex++) {
                roomJson = roomArray.getJSONObject(roomIndex);
                RoomType roomType = getRoomTypeByRoomJSON(roomJson, hid);
                if (roomTypeRepository.findByRoomTypeCode(roomType.getRoomTypeCode()) == null) {
                    roomTypeRepository.save(roomType);
                }
            }
        }

    }

    public RoomType getRoomTypeByRoomJSON(JSONObject roomJson, String hid) {
        RoomType roomType = new RoomType();
        roomType.setHotelId(hid);
        roomType.setVendor(VendorEnum.DOTW.name());
        roomType.setRoomTypeCode(roomJson.getString("@roomtypecode"));
        roomType.setTwin(roomJson.getString("twin"));
        roomType.setName(roomJson.getString("name"));
        roomType.setRoomCapacityInfo(roomJson.getJSONObject("roomCapacityInfo").toJSONString());
        roomType.setRoomInfo(roomJson.getJSONObject("roomInfo").toJSONString());
        roomType.setRoomAmenities(roomJson.getJSONObject("roomAmenities").toJSONString());
        return roomType;
    }

    public HotelAdditionalInfo getHotelAdditionalByHotelJSON(JSONObject hotelJson) {
        HotelAdditionalInfo hotelAdditionalInfo = new HotelAdditionalInfo();
        hotelAdditionalInfo.setHotelId(hotelJson.getString("@hotelid"));
        hotelAdditionalInfo.setZipCode(hotelJson.getString("zipCode"));
        hotelAdditionalInfo.setRooms(hotelJson.getJSONObject("rooms").toJSONString());
        hotelAdditionalInfo.setRenovationYear(hotelJson.getString("renovationYear"));
        hotelAdditionalInfo.setCityCode(hotelJson.getString("cityCode"));
        hotelAdditionalInfo.setRegionName(hotelJson.getString("regionName"));
        hotelAdditionalInfo.setRating(hotelJson.getString("rating"));
        hotelAdditionalInfo.setDirect(hotelJson.getString("direct"));
        hotelAdditionalInfo.setHotelCheckOut(hotelJson.getString("hotelCheckOut"));
        hotelAdditionalInfo.setDescription2(hotelJson.getJSONObject("description2").toJSONString());
        hotelAdditionalInfo.setGeoPoint(hotelJson.getJSONObject("geoPoint").toJSONString());
        hotelAdditionalInfo.setDescription1(hotelJson.getJSONObject("description1").toJSONString());
        hotelAdditionalInfo.setLastUpdated(hotelJson.getString("lastUpdated"));
        hotelAdditionalInfo.setRegionCode(hotelJson.getString("regionCode"));
        hotelAdditionalInfo.setFloors(hotelJson.getString("floors"));
        hotelAdditionalInfo.setCityName(hotelJson.getString("cityName"));
        hotelAdditionalInfo.setStateName(hotelJson.getString("stateName"));
        hotelAdditionalInfo.setLocationId(hotelJson.getString("locationId"));
        hotelAdditionalInfo.setCountryCode(hotelJson.getString("countryCode"));
        hotelAdditionalInfo.setHotelPhone(hotelJson.getString("hotelPhone"));
        hotelAdditionalInfo.setLeisure(hotelJson.getJSONObject("leisure").toJSONString());
        hotelAdditionalInfo.setPreferred(hotelJson.getString("preferred"));
        hotelAdditionalInfo.setImages(hotelJson.getJSONObject("images").toJSONString());
        hotelAdditionalInfo.setChain(hotelJson.getString("chain"));
        hotelAdditionalInfo.setAddress(hotelJson.getString("address"));
        hotelAdditionalInfo.setHotelPreference(hotelJson.getJSONObject("hotelPreference").toJSONString());
        hotelAdditionalInfo.setBusiness(hotelJson.getJSONObject("business").toJSONString());
        hotelAdditionalInfo.setHotelCheckIn(hotelJson.getString("hotelCheckIn"));
        hotelAdditionalInfo.setHotelName(hotelJson.getString("hotelName"));
        hotelAdditionalInfo.setAmenitie(hotelJson.getJSONObject("amenitie").toJSONString());
        hotelAdditionalInfo.setTransportation(hotelJson.getJSONObject("transportation").toJSONString());
        hotelAdditionalInfo.setRails(hotelJson.getJSONObject("rails").toJSONString());
        hotelAdditionalInfo.setCruises(hotelJson.getJSONObject("cruises").toJSONString());
        hotelAdditionalInfo.setAirports(hotelJson.getJSONObject("airports").toJSONString());
        hotelAdditionalInfo.setFireSafety(hotelJson.getString("fireSafety"));
        hotelAdditionalInfo.setAttraction(hotelJson.getString("attraction"));
        hotelAdditionalInfo.setMinAge(hotelJson.getString("minAge"));
        hotelAdditionalInfo.setFullAddress(hotelJson.getJSONObject("fullAddress").toJSONString());
        hotelAdditionalInfo.setBuiltYear(hotelJson.getString("builtYear"));
        hotelAdditionalInfo.setLuxury(hotelJson.getString("luxury"));
        hotelAdditionalInfo.setLocation(hotelJson.getString("location"));
        hotelAdditionalInfo.setStateCode(hotelJson.getString("stateCode"));
        hotelAdditionalInfo.setCountryName(hotelJson.getString("countryName"));
        hotelAdditionalInfo.setNoOfRooms(hotelJson.getString("noOfRooms"));
        return hotelAdditionalInfo;
    }

}
