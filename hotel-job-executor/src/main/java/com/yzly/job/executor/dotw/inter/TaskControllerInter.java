package com.yzly.job.executor.dotw.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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

}
