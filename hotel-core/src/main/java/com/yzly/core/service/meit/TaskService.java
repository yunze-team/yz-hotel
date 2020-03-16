package com.yzly.core.service.meit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.HotelManualSyncList;
import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.HotelRoomPriceXml;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import com.yzly.core.domain.dotw.UserHotelRoomPriceXml;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.GoodsSearchQuery;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SyncStatus;
import com.yzly.core.repository.HotelManualSyncListRepository;
import com.yzly.core.repository.HotelSyncListRepository;
import com.yzly.core.repository.dotw.*;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.repository.meit.MeitResultRepository;
import com.yzly.core.repository.meit.MeitTraceLogRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/1/15
 * @desc
 **/
@Service
@CommonsLog
public class TaskService {

    @Autowired
    private HotelManualSyncListRepository hotelManualSyncListRepository;
    @Autowired
    private RoomPriceByDateRepository roomPriceByDateRepository;
    @Autowired
    private RateBasisRepository rateBasisRepository;
    @Autowired
    private HotelRoomPriceXmlRepository hotelRoomPriceXmlRepository;
    @Autowired
    private EventAttrRepository eventAttrRepository;
    @Autowired
    private HotelSyncListRepository hotelSyncListRepository;
    @Autowired
    private RoomBookingInfoRepository roomBookingInfoRepository;
    @Autowired
    private UserHotelRoomPriceXmlRepository userHotelRoomPriceXmlRepository;
    @Autowired
    private DotwXmlLogRepository dotwXmlLogRepository;
    @Autowired
    private MeitResultRepository meitResultRepository;
    @Autowired
    private MeitTraceLogRepository meitTraceLogRepository;

    private static final String MEIT_ROOM_PRICE_PULL_SIZE = "MEIT_ROOM_PRICE_PULL_SIZE";

    private static final String DOTW_HOTEL_PULL_SIZE = "DOTW_HOTEL_PULL_SIZE";

    public void delAllUserPrice() {
        userHotelRoomPriceXmlRepository.deleteAll();
    }

    /**
     * 按照参数保留天数，删除美团日志
     * @param days
     */
    public void delMeitLogByDays(int days) {
        Date day = DateTime.now().minusDays(days).toDate();
//        List<MeitResult> mlist = meitResultRepository.findAllByCreatedAtBefore(day);
//        List<MeitTraceLog> tlist = meitTraceLogRepository.findAllByCreatedAtBefore(day);
        meitResultRepository.deleteAllByCreatedAtBefore(day);
        meitTraceLogRepository.deleteAllByCreatedAtBefore(day);
    }

    /**
     * 删除说有的dotw_xml_log日志
     */
    public void delAllDotwXmlLog() {
        dotwXmlLogRepository.deleteAll();
    }

    /**
     * 清空所有的dotw_room_booking_info缓存
     */
    public void delAllRoomBookInfo() {
        roomBookingInfoRepository.deleteAll();
    }

    /**
     * 获得所有的需要手工同步的酒店ids
     * @return
     */
    public String getManualSyncHotelIds() {
        String ids = "";
        EventAttr attr = eventAttrRepository.findByEventType(DOTW_HOTEL_PULL_SIZE);
        Integer pullSize = Integer.valueOf(attr.getEventValue());
        Pageable pageable = new PageRequest(0, pullSize);
        Page<HotelSyncList> hpage = hotelSyncListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(criteriaBuilder.equal(root.get("distributor").as(DistributorEnum.class), DistributorEnum.MEIT));
            list.add(criteriaBuilder.equal(root.get("syncStatus").as(SyncStatus.class), SyncStatus.AVAILABLE));
            list.add(criteriaBuilder.equal(root.get("taskStatus").as(Integer.class), 0));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
        List<HotelSyncList> hlist = hpage.getContent();
        log.info("getManualSyncHotelIds:" + hlist);
        for (int i = 0; i < hlist.size(); i++) {
            if (i != hlist.size() - 1) {
                ids += hlist.get(i).getHotelId() + ",";
            } else {
                ids += hlist.get(i).getHotelId();
            }
            HotelSyncList hs = hlist.get(i);
            hs.setTaskStatus(1);
            hotelSyncListRepository.save(hs);
        }
        return ids;
    }

