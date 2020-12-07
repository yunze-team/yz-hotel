package com.yzly.api.controller.ctrip;

import com.yzly.api.service.ctrip.CtripApiService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 提供给携程的接口
 * @author lazyb
 * @create 2020/9/2
 * @desc
 **/
@RestController
@RequestMapping(value = "/api/ctrip")
@CommonsLog
public class CtripController {

    @Autowired
    private CtripApiService ctripApiService;

    /**
     * 房间产品检查接口
     * @param xml
     * @return
     */
    @PostMapping(value = "/check",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public Object checkAvailability(@RequestBody String xml) {
        log.info(xml);
        try {
            return ctripApiService.executeCtripCheckApi(xml);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 创建订单接口
     * @param xml
     * @return
     */
    @PostMapping(value = "/order_new",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public Object crteateOrder(@RequestBody String xml) {
        log.info(xml);
        try {
            return ctripApiService.executeCtripCreateApi(xml);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 取消订单接口
     * @param xml
     * @return
     */
    @PostMapping(value = "/cancel",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public Object cancelOrder(@RequestBody String xml) {
        log.info(xml);
        try {
            return ctripApiService.executeCtripCancelApi(xml);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 查询订单接口
     * @param xml
     * @return
     */
    @PostMapping(value = "/read",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE)
    @ResponseBody
    public Object readOrder(@RequestBody String xml) {
        log.info(xml);
        try {
            return ctripApiService.executeCtripReadApi(xml);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
