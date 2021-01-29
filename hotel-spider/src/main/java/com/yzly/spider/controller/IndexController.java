package com.yzly.spider.controller;

import com.yzly.spider.service.FLMiaoSpiderService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2021/1/27
 * @desc
 **/
@RestController
@CommonsLog
public class IndexController {

    @Autowired
    private FLMiaoSpiderService flMiaoSpiderService;

    @GetMapping("/flmiao")
    public Object flmiaoPageSpider() {
        flMiaoSpiderService.spiderPageTest();
        return "SUCCESS";
    }

    @GetMapping("/show")
    public Object showSSL() {
        try {
            flMiaoSpiderService.showSupportSSL();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "SUCCESS";
    }

}