    /**
     * 根据日期和dotw返回的报文，入库或更新roomprice
     * @param priceObject
     * @param fromDate
     * @param toDate
     */
    public void syncRoomPriceByDate(JSONObject priceObject, String fromDate, String toDate) {
        JSONArray hotelArray = priceObject.getJSONArray("hotels");
        for (int i = 0; i < hotelArray.size(); i++) {
            JSONObject hotelObject = hotelArray.getJSONObject(i);
            String hotelId = hotelObject.getString("@hotelid");
            Object roomTypeObject = hotelObject.getJSONObject("rooms").getJSONObject("room").getObject("roomType", Object.class);
            if (roomTypeObject instanceof JSONArray) {
                JSONArray roomTypeArray = (JSONArray) roomTypeObject;
                for (int j = 0; j < roomTypeArray.size(); j++) {
                    JSONObject rateBasesObject = roomTypeArray.getJSONObject(j);
                    executeRoomTypeJson(rateBasesObject, hotelId, fromDate, toDate);
                }
            } else if (roomTypeObject instanceof JSONObject) {
                JSONObject roomTypeJson = (JSONObject) roomTypeObject;
                executeRoomTypeJson(roomTypeJson, hotelId, fromDate, toDate);
            }
        }
    }

    /**
     * 处理返回的roomtype报文
     * @param roomTypeJson
     * @param hotelId
     * @param fromDate
     * @param toDate
     */
    private void executeRoomTypeJson(JSONObject roomTypeJson, String hotelId, String fromDate, String toDate) {
        JSONObject rateBasesObject = roomTypeJson;
        String roomName = rateBasesObject.getString("name");
        String roomTypeCOde = rateBasesObject.getString("@roomtypecode");
        Object rateBases = rateBasesObject.getObject("rateBases", Object.class);
        if (rateBases instanceof JSONObject) {
            JSONObject rateJSON = ((JSONObject) rateBases).getJSONObject("rateBasis");
            buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
        } else if (rateBases instanceof JSONArray) {
            JSONArray rateArray = (JSONArray) rateBases;
            for (int k = 0; k < rateArray.size(); k++) {
                JSONObject rateJSON = rateArray.getJSONObject(k);
                buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
            }
        }
    }

    /**
     * 根据解析的报文，入库或更新roomprice
     * @param rateJSON
     * @param hotelId
     * @param roomName
     * @param roomTypeCode
     * @param fromDate
     * @param toDate
     * @return
     */
    private RoomPriceByDate buildRoomPriceByJSON(JSONObject rateJSON, String hotelId, String roomName,
                                                 String roomTypeCode, String fromDate, String toDate) {
        String rateBasis = rateBasisRepository.findByCode(rateJSON.getString("@id")).getName();
        RoomPriceByDate roomPrice = roomPriceByDateRepository.findByRoomTypeCodeAndRateBasisAndFromDateAndToDate(roomTypeCode, rateBasis, fromDate, toDate);
        if (roomPrice == null) {
            roomPrice = new RoomPriceByDate();
        }
        roomPrice.setHotelCode(hotelId);
        roomPrice.setFromDate(fromDate);
        roomPrice.setToDate(toDate);
        roomPrice.setRoomName(roomName);
        roomPrice.setRoomTypeCode(roomTypeCode);
        roomPrice.setTotal(rateJSON.getString("total"));
        roomPrice.setRateBasis(rateBasis);
        return roomPriceByDateRepository.save(roomPrice);
    }

    /**
     * 情况所有的房型价格数据
     */
    public void delAllRoomPrice() {
        roomPriceByDateRepository.deleteAllInBatch();
    }

    /**
     * 组装price实体参数
     * @param resp
     * @param goodsSearchQuery
     * @param hotelId
     * @return
     */
    private HotelRoomPriceXml generatePriceXmlByGoodsSearchQuery(String resp, GoodsSearchQuery goodsSearchQuery, String hotelId) {
        HotelRoomPriceXml hotelRoomPriceXml = new HotelRoomPriceXml();
        hotelRoomPriceXml.setHotelId(hotelId);
        hotelRoomPriceXml.setFromDate(goodsSearchQuery.getCheckin());
        hotelRoomPriceXml.setToDate(goodsSearchQuery.getCheckout());
        hotelRoomPriceXml.setCurrency(goodsSearchQuery.getCurrencyCode());
        hotelRoomPriceXml.setNumberOfAdults(goodsSearchQuery.getNumberOfAdults() == null ? "2" : String.valueOf(goodsSearchQuery.getNumberOfAdults()));
        hotelRoomPriceXml.setRoomNumber(goodsSearchQuery.getRoomNumber() == null ? "1" : String.valueOf(goodsSearchQuery.getRoomNumber()));
        String childrenNum = goodsSearchQuery.getNumberOfChildren() == null ? "0" : String.valueOf(goodsSearchQuery.getNumberOfChildren());
        hotelRoomPriceXml.setNumberOfChildren(childrenNum);
        if (!childrenNum.equals("0")) {
            hotelRoomPriceXml.setChildrenAges(goodsSearchQuery.getChildrenAges());
        }
        if (StringUtils.isNotEmpty(resp)) {
            hotelRoomPriceXml.setXmlResp(resp);
        }
        return hotelRoomPriceXml;
    }

