package com.yzly.api.service.dotw;

import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.meit.dto.GoodsSearchQuery;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.service.meit.TaskService;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void syncDotwRoomPrice() {
        List<String> hotelIds = taskService.findSyncHotelIdsByAttr();
        Integer days = Integer.valueOf(eventAttrRepository.findByEventType(MEIT_ROOM_PRICE_PULL_DAY).getEventValue());
        for (int i = 0; i < days; i++) {
            String fromDate = DateTime.now().plusDays(i).toString("yyyy-MM-dd");
            String toDate = DateTime.now().plusDays(i).plusDays(1).toString("yyyy-MM-dd");
            GoodsSearchQuery goodsSearchQuery = new GoodsSearchQuery();
            goodsSearchQuery.setCheckin(fromDate);
            goodsSearchQuery.setCheckout(toDate);
            goodsSearchQuery.setCurrencyCode("CNY");
            for (String hotelId : hotelIds) {
                String resp = dcmlHandler.getRoomsByMeitQueryWithHotelId(hotelId, goodsSearchQuery);
                log.info(resp);
                // 不需额外加入添加，在dcml中集成
                //taskService.addRoomPrice(resp, goodsSearchQuery, hotelId);
            }
        }
    }

}
