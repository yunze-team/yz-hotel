package com.yzly.core.service.meit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.*;
import com.yzly.core.domain.dotw.enums.OrderStatus;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.meit.MeitCity;
import com.yzly.core.domain.meit.MeitOrderBookingInfo;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SyncStatus;
import com.yzly.core.enums.meit.PlatformOrderStatusEnum;
import com.yzly.core.repository.HotelSyncListRepository;
import com.yzly.core.repository.dotw.*;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.repository.meit.MeitCityRepository;
import com.yzly.core.repository.meit.MeitOrderBookingInfoRepository;
import com.yzly.core.repository.meit.MeitResultRepository;
import com.yzly.core.repository.meit.MeitTraceLogRepository;
import com.yzly.core.util.SnowflakeIdWorker;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private HotelAdditionalInfoRepository hotelAdditionalInfoRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private EventAttrRepository eventAttrRepository;
    @Autowired
    private MeitOrderBookingInfoRepository meitOrderBookingInfoRepository;
    @Autowired
    private RoomBookingInfoRepository roomBookingInfoRepository;
    @Autowired
    private BookingOrderInfoRepository bookingOrderInfoRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private MeitResultRepository meitResultRepository;
    @Autowired
    private SubOrderRepository subOrderRepository;
    @Autowired
    private RoomPriceDateXmlRepository roomPriceDateXmlRepository;
    @Autowired
    private HotelPriceByDateRepository hotelPriceByDateRepository;

    private static final String MEIT_ROOM_PRICE_RATE = "MEIT_ROOM_PRICE_RATE";

    @Value("${snowflake.workId}")
    private long workId;
    @Value("${snowflake.datacenterId}")
    private long datacenterId;



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
     * 增加或修改美团结果返回日志
     * @param meitResult
     * @return
     */
    public MeitResult addOrUpdateRes(MeitResult meitResult) {
        String req = "";
        if (meitResult.getReqData() != null) {
            req = meitResult.getReqData().toJSONString();
        }
        String resp = "";
        if (meitResult.getData() != null) {
            resp = JSONObject.toJSONString(meitResult.getData());
        }
        meitResult.setReq(req);
        meitResult.setResp(resp);
        return meitResultRepository.save(meitResult);
    }

    public void syncMeitCityToHotel(List<String> ids) {
        for (String hotelId: ids) {
            HotelInfo hotelInfo = hotelInfoRepository.findByDotwHotelCode(hotelId);
            List<MeitCity> mc = meitCityRepository.findAllByNameENLike("%" + hotelInfo.getCity().replaceAll(" ", "") + "%");
            if (mc.size() > 0) {
                hotelInfo.setMeitCityId(mc.get(0).getCityId());
            }
            hotelInfoRepository.save(hotelInfo);
        }
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
            list.add(criteriaBuilder.equal(root.get("syncStatus").as(SyncStatus.class), SyncStatus.AVAILABLE));
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
            if (StringUtils.isNotEmpty(hotelInfo.getMeitCityId())) {
                mh.setCityId(Integer.valueOf(hotelInfo.getMeitCityId()));
            } else {
                List<MeitCity> mc = meitCityRepository.findAllByNameENLike(hotelInfo.getCity().replaceAll(" ", "") + "%");
                mh.setHotelId(hs.getHotelId());
                if (mc.size() > 0) {
                    mh.setCityId(Integer.valueOf(mc.get(0).getCityId()));
                }
            }
            mh.setNameCn(hotelInfo.getHotelNameCn());
            mh.setNameEn(hotelInfo.getHotelName());
            mh.setAddressCn(hotelInfo.getHotelAddressCn());
            mh.setAddressEn(hotelInfo.getHotelAddress());
            mh.setTel(hotelInfo.getReservationTelephone());
            mh.setLongitude(hotelInfo.getLongitude());
            mh.setLatitude(hotelInfo.getLatitude());
            mh.setRegionType(2);
            mlist.add(mh);
            // 注释同步完成后修改状态的方法，不需修改状态
//            hs.setSyncStatus(SyncStatus.SYNCED);
//            hotelSyncListRepository.save(hs);
        }
        return mlist;
    }

    /**
     * 通过ids列表获得酒店扩展信息
     * @param hotelIds
     * @return
     */
    public List<MeitHotelExt> getHotelExtListByIds(String hotelIds) {
        String[] ids = hotelIds.split(",");
        List<MeitHotelExt> mlist = new ArrayList<>();
        for (String id : ids) {
            HotelAdditionalInfo info = hotelAdditionalInfoRepository.findOneByHotelId(id);
            MeitHotelExt hotelExt = new MeitHotelExt();
            if (StringUtils.isEmpty(info.getImages())) {
                continue;
            }
            JSONObject images = JSONObject.parseObject(info.getImages()).getJSONObject("hotelImages");
            List<Img> imgs = new ArrayList<>();
            if (images.getString("@count").equals("1")) {
                JSONObject image = images.getJSONObject("image");
                imgs.add(generateMeitImg(image));
            } else {
                JSONArray image = images.getJSONArray("image");
                for (int i = 0; i < image.size(); i++) {
                    JSONObject io = image.getJSONObject(i);
                    imgs.add(generateMeitImg(io));
                }
            }
            hotelExt.setHotelId(id);
            hotelExt.setImg(imgs);
            MeitHotelExts hotelExts = new MeitHotelExts();
            hotelExts.setHotelExt(hotelExt);
            mlist.add(hotelExt);
        }
        return mlist;
    }

    /**
     * 通过id列表获得房型基础信息
     * @param hotelIds
     * @return
     */
    public List<HotelRoomTypeBasic> getRoomBasicsByIds(String hotelIds) {
        String[] ids = hotelIds.split(",");
        List<HotelRoomTypeBasic> hlist = new ArrayList<>();
        for (String id : ids) {
            HotelRoomTypeBasic hotelRoomTypeBasic = new HotelRoomTypeBasic();
            hotelRoomTypeBasic.setHotelId(id);
            List<RoomType> rlist = roomTypeRepository.findAllByHotelId(id);
            List<RoomTypeBasic> rtypeList = new ArrayList<>();
            for (RoomType r : rlist) {
                RoomTypeBasic rtype = new RoomTypeBasic();
                rtype.setRoomId(r.getRoomTypeCode());
                rtype.setRoomNameEn(r.getName());
                rtypeList.add(rtype);
            }
            hotelRoomTypeBasic.setRoomTypeBasics(rtypeList);
            HotelRoomTypeBasics hotelRoomTypeBasics = new HotelRoomTypeBasics();
            hotelRoomTypeBasics.setHotelRoomTypeBasic(hotelRoomTypeBasic);
            hlist.add(hotelRoomTypeBasic);
        }
        return hlist;
    }

    /**
     * 通过id列表获得房型扩展信息列表
     * @param hotelIds
     * @return
     */
    public List<RoomTypeExtModelList> getRoomExtendsByIds(String hotelIds) {
        String[] ids = hotelIds.split(",");
        List<RoomTypeExtModelList> hlist = new ArrayList<>();
        for (String id : ids) {
            List<RoomType> rlist = roomTypeRepository.findAllByHotelId(id);
            for (RoomType r : rlist) {
                RoomTypeExtModelList re = new RoomTypeExtModelList();
                re.setHotelId(id);
                re.setRoomId(r.getRoomTypeCode());
                hlist.add(re);
            }
        }
        return hlist;
    }

    private Img generateMeitImg(JSONObject image) {
        String imageType = "";
        switch (image.getJSONObject("category").getString("#text")) {
            case "Exterior":
                imageType = "1";
                break;
            case "Hotel Rooms":
                imageType = "9";
                break;
            case "Amenities And Services":
                imageType = "19";
                break;
            case "Lobby":
                imageType = "32";
                break;
            default:
                imageType = "10";
                break;
        }
        Img img = new Img();
        img.setImageOrigin(image.getString("url"));
        img.setImageType(imageType);
        return img;
    }

    public List<Room> assemblyMeitRoomCache(HotelPriceByDate hotelPriceByDate) {
        log.debug("assembly meit room cache start.");
        List<Room> rlist = new ArrayList<>();
        JSONObject roomPriceJson = hotelPriceByDate.getRoomPriceJson();
        Object roomTypeOb = roomPriceJson.getObject("roomType", Object.class);
        if (roomTypeOb instanceof ArrayList) {
            JSONArray roomTypeArray = JSONArray.parseArray(JSON.toJSONString(roomTypeOb));
            for (int i = 0; i < roomTypeArray.size(); i++) {
                JSONObject roomTypeObject = roomTypeArray.getJSONObject(i);
                rlist.addAll(executeRoomTypeJson(roomTypeObject, hotelPriceByDate.getHotelCode(),
                        hotelPriceByDate.getFromDate(), hotelPriceByDate.getToDate()));
            }
        } else {
            JSONObject roomTypeObject = JSONObject.parseObject(JSON.toJSONString(roomTypeOb));
            rlist.addAll(executeRoomTypeJson(roomTypeObject, hotelPriceByDate.getHotelCode(),
                    hotelPriceByDate.getFromDate(), hotelPriceByDate.getToDate()));
        }
        return rlist;
    }

    private List<Room> executeRoomTypeJson(JSONObject roomTypeJson, String hotelId, String fromDate, String toDate) {
        List<Room> rlist = new ArrayList<>();
        JSONObject rateBasesObject = roomTypeJson;
        String roomName = rateBasesObject.getString("name");
        String roomTypeCOde = rateBasesObject.getString("@roomtypecode");
        Object rateBases = rateBasesObject.getObject("rateBases", Object.class);
        if (rateBases instanceof JSONObject) {
            JSONObject rateJSON = ((JSONObject) rateBases).getJSONObject("rateBasis");
            Room room = buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
            rlist.add(room);
        } else if (rateBases instanceof JSONArray) {
            JSONArray rateArray = (JSONArray) rateBases;
            for (int k = 0; k < rateArray.size(); k++) {
                JSONObject rateJSON = rateArray.getJSONObject(k);
                Room room = buildRoomPriceByJSON(rateJSON, hotelId, roomName, roomTypeCOde, fromDate, toDate);
                rlist.add(room);
            }
        }
        return rlist;
    }

    private Room buildRoomPriceByJSON(JSONObject rateJSON, String hotelId, String roomName,
                                                  String roomTypeCode, String fromDate, String toDate) {
        log.debug("assembly meit room cache start.");
        Room room = new Room();
        String rateBasisId = rateJSON.getString("@id");
        // 判断房型是否还有早餐
        Breakfast breakfast = new Breakfast();
        if (!rateBasisId.equals("1331")) {
            breakfast.setCount(0);
        } else {
            breakfast.setCount(2);
        }
        List<DayInfo> dayInfos = new ArrayList<>();
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        DayInfo dayInfo = generateDayInfoByDate(fromDate, rateJSON.getString("total"), priceRate);
        dayInfos.add(dayInfo);
        room.setDayInfos(dayInfos);
        room.setBreakfast(breakfast);
        room.setRoomId(roomTypeCode);
        room.setRoomName(roomName);
        room.setRatePlanCode(rateBasisId);
        HotelAdditionalInfo hotelAdditionalInfo = hotelAdditionalInfoRepository.findOneByHotelId(hotelId);
        // 可能为空，判断一下
        if (hotelAdditionalInfo != null) {
            room.setCheckInTime(hotelAdditionalInfo.getHotelCheckIn());
            room.setCheckOutTime(hotelAdditionalInfo.getHotelCheckOut());
        }
        List<RefundRule> refundRules = new ArrayList<>();
        // 所有给美团的房型，都显示为不可取消
        RefundRule refundRule = new RefundRule();
        refundRule.setReturnable(false);
        refundRule.setMaxHoursBeforeCheckIn(null);
        refundRule.setMinHoursBeforeCheckIn(0);
        refundRules.add(refundRule);
        room.setRefundRules(refundRules);
        log.debug("assembly meit room cache end.");
        return room;
    }

    /**
     * 通过pricedataxml缓存封装美团room视图
     * @param priceDateXml
     * @return
     */
    public Room assemblyMeitRoomCash(RoomPriceDateXml priceDateXml) {
        log.debug("assembly meit room cash start.");
        Room room = new Room();
        Breakfast breakfast = new Breakfast();
        // 判断房型是否还有早餐
        if (!priceDateXml.getRateBasisId().equals("1331")) {
            breakfast.setCount(0);
        } else {
            breakfast.setCount(2);
        }
        List<DayInfo> dayInfos = new ArrayList<>();
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        DayInfo dayInfo = generateDayInfoByDate(priceDateXml.getFromDate(), priceDateXml.getTotal(), priceRate);
        dayInfos.add(dayInfo);
        room.setDayInfos(dayInfos);
        room.setBreakfast(breakfast);
        room.setRoomId(priceDateXml.getRoomTypeCode());
        room.setRoomName(priceDateXml.getRoomName());
        room.setRatePlanCode(priceDateXml.getRateBasisId());
        HotelAdditionalInfo hotelAdditionalInfo = hotelAdditionalInfoRepository.findOneByHotelId(priceDateXml.getHotelCode());
        // 可能为空，判断一下
        if (hotelAdditionalInfo != null) {
            room.setCheckInTime(hotelAdditionalInfo.getHotelCheckIn());
            room.setCheckOutTime(hotelAdditionalInfo.getHotelCheckOut());
        }
        List<RefundRule> refundRules = new ArrayList<>();
        // 所有给美团的房型，都显示为不可取消
        RefundRule refundRule = new RefundRule();
        refundRule.setReturnable(false);
        refundRule.setMaxHoursBeforeCheckIn(null);
        refundRule.setMinHoursBeforeCheckIn(0);
        refundRules.add(refundRule);
        room.setRefundRules(refundRules);
        log.debug("assembly meit room cash end.");
        return room;
    }

    /**
     * 通过roombookinginfo实体封装美团room视图
     * @param roomBookingInfo
     * @return
     */
    public Room assemblyMeitRoom(RoomBookingInfo roomBookingInfo) {
        log.debug("assembly meit room start.");
        Room room = new Room();
        Breakfast breakfast = new Breakfast();
        // 判断房型是否还有早餐
        if (!roomBookingInfo.getRateBasisId().equals("1331")) {
            breakfast.setCount(0);
        } else {
            breakfast.setCount(2);
        }
        JSONObject dates = JSONObject.parseObject(roomBookingInfo.getDates());
        List<DayInfo> dayInfos = new ArrayList<>();
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        if (dates.getString("@count").equals("1")) {
            JSONObject date = dates.getJSONObject("date");
            DayInfo dayInfo = generateDayInfoByJson(date, priceRate);
            dayInfos.add(dayInfo);
        } else {
            JSONArray dateArray = dates.getJSONArray("date");
            for (int i = 0; i < dateArray.size(); i++) {
                JSONObject date = dateArray.getJSONObject(i);
                DayInfo dayInfo = generateDayInfoByJson(date, priceRate);
                dayInfos.add(dayInfo);
            }
        }
        room.setDayInfos(dayInfos);
        room.setBreakfast(breakfast);
        room.setRoomId(roomBookingInfo.getRoomTypeCode());
        room.setRoomName(roomBookingInfo.getName());
        room.setRatePlanCode(roomBookingInfo.getAllocationDetails());
        HotelAdditionalInfo hotelAdditionalInfo = hotelAdditionalInfoRepository.findOneByHotelId(roomBookingInfo.getHotelId());
        // 可能为空，判断一下
        if (hotelAdditionalInfo != null) {
            room.setCheckInTime(hotelAdditionalInfo.getHotelCheckIn());
            room.setCheckOutTime(hotelAdditionalInfo.getHotelCheckOut());
        }
        List<RefundRule> refundRules = new ArrayList<>();
        // 退订规则组装
        //JSONObject cancellationRules = JSONObject.parseObject(roomBookingInfo.getCancellationRules());
        // 所有给美团的房型，都显示为不可取消
        RefundRule refundRule = new RefundRule();
        refundRule.setReturnable(false);
        refundRule.setMaxHoursBeforeCheckIn(null);
        refundRule.setMinHoursBeforeCheckIn(0);
        refundRules.add(refundRule);
        // 注释同步给美团按照dotw的取消规则封装的数据
//        if (cancellationRules.getString("@count").equals("1")) {
//            RefundRule refundRule = new RefundRule();
//            refundRule.setReturnable(false);
//            refundRules.add(refundRule);
//        } else {
//            JSONArray cancelArray = cancellationRules.getJSONArray("rule");
//            for (int i = 0; i < cancelArray.size(); i++) {
//                JSONObject rule = cancelArray.getJSONObject(i);
//                if (rule.getString("noShowPolicy") != null) {
//                    continue;
//                }
//                RefundRule refundRule = new RefundRule();
//                try {
//                    if (rule.getString("cancelRestricted") != null) {
//                        refundRule.setReturnable(false);
//                    } else {
//                        BigDecimal cancelCharge = new BigDecimal(rule.getJSONObject("cancelCharge").getString("#text"));
//                        if (cancelCharge.compareTo(new BigDecimal(0)) > 0) {
//                            refundRule.setReturnable(true);
//                            refundRule.setRefundType(1);
//                            refundRule.setFine(cancelCharge
//                                    .multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
//                        } else {
//                            refundRule.setReturnable(true);
//                            refundRule.setRefundType(0);
//                        }
//                    }
//                    DateTime hotelCheckIn = DateTime.parse(roomBookingInfo.getFromDate() + " " + room.getCheckInTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"));
//                    if (StringUtils.isNotEmpty(rule.getString("fromDate"))) {
//                        DateTime fromDate = DateTime.parse(rule.getString("fromDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).minusDays(2).minusSeconds(1);
//                        if (i == 0 && fromDate.isBefore(System.currentTimeMillis())) {
//                            refundRule.setFromDate(null);
//                        } else {
//                            refundRule.setFromDate(fromDate.toString("yyyy-MM-dd HH:mm:ss"));
//                            long diff = hotelCheckIn.getMillis() - fromDate.getMillis();
//                            long days = diff / (1000 * 24 * 60 * 60);
//                            long maxCheckInTime = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + days * 24;
//                            Integer maxCheckIn = maxCheckInTime > 0 ? Integer.valueOf(String.valueOf(maxCheckInTime)) : 0;
//                            refundRule.setMaxHoursBeforeCheckIn(maxCheckIn);
//                        }
//                    } else {
//                        refundRule.setMaxHoursBeforeCheckIn(null);
//                    }
//                    if (StringUtils.isNotEmpty(rule.getString("toDate"))) {
//                        DateTime toDate = DateTime.parse(rule.getString("toDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).minusDays(2);
//                        refundRule.setToDate(toDate.toString("yyyy-MM-dd HH:mm:ss"));
//                        long diff = hotelCheckIn.getMillis() - toDate.getMillis();
//                        long days = diff / (1000 * 24 * 60 * 60);
//                        long minCheckInTime = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + days * 24;
//                        Integer minCheckIn = minCheckInTime > 0 ? Integer.valueOf(String.valueOf(minCheckInTime)) : 0;
//                        refundRule.setMinHoursBeforeCheckIn(minCheckIn);
//                    } else {
//                        refundRule.setMinHoursBeforeCheckIn(0);
//                    }
//                } catch (Exception e) {
//                    log.error(e.getMessage());
//                    refundRule.setReturnable(false);
//                }
//                refundRules.add(refundRule);
//            }
//        }
        room.setRefundRules(refundRules);
        log.debug("assembly meit room end.");
        return room;
    }

    private DayInfo generateDayInfoByJson(JSONObject date, Double priceRate) {
        DayInfo dayInfo = new DayInfo();
        BigDecimal basePrice = new BigDecimal(date.getJSONObject("price").getString("#text"));
        BigDecimal finalPrice = basePrice.multiply(new BigDecimal(100)).multiply(new BigDecimal(1 + priceRate));
        dayInfo.setBasePrice(finalPrice.setScale(0, RoundingMode.HALF_UP).intValue());
        dayInfo.setDate(date.getString("@datetime"));
        dayInfo.setStatus(1);
        dayInfo.setCounts(1);
        return dayInfo;
    }

    private DayInfo generateDayInfoByDate(String fromDate, String total, Double priceRate) {
        DayInfo dayInfo = new DayInfo();
        BigDecimal basePrice = new BigDecimal(total);
        BigDecimal finalPrice = basePrice.multiply(new BigDecimal(100)).multiply(new BigDecimal(1 + priceRate));
        dayInfo.setBasePrice(finalPrice.setScale(0, RoundingMode.HALF_UP).intValue());
        dayInfo.setDate(fromDate);
        dayInfo.setStatus(1);
        dayInfo.setCounts(1);
        return dayInfo;
    }

    /**
     * 创建美团订单入库
     * @param orderCreateParam
     * @return
     */
    public MeitOrderBookingInfo createMeitOrder(OrderCreateParam orderCreateParam) {
        MeitOrderBookingInfo orderBookingInfo = new MeitOrderBookingInfo();
        orderBookingInfo.setHotelId(orderCreateParam.getHotelId());
        orderBookingInfo.setRoomId(orderCreateParam.getRoomId());
        orderBookingInfo.setRatePlanCode(orderCreateParam.getRatePlanCode());
        orderBookingInfo.setCheckin(orderCreateParam.getCheckin());
        orderBookingInfo.setCheckout(orderCreateParam.getCheckout());
        orderBookingInfo.setNumberOfAdults(orderCreateParam.getNumberOfAdults());
        orderBookingInfo.setNumberOfChildren(orderCreateParam.getNumberOfChildren());
        orderBookingInfo.setChildrenAges(orderCreateParam.getChildrenAges());
        orderBookingInfo.setRoomNum(orderCreateParam.getRoomNum());
        orderBookingInfo.setTotalPrice(orderCreateParam.getTotalPrice());
        orderBookingInfo.setCurrencyCode(orderCreateParam.getCurrencyCode());
        orderBookingInfo.setGuestInfo(JSONArray.toJSONString(orderCreateParam.getGuestInfo()));
        orderBookingInfo.setOrderInfo(JSONObject.toJSONString(orderCreateParam.getOrderInfo()));
        orderBookingInfo.setCreditCard(JSONObject.toJSONString(orderCreateParam.getCreditCard()));
        orderBookingInfo.setPartnerOrderId(String.valueOf(new SnowflakeIdWorker(workId, datacenterId).nextId()));
        orderBookingInfo.setOrderId(orderCreateParam.getOrderInfo().getOrderId());
        orderBookingInfo.setOrderStatus(PlatformOrderStatusEnum.BOOKING);
        orderBookingInfo.setActualTotalPrice(Integer.valueOf(orderCreateParam.getTotalPrice()));
        RoomBookingInfo roomBookingInfo = roomBookingInfoRepository.findByRoomTypeCodeAndAllocationDetails(orderCreateParam.getRoomId(),
                orderCreateParam.getRatePlanCode());
        if (roomBookingInfo != null) {
            orderBookingInfo.setRateBaseId(roomBookingInfo.getRateBasisId());
        }
        orderBookingInfo = meitOrderBookingInfoRepository.save(orderBookingInfo);
        return orderBookingInfo;
    }

    /**
     * 更新订单状态为失败
     * @param orderId
     * @return
     */
    public MeitOrderBookingInfo updateOrderFail(String orderId) {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findByOrderId(Long.valueOf(orderId));
        meitOrder.setOrderStatus(PlatformOrderStatusEnum.BOOK_FAIL);
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 根据美团订单号获得美团订单
     * @param orderId
     * @return
     */
    public MeitOrderBookingInfo getOrderByOrderId(String orderId) {
        List<MeitOrderBookingInfo> mlist = meitOrderBookingInfoRepository.findAllByOrderId(Long.valueOf(orderId));
        return mlist.get(0);
    }

    /**
     * 根据美团orderid保存订单表
     * @param orderId
     * @return
     * @throws Exception
     */
    public BookingOrderInfo saveBookingByMeitOrder(String orderId) throws Exception {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findByOrderId(Long.valueOf(orderId));
        String allocationDetails = meitOrder.getRatePlanCode();
        List<Passenger> plist = new ArrayList<>();
        JSONArray guestArray = JSONArray.parseArray(meitOrder.getGuestInfo());
        for (int i = 0; i < guestArray.size(); i++) {
            String salutaionCode;
            String gender = guestArray.getJSONObject(i).getString("gender");
            if (StringUtils.isEmpty(gender)) {
                salutaionCode = "3801";
            } else {
                if (gender.toLowerCase().equals("male")) {
                    salutaionCode = "1328";
                } else if (gender.toLowerCase().equals("female")) {
                    salutaionCode = "15134";
                } else if (gender.toLowerCase().equals("child")) {
                    salutaionCode = "14632";
                } else {
                    salutaionCode = "3801";
                }
            }
            Passenger passenger = new Passenger(salutaionCode,
                    guestArray.getJSONObject(i).getString("firstName"),
                    guestArray.getJSONObject(i).getString("lastName"));
            passenger.setRoomSeq(Integer.valueOf(guestArray.getJSONObject(i).getString("roomSeq")));
            passenger.setSeq(Integer.valueOf(guestArray.getJSONObject(i).getString("seq")));
            plist.add(passenger);
        }
        // 加入儿童信息
        for (int i = 0; i < meitOrder.getNumberOfChildren(); i++) {
            Passenger passenger = new Passenger("14632",
                    "TBA",
                    "TBA");
            passenger.setRoomSeq(0);
            passenger.setSeq(0);
            plist.add(passenger);
        }
        BookingOrderInfo bookingOrderInfo = bookingOrderInfoRepository.findByAllocationDetails(allocationDetails);
        if (bookingOrderInfo == null) {
            bookingOrderInfo = new BookingOrderInfo();
        }
        RoomBookingInfo roomBookingInfo = roomBookingInfoRepository.findByRoomTypeCodeAndAllocationDetails(meitOrder.getRoomId(),
                meitOrder.getRatePlanCode());
        if (roomBookingInfo == null) {
            throw new Exception("room is null");
        }
        bookingOrderInfo.setHotelId(roomBookingInfo.getHotelId());
        bookingOrderInfo.setRoomTypeCode(roomBookingInfo.getRoomTypeCode());
        bookingOrderInfo.setAllocationDetails(allocationDetails);
        bookingOrderInfo.setSelectedRateBasis(roomBookingInfo.getRateBasisId());
        bookingOrderInfo.setActualAdults(String.valueOf(meitOrder.getNumberOfAdults()));
        bookingOrderInfo.setChildren(String.valueOf(meitOrder.getNumberOfChildren()));
        bookingOrderInfo.setChildrenAges(meitOrder.getChildrenAges());
        bookingOrderInfo.setPassengerNationality("168");
        bookingOrderInfo.setFromDate(meitOrder.getCheckin());
        bookingOrderInfo.setToDate(meitOrder.getCheckout());
        bookingOrderInfo.setPassengerInfos(JSONObject.toJSONString(plist));
        bookingOrderInfo.setCurrency(currencyRepository.findByName(meitOrder.getCurrencyCode()).getCode());
        bookingOrderInfo.setOrderStatus(OrderStatus.SAVED);
        bookingOrderInfo.setRoomNum(meitOrder.getRoomNum());
        bookingOrderInfo.setOrderId(String.valueOf(meitOrder.getOrderId()));
        return bookingOrderInfoRepository.save(bookingOrderInfo);
    }

    /**
     * 根据dotw订单信息更新美团订单信息
     * @param orderId
     * @param bookingOrderInfo
     * @return
     */
    public MeitOrderBookingInfo updateOrderByBookingInfo(String orderId, BookingOrderInfo bookingOrderInfo) {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findByOrderId(Long.valueOf(orderId));
        meitOrder.setOrderAvailable("0");
        meitOrder.setDotwOrderId(bookingOrderInfo.getId());
        meitOrder.setAgentOrderId(bookingOrderInfo.getBookingCode());
        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(bookingOrderInfo.getId());
        BigDecimal basePrice = new BigDecimal(0);
        for (SubOrder subOrder : subOrders) {
            basePrice = basePrice.add(new BigDecimal(subOrder.getPriceValue()));
        }
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        BigDecimal totalPrice = basePrice.multiply(new BigDecimal(100)).multiply(new BigDecimal(1 + priceRate));
        meitOrder.setActualTotalPrice(totalPrice.setScale(0, RoundingMode.HALF_UP).intValue());
        meitOrder.setOrderStatus(PlatformOrderStatusEnum.BOOK_SUCCESS);
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 根据房间确认号和总金额更新美团订单
     * @param orderId
     * @param confirmNumbers
     * @param totalPrice
     * @return
     */
    public MeitOrderBookingInfo updateOrderByManual(String orderId, String confirmNumbers, String totalPrice) {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findByOrderId(Long.valueOf(orderId));
        meitOrder.setOrderAvailable("1");
        BigDecimal basePrice = new BigDecimal(totalPrice);
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        BigDecimal totalP = basePrice.multiply(new BigDecimal(1 + priceRate));
        meitOrder.setActualTotalPrice(totalP.setScale(0, RoundingMode.HALF_UP).intValue());
        meitOrder.setConfirmationNumbers(confirmNumbers);
        meitOrder.setOrderStatus(PlatformOrderStatusEnum.BOOK_SUCCESS);
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 通过美团订单id获得dotw订单详细
     * @param orderId
     * @return
     */
    public BookingOrderInfo getBookingByMeitOrder(String orderId) {
        return bookingOrderInfoRepository.findByOrderId(orderId);
    }

    /**
     * 更新美团订单的取消状态
     * @param meitOrder
     * @param penalty
     * @return
     */
    public MeitOrderBookingInfo updateCancelOrder(MeitOrderBookingInfo meitOrder, Integer penalty) {
        meitOrder.setPenalty(penalty);
        meitOrder.setOrderStatus(PlatformOrderStatusEnum.CANCEL_SUCCESS);
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 按照酒店ids和日期，给出缓存的房型价格缓存数据
     * @param hotelIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public Map<String, List<RoomPriceDateXml>> findAllByGoodsSearch(String hotelIds, String fromDate, String toDate) {
        String[] hotelIdsArray = hotelIds.split(",");
        Map<String, List<RoomPriceDateXml>> rMap = new HashMap<>();
        for (String hotelId : hotelIdsArray) {
            List<RoomPriceDateXml> roomList = roomPriceDateXmlRepository.findAllByHotelCodeAndFromDateAndToDate(hotelId, fromDate, toDate);
            rMap.put(hotelId, roomList);
        }
        return rMap;
    }

    public Map<String, HotelPriceByDate> findHotelPriceByGoodsSearch(String hotelIds, String fromDate, String toDate) {
        String[] hotelIdsArray = hotelIds.split(",");
        Map<String, HotelPriceByDate> rmap = new HashMap<>();
        for (String hotelId : hotelIdsArray) {
            HotelPriceByDate hotelPriceByDate = hotelPriceByDateRepository.findByHotelCodeAndFromDateAndToDate(hotelId, fromDate, toDate);
            rmap.put(hotelId, hotelPriceByDate);
        }
        return rmap;
    }

}