    private UserHotelRoomPriceXml generateUserPrcieByQuery(String resp, GoodsSearchQuery goodsSearchQuery, String hotelId) {
        UserHotelRoomPriceXml hotelRoomPriceXml = new UserHotelRoomPriceXml();
        hotelRoomPriceXml.setHotelId(hotelId);
        hotelRoomPriceXml.setFromDate(goodsSearchQuery.getCheckin());
        hotelRoomPriceXml.setToDate(goodsSearchQuery.getCheckout());
        hotelRoomPriceXml.setCurrency(goodsSearchQuery.getCurrencyCode());
        hotelRoomPriceXml.setNumberOfAdults(goodsSearchQuery.getNumberOfAdults() == null ? "2" : String.valueOf(goodsSearchQuery.getNumberOfAdults()));
        hotelRoomPriceXml.setRoomNumber(goodsSearchQuery.getRoomNumber() == null ? "1" : String.valueOf(goodsSearchQuery.getRoomNumber()));
        String childrenNum = goodsSearchQuery.getNumberOfChildren() == null ? "0" : String.valueOf(goodsSearchQuery.getNumberOfChildren());
        hotelRoomPriceXml.setNumberOfChildren(childrenNum);
        if (!childrenNum.equals("0")) {
            hotelRoomPriceXml.setChildrenAges(goodsSearchQuery.getChildrenAges());
        }
        if (StringUtils.isNotEmpty(resp)) {
            hotelRoomPriceXml.setXmlResp(resp);
        }
        return hotelRoomPriceXml;
    }

    /**
     * 通过美团查询参数和dotw返回xml结果来保存房型价格数据
     * @param resp
     * @param goodsSearchQuery
     * @param hotelId
     * @return
     */
    public HotelRoomPriceXml addRoomPrice(String resp, GoodsSearchQuery goodsSearchQuery, String hotelId) {
        HotelRoomPriceXml hotelRoomPriceXml = this.generatePriceXmlByGoodsSearchQuery(resp, goodsSearchQuery, hotelId);
        return hotelRoomPriceXmlRepository.insert(hotelRoomPriceXml);
    }

    public UserHotelRoomPriceXml addUserRoomPrice(String resp, GoodsSearchQuery goodsSearchQuery, String hotelId) {
        UserHotelRoomPriceXml hotelRoomPriceXml = this.generateUserPrcieByQuery(resp, goodsSearchQuery, hotelId);
        return userHotelRoomPriceXmlRepository.insert(hotelRoomPriceXml);
    }

    /**
     * 通过参数查询price实体
     * @param goodsSearchQuery
     * @param hotelId
     * @return
     */
    public List<HotelRoomPriceXml> findPriceByQuery(GoodsSearchQuery goodsSearchQuery, String hotelId) {
        HotelRoomPriceXml hotelRoomPriceXml = this.generatePriceXmlByGoodsSearchQuery(null, goodsSearchQuery, hotelId);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "xmlResp")
                .withIgnoreCase().withIgnoreNullValues();
        Example<HotelRoomPriceXml> priceXmlExample = Example.of(hotelRoomPriceXml, exampleMatcher);
        return hotelRoomPriceXmlRepository.findAll(priceXmlExample);
    }

    public List<UserHotelRoomPriceXml> findUserPriceByQuery(GoodsSearchQuery goodsSearchQuery, String hotelId) {
        UserHotelRoomPriceXml hotelRoomPriceXml = this.generateUserPrcieByQuery(null, goodsSearchQuery, hotelId);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "xmlResp")
                .withIgnoreCase().withIgnoreNullValues();
        Example<UserHotelRoomPriceXml> priceXmlExample = Example.of(hotelRoomPriceXml, exampleMatcher);
        return userHotelRoomPriceXmlRepository.findAll(priceXmlExample);
    }

    /**
     * 通过配置表的数量配置，获得酒店同步列表的酒店ids
     * @return
     */
    public List<String> findSyncHotelIdsByAttr() {
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_PULL_SIZE);
        Integer pullSize = Integer.valueOf(attr.getEventValue());
        Pageable pageable = new PageRequest(0, pullSize);
        Page<HotelSyncList> hpage = hotelSyncListRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(criteriaBuilder.equal(root.get("distributor").as(DistributorEnum.class), DistributorEnum.MEIT));
            list.add(criteriaBuilder.equal(root.get("syncStatus").as(SyncStatus.class), SyncStatus.AVAILABLE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
        List<HotelSyncList> hlist = hpage.getContent();
        List<String> ids = new ArrayList<>();
        for (HotelSyncList h : hlist) {
            ids.add(h.getHotelId());
        }
        return ids;
    }

    /**
     * 删除所有的房型价格数据
     */
    public void delAllRoomPriceXml() {
        hotelRoomPriceXmlRepository.deleteAll();
    }

    public void delRoomPriceXmlList(List<HotelRoomPriceXml> hlist) {
        for (HotelRoomPriceXml h : hlist) {
            hotelRoomPriceXmlRepository.delete(h);
        }
    }

}
