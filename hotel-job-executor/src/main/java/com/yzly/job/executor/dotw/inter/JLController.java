package com.yzly.job.executor.dotw.inter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lazyb
 * @create 2020/5/7
 * @desc
 **/
@FeignClient("yz-hotel-api")
public interface JLController {

    @GetMapping("/api/jl/static/sync_hotel")
    String syncHotel();

}
