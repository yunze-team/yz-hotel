package com.yzly.api.service.dotw;

import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.*;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.service.meit.MeitService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MeitApiService {

    @Autowired
    private MeitService meitService;

    /**
     * 添加美团调用日志记录
     * @param traceId
     * @param encyptData
     * @param reqData
     * @param meitTraceLog
     * @return
     */
    public MeitTraceLog addTraceLog(String traceId, String encyptData, String reqData, MeitTraceLog meitTraceLog) {
        return meitService.addOrUpdateTrace(traceId, encyptData, reqData, meitTraceLog);
    }

    /**
     * 美团酒店同步方法
     * @param skip
     * @param limit
     * @return
     */
    public Object syncHotelBasic(int skip, int limit) {
        int page = skip / limit + 1;
        List<MeitHotel> mlist = meitService.getHotelBasicList(page, limit, DistributorEnum.MEIT);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotels", mlist);
        return data;
    }

    /**
     * 美团酒店扩展信息同步
     * @param hotelId
     * @return
     */
    public Object syncHotelExtend(String hotelId) {
        List<MeitHotelExt> mlist = meitService.getHotelExtListByIds(hotelId);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelExt", mlist);
        return data;
    }

    /**
     * 美团房型基础信息同步
     * @param hotelId
     * @return
     */
    public Object syncRoomBasic(String hotelId) {
        List<HotelRoomTypeBasic> hlist = meitService.getRoomBasicsByIds(hotelId);
        if (hlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelRoomTypeBasics", hlist);
        return data;
    }

    /**
     * 美团房型扩展信息同步
     * @param hotelId
     * @return
     */
    public Object syncRoomExtend(String hotelId) {
        List<RoomTypeExtModelList> hlist = meitService.getRoomExtendsByIds(hotelId);
        if (hlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("roomTypeExtModelList", hlist);
        return data;
    }

    /**
     * 美团产品搜索结果同步
     * @param hotelIds 酒店ID，多个用“，”分隔
     * @param roomId 房型ID，单产品查询时有值
     * @param ratePlanCode 售卖计划，单产品查询时有值
     * @param checkin 入住日期，格式为YYYY-MM-DD
     * @param checkout 离店日期，格式为YYYY-MM-DD
     * @param roomNumber 房间数
     * @param numberOfAdults 成人数
     * @param numberOfChildren 儿童数
     * @param childrenAges 儿童年龄，以“,”分隔的数字组合
     * @param currencyCode 币种，（海外）Currency code. ISO 4217（国内）默认为“CNY”
     * @return
     */
    public Object syncGoodsSearch(String hotelIds, String roomId, String ratePlanCode, String checkin,
                                  String checkout, Integer roomNumber, Integer numberOfAdults, Integer numberOfChildren,
                                  String childrenAges, String currencyCode) {
        Map<String, HotelMap> data = new HashMap<>();
        return data;
    }

}
