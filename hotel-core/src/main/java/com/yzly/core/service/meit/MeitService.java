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
            List<MeitCity> mc = meitCityRepository.findAllByNameENLike("%" + hotelInfo.getCity().replaceAll(" ", "") + "%");
            mh.setHotelId(hs.getHotelId());
            if (mc.size() > 0) {
                mh.setCityId(Integer.valueOf(mc.get(0).getCityId()));
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
            hs.setSyncStatus(SyncStatus.SYNCED);
            hotelSyncListRepository.save(hs);
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

    /**
     * 通过roombookinginfo实体封装美团room视图
     * @param roomBookingInfo
     * @return
     */
    public Room assemblyMeitRoom(RoomBookingInfo roomBookingInfo) {
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
        room.setCheckInTime(hotelAdditionalInfo.getHotelCheckIn());
        room.setCheckOutTime(hotelAdditionalInfo.getHotelCheckOut());
        List<RefundRule> refundRules = new ArrayList<>();
        // 退订规则组装
        JSONObject cancellationRules = JSONObject.parseObject(roomBookingInfo.getCancellationRules());
        if (cancellationRules.getString("@count").equals("1")) {
            RefundRule refundRule = new RefundRule();
            refundRule.setReturnable(false);
            refundRules.add(refundRule);
        } else {
            JSONArray cancelArray = cancellationRules.getJSONArray("rule");
            for (int i = 0; i < cancelArray.size(); i++) {
                JSONObject rule = cancelArray.getJSONObject(i);
                if (rule.getString("noShowPolicy") != null) {
                    continue;
                }
                RefundRule refundRule = new RefundRule();
                try {
                    if (rule.getString("cancelRestricted") != null) {
                        refundRule.setReturnable(false);
                    } else {
                        BigDecimal cancelCharge = new BigDecimal(rule.getJSONObject("cancelCharge").getString("#text"));
                        if (cancelCharge.compareTo(new BigDecimal(0)) > 0) {
                            refundRule.setReturnable(true);
                            refundRule.setRefundType(1);
                            refundRule.setFine(cancelCharge
                                    .multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue());
                        } else {
                            refundRule.setReturnable(true);
                            refundRule.setRefundType(0);
                        }
                    }
                    DateTime hotelCheckIn = DateTime.parse(roomBookingInfo.getFromDate() + " " + room.getCheckInTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"));
                    if (StringUtils.isNotEmpty(rule.getString("fromDate"))) {
                        DateTime fromDate = DateTime.parse(rule.getString("fromDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).minusDays(2).minusSeconds(1);
                        if (i == 0 && fromDate.isBefore(System.currentTimeMillis())) {
                            refundRule.setFromDate(null);
                        } else {
                            refundRule.setFromDate(fromDate.toString("yyyy-MM-dd HH:mm:ss"));
                            long diff = hotelCheckIn.getMillis() - fromDate.getMillis();
                            long days = diff / (1000 * 24 * 60 * 60);
                            long maxCheckInTime = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + days * 24;
                            Integer maxCheckIn = maxCheckInTime > 0 ? Integer.valueOf(String.valueOf(maxCheckInTime)) : 0;
                            refundRule.setMaxHoursBeforeCheckIn(maxCheckIn);
                        }
                    } else {
                        refundRule.setMaxHoursBeforeCheckIn(null);
                    }
                    if (StringUtils.isNotEmpty(rule.getString("toDate"))) {
                        DateTime toDate = DateTime.parse(rule.getString("toDate"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).minusDays(2);
                        refundRule.setToDate(toDate.toString("yyyy-MM-dd HH:mm:ss"));
                        long diff = hotelCheckIn.getMillis() - toDate.getMillis();
                        long days = diff / (1000 * 24 * 60 * 60);
                        long minCheckInTime = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + days * 24;
                        Integer minCheckIn = minCheckInTime > 0 ? Integer.valueOf(String.valueOf(minCheckInTime)) : 0;
                        refundRule.setMinHoursBeforeCheckIn(minCheckIn);
                    } else {
                        refundRule.setMinHoursBeforeCheckIn(0);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    refundRule.setReturnable(false);
                }
                refundRules.add(refundRule);
            }
        }
        room.setRefundRules(refundRules);
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
            if (gender.toLowerCase().equals("male")) {
                salutaionCode = "1328";
            } else if (gender.toLowerCase().equals("female")) {
                salutaionCode = "15134";
            } else if (gender.toLowerCase().equals("child")) {
                salutaionCode = "14632";
            } else {
                salutaionCode = "3801";
            }
            Passenger passenger = new Passenger(salutaionCode,
                    guestArray.getJSONObject(i).getString("firstName"),
                    guestArray.getJSONObject(i).getString("lastName"));
            passenger.setRoomSeq(Integer.valueOf(guestArray.getJSONObject(i).getString("roomSeq")));
            passenger.setSeq(Integer.valueOf(guestArray.getJSONObject(i).getString("seq")));
            plist.add(passenger);
        }
        BookingOrderInfo bookingOrderInfo = bookingOrderInfoRepository.findByAllocationDetails(allocationDetails);
        if (bookingOrderInfo == null) {
            bookingOrderInfo = new BookingOrderInfo();
        }
        List<RoomBookingInfo> roomBookingInfoList = roomBookingInfoRepository.findAllByRoomTypeCode(meitOrder.getRoomId());
        if (roomBookingInfoList.size() == 0) {
            throw new Exception("room is null");
        }
        RoomBookingInfo roomBookingInfo = roomBookingInfoList.get(0);
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
        meitOrder.setAgentOrderId(bookingOrderInfo.getBookingCode());
        BigDecimal basePrice = new BigDecimal(bookingOrderInfo.getPriceValue());
        // 获得酒店价格上浮利率
        EventAttr attr = eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_RATE);
        Double priceRate = Double.valueOf(attr.getEventValue());
        BigDecimal totalPrice = basePrice.multiply(new BigDecimal(100)).multiply(new BigDecimal(1 + priceRate));
        meitOrder.setActualTotalPrice(totalPrice.setScale(0, RoundingMode.HALF_UP).intValue());
        meitOrder.setOrderStatus(PlatformOrderStatusEnum.BOOK_SUCCESS);
        return meitOrderBookingInfoRepository.save(meitOrder);
    }

    /**
     * 通过美团订单id获得dotw订单详细
     * @param orderId
     * @return
     */
    public BookingOrderInfo getBookingByMeitOrder(String orderId) {
        MeitOrderBookingInfo meitOrder = meitOrderBookingInfoRepository.findByOrderId(Long.valueOf(orderId));
        return bookingOrderInfoRepository.findByBookingCode(meitOrder.getAgentOrderId());
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

}
