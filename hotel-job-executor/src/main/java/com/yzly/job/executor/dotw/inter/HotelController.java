package com.yzly.job.executor.dotw.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lazyb
 * @create 2019/12/10
 * @desc
 **/
@FeignClient("yz-hotel-api")
public interface HotelController {

    @GetMapping("/hotel/info")
    Object allHotelInfo(@RequestParam("country") String country, @RequestParam("city") String city, @RequestParam("page") int page, @RequestParam("size") int size);

}
