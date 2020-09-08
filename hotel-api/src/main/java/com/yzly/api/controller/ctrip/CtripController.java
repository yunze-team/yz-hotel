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
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
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

}
