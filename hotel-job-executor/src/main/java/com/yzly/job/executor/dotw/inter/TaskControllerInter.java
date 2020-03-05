package com.yzly.job.executor.dotw.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lazyb
 * @create 2019/12/16
 * @desc
 **/
@FeignClient("yz-hotel-api")
public interface TaskControllerInter {

    @GetMapping("/api/task/pull_hotel")
    String pullHotel();

    @GetMapping("/api/task/pull_price")
    String pullPrice();

    @GetMapping("/api/task/update_date")
    String updatePullDate();

    @GetMapping("/api/task/sync_list")
    String syncHotelAndRoomByList();

    @GetMapping("/api/task/sync_price")
    String syncRoomPriceByDate();

    @GetMapping("/api/task/room_price_excel")
    String generateRoomPriceExcel();

    @GetMapping("/api/task/sync_price_xml")
    String syncRoomPriceXmlByDate(@RequestParam("offset") int offset);

}
