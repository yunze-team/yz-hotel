package com.yzly.api.controller.jl;

import com.yzly.api.service.jl.JLStaticApiService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**捷旅api-静态信息接口
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@RequestMapping("/api/jl")
@RestController
@CommonsLog
public class JLStaticController {

    @Autowired
    private JLStaticApiService jlStaticApiService;

    @GetMapping("/sync_city")
    public Object syncCity() {
        Runnable runnable = () -> {
            jlStaticApiService.syncJlCity();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

    @GetMapping("/sync_hotel")
    public Object syncHotel() {
        Runnable runnable = () -> {
            jlStaticApiService.syncJLHotel();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return "SUCCESS";
    }

}
