package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.HotelRoomPriceXml;
import com.yzly.core.domain.meit.dto.GoodsSearchQuery;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.service.meit.TaskService;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/2/20
 * @desc
 **/
@Service
@CommonsLog
public class TaskApiService {

    @Autowired
    private DCMLHandler dcmlHandler;
    @Autowired
    private EventAttrRepository eventAttrRepository;
    @Autowired
    private TaskService taskService;

    private final static String MEIT_ROOM_PRICE_PULL_DAY = "MEIT_ROOM_PRICE_PULL_DAY";

    /**
     * 按照酒店同步列表的数据，同步30天的房型价格
     */
    public void syncDotwRoomPrice(int offset) {
        List<String> hotelIds = taskService.findSyncHotelIdsByAttr();
        Integer days = Integer.valueOf(eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_PULL_DAY).getEventValue());
        int startDays = offset * days;
        int endDays = days + startDays;
        for (int i = startDays; i < endDays; i++) {
            String fromDate = DateTime.now().plusDays(i).toString("yyyy-MM-dd");
            String toDate = DateTime.now().plusDays(i).plusDays(1).toString("yyyy-MM-dd");
            GoodsSearchQuery goodsSearchQuery = new GoodsSearchQuery();
            goodsSearchQuery.setCheckin(fromDate);
            goodsSearchQuery.setCheckout(toDate);
            goodsSearchQuery.setCurrencyCode("CNY");
            goodsSearchQuery.setNumberOfAdults(2);
            goodsSearchQuery.setNumberOfChildren(0);
            goodsSearchQuery.setRoomNumber(1);
            for (String hotelId : hotelIds) {
                List<HotelRoomPriceXml> hlist = taskService.findPriceByQuery(goodsSearchQuery, hotelId);
                if (hlist != null) {
                    taskService.delRoomPriceXmlList(hlist);
                }
                String resp = dcmlHandler.getRoomsByMeitQueryWithHotelId(hotelId, goodsSearchQuery, false);
                log.debug(resp);
                taskService.addRoomPrice(resp, goodsSearchQuery, hotelId);
            }
        }
    }

    /**
     * 使用search-hotels方法同步酒店的房型价格
     * @param offset
     */
    public void syncDotwHotelPrice(int offset) {
        List<String> hotelIds = taskService.findSyncHotelIdsByAttr();
        Integer days = Integer.valueOf(eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_PULL_DAY).getEventValue());
        int startDays = offset * days;
        int endDays = days + startDays;
        for (int i = startDays; i < endDays; i++) {
            String fromDate = DateTime.now().plusDays(i).toString("yyyy-MM-dd");
            String toDate = DateTime.now().plusDays(i).plusDays(1).toString("yyyy-MM-dd");
            for (String hotelId : hotelIds) {
                List<String> hotelIdArray = new ArrayList<>();
                hotelIdArray.add(hotelId);
                JSONObject priceObject = dcmlHandler.searchHotelPriceByID(hotelIdArray, fromDate, toDate);
                taskService.syncRoomPriceByDate(priceObject, fromDate, toDate);
            }
        }
    }

}
