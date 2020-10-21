package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.HotelAdditionalInfo;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomType;
import com.yzly.core.domain.dotw.query.HotelQuery;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.enums.VendorEnum;
import com.yzly.core.redis.IRedisService;
import com.yzly.core.repository.dotw.HotelAdditionalInfoRepository;
import com.yzly.core.repository.dotw.HotelInfoRepository;
import com.yzly.core.repository.dotw.RoomTypeRepository;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.util.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    private HotelAdditionalInfoRepository hotelAdditionalInfoRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private EventAttrRepository eventAttrRepository;

    @Value("${dotw.hotel.excel}")
    private String dataPath;
    @Value("${dotw.redis.list.key}")
    private String hotelListKey;

    private static final String DOTW_HOTEL_PULL_COUNTRY = "DOTW_HOTEL_PULL_COUNTRY";
    private static final String DOTW_HOTEL_PULL_SIZE = "DOTW_HOTEL_PULL_SIZE";
    private static final String DOTW_ROOM_PULL_SIZE = "DOTW_ROOM_PULL_SIZE";
    private static final String DOTW_ROOM_PULL_DATE = "DOTW_ROOM_PULL_DATE";
    private static final String DOTW_ROOM_PULL_CITY = "DOTW_ROOM_PULL_CITY";
    private static final String MEIT_ROOM_PRICE_RATE = "MEIT_ROOM_PRICE_RATE";

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
            if (StringUtils.isNotEmpty(hotelQuery.getHotelNameEn())) {
                list.add(criteriaBuilder.like(root.get("hotelName").as(String.class), "%" + hotelQuery.getHotelNameEn() + "%"));
            }
            if (!hotelQuery.getIsUpdateFlag()) {
                if (StringUtils.isNotEmpty(hotelQuery.getIsUpdate())) {
                    list.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("isUpdate").as(String.class), hotelQuery.getIsUpdate()),
                            criteriaBuilder.isNull(root.get("isUpdate").as(String.class))));
                }
            } else {
                if (StringUtils.isNotEmpty(hotelQuery.getIsUpdate())) {
                    list.add(criteriaBuilder.equal(root.get("isUpdate").as(String.class), hotelQuery.getIsUpdate()));
                }
            }
            if (StringUtils.isNotEmpty(hotelQuery.getSyncRoomPriceDate())) {
                list.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("syncRoomPriceDate").as(String.class), hotelQuery.getSyncRoomPriceDate()),
                        criteriaBuilder.isNull(root.get("syncRoomPriceDate").as(String.class))));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    @Transactional
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
        HotelAdditionalInfo hotelAdditionalInfo = null;
        hotelAdditionalInfo = hotelAdditionalInfoRepository.findOneByHotelId(hid);
        if (hotelAdditionalInfo == null) {
            hotelAdditionalInfo = getHotelAdditionalByHotelJSON(hotelJson);
            hotelAdditionalInfoRepository.save(hotelAdditionalInfo);
        }
        // 更新酒店实体
        HotelInfo hotelInfo = hotelInfoRepository.findByDotwHotelCode(hid);
        hotelInfo.setIsUpdate("1");
        hotelInfoRepository.save(hotelInfo);
    }

    public RoomType getRoomTypeByRoomJSON(JSONObject roomJson, String hid) {
        RoomType roomType = new RoomType();
        roomType.setHotelId(hid);
        roomType.setVendor(VendorEnum.DOTW.name());
        roomType.setRoomTypeCode(roomJson.getString("@roomtypecode"));
        roomType.setTwin(roomJson.getString("twin"));
        roomType.setName(roomJson.getString("name"));
        roomType.setRoomCapacityInfo(roomJson.getString("roomCapacityInfo"));
        roomType.setRoomInfo(roomJson.getString("roomInfo"));
        roomType.setRoomAmenities(roomJson.getString("roomAmenities"));
        return roomType;
    }

    public HotelAdditionalInfo getHotelAdditionalByHotelJSON(JSONObject hotelJson) {
        HotelAdditionalInfo hotelAdditionalInfo = new HotelAdditionalInfo();
        hotelAdditionalInfo.setHotelId(hotelJson.getString("@hotelid"));
        hotelAdditionalInfo.setZipCode(hotelJson.getString("zipCode"));
        hotelAdditionalInfo.setRooms(hotelJson.getString("rooms"));
        hotelAdditionalInfo.setRenovationYear(hotelJson.getString("renovationYear"));
        hotelAdditionalInfo.setCityCode(hotelJson.getString("cityCode"));
        hotelAdditionalInfo.setRegionName(hotelJson.getString("regionName"));
        hotelAdditionalInfo.setRating(hotelJson.getString("rating"));
        hotelAdditionalInfo.setDirect(hotelJson.getString("direct"));
        hotelAdditionalInfo.setHotelCheckOut(hotelJson.getString("hotelCheckOut"));
        hotelAdditionalInfo.setDescription2(hotelJson.getString("description2"));
        hotelAdditionalInfo.setGeoPoint(hotelJson.getString("geoPoint"));
        hotelAdditionalInfo.setDescription1(hotelJson.getString("description1"));
        hotelAdditionalInfo.setLastUpdated(hotelJson.getString("lastUpdated"));
        hotelAdditionalInfo.setRegionCode(hotelJson.getString("regionCode"));
        hotelAdditionalInfo.setFloors(hotelJson.getString("floors"));
        hotelAdditionalInfo.setCityName(hotelJson.getString("cityName"));
        hotelAdditionalInfo.setStateName(hotelJson.getString("stateName"));
        hotelAdditionalInfo.setLocationId(hotelJson.getString("locationId"));
        hotelAdditionalInfo.setCountryCode(hotelJson.getString("countryCode"));
        hotelAdditionalInfo.setHotelPhone(hotelJson.getString("hotelPhone"));
        hotelAdditionalInfo.setLeisure(hotelJson.getString("leisure"));
        hotelAdditionalInfo.setPreferred(hotelJson.getString("preferred"));
        hotelAdditionalInfo.setImages(hotelJson.getString("images"));
        hotelAdditionalInfo.setChain(hotelJson.getString("chain"));
        hotelAdditionalInfo.setAddress(hotelJson.getString("address"));
        hotelAdditionalInfo.setHotelPreference(hotelJson.getString("hotelPreference"));
        hotelAdditionalInfo.setBusiness(hotelJson.getString("business"));
        hotelAdditionalInfo.setHotelCheckIn(hotelJson.getString("hotelCheckIn"));
        hotelAdditionalInfo.setHotelName(hotelJson.getString("hotelName"));
        hotelAdditionalInfo.setAmenitie(hotelJson.getString("amenitie"));
        hotelAdditionalInfo.setTransportation(hotelJson.getString("transportation"));
        hotelAdditionalInfo.setRails(hotelJson.getString("rails"));
        hotelAdditionalInfo.setCruises(hotelJson.getString("cruises"));
        hotelAdditionalInfo.setAirports(hotelJson.getString("airports"));
        hotelAdditionalInfo.setFireSafety(hotelJson.getString("fireSafety"));
        hotelAdditionalInfo.setAttraction(hotelJson.getString("attraction"));
        hotelAdditionalInfo.setMinAge(hotelJson.getString("minAge"));
        hotelAdditionalInfo.setFullAddress(hotelJson.getString("fullAddress"));
        hotelAdditionalInfo.setBuiltYear(hotelJson.getString("builtYear"));
        hotelAdditionalInfo.setLuxury(hotelJson.getString("luxury"));
        hotelAdditionalInfo.setLocation(hotelJson.getString("location"));
        hotelAdditionalInfo.setStateCode(hotelJson.getString("stateCode"));
        hotelAdditionalInfo.setCountryName(hotelJson.getString("countryName"));
        hotelAdditionalInfo.setNoOfRooms(hotelJson.getString("noOfRooms"));
        return hotelAdditionalInfo;
    }

    /**
     * 给job定时拉取同步dotw酒店的定时任务的方法，
     * 依据参数表配置的国家和每次拉取的条数去表查询出未同步的酒店列表信息
     * @return
     */
    public List<HotelInfo> findHotelListByEventAttr() {
        String country = eventAttrRepository.findByEventType(DOTW_HOTEL_PULL_COUNTRY).getEventValue();
        int size = Integer.valueOf(eventAttrRepository.findByEventType(DOTW_HOTEL_PULL_SIZE).getEventValue());
        HotelQuery hotelQuery = new HotelQuery();
        hotelQuery.setCountry(country);
        hotelQuery.setIsUpdate("1");
        Page<HotelInfo> hpage = this.findAllByPageQuery(1, size, hotelQuery);
        return hpage.getContent();
    }

    public List<HotelInfo> findUpdatedHotelList() {
        int size = Integer.valueOf(eventAttrRepository.findByEventType(DOTW_ROOM_PULL_SIZE).getEventValue());
        String date = eventAttrRepository.findByEventType(DOTW_ROOM_PULL_DATE).getEventValue();
        String city = eventAttrRepository.findByEventType(DOTW_ROOM_PULL_CITY).getEventValue();
        HotelQuery hotelQuery = new HotelQuery();
        hotelQuery.setCity(city);
        hotelQuery.setIsUpdate("1");
        hotelQuery.setIsUpdateFlag(true);
        hotelQuery.setSyncRoomPriceDate(date);
        Page<HotelInfo> hpage = this.findAllByPageQuery(1, size, hotelQuery);
        return hpage.getContent();
    }

    public void updateBatchHotel(List<HotelInfo> hlist) {
        for (HotelInfo h : hlist) {
            h.setIsUpdate("1");
            hotelInfoRepository.save(h);
        }
    }

    /**
     * 更新房型价格更新日期
     * @param hotelInfo
     * @param fromDate
     */
    public void updateHotelSyncDate(HotelInfo hotelInfo, String fromDate) {
        hotelInfo.setSyncRoomPriceDate(fromDate);
        hotelInfoRepository.save(hotelInfo);
    }

    public String getDotwRoomDate() {
        String date = eventAttrRepository.findByEventType(DOTW_ROOM_PULL_DATE).getEventValue();
        return date;
    }

    /**
     * 更新房型价格同步日期配置，自动加一天
     */
    public void updatePullDateAttr() {
        EventAttr attr = eventAttrRepository.findByEventType(DOTW_ROOM_PULL_DATE);
        DateTime date = DateTime.parse(attr.getEventValue(), DateTimeFormat.forPattern("yyyy-MM-dd"));
        attr.setEventValue(date.plusDays(1).toString("yyyy-MM-dd"));
        eventAttrRepository.save(attr);
    }

    /**
     * 从event_attr中获取酒店价格上浮利率
     * @return
     */
    public Double findHotelPriceRateByEvent() {
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        return Double.valueOf(attr.getEventValue());
    }

    /**
     * 获得酒店附加详细数据
     * @param hotelId
     * @return
     */
    public HotelAdditionalInfo findOneById(String hotelId) {
        return hotelAdditionalInfoRepository.findOneByHotelId(hotelId);
    }

    /**
     * 通过酒店列表id判断酒店附属信息和房型信息本地缓存是否存在
     * @param hotelIds
     * @return
     */
    public Boolean judgeHotelAdditionalAndRoomType(List<String> hotelIds) {
        boolean flag = false;
        for (String id : hotelIds) {
            HotelAdditionalInfo info = hotelAdditionalInfoRepository.findOneByHotelId(id);
            List<RoomType> rlist = roomTypeRepository.findAllByHotelId(id);
            if (info != null && rlist.size() > 0) {
                flag = true;
            }
        }
        return flag;
    }

}
