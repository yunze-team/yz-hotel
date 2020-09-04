package com.yzly.api.controller.ctrip;

import com.yzly.api.util.ctrip.AuthUtil;
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
    private AuthUtil authUtil;

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
        String requestName = "OTA_HotelAvailRQ";
        String echoToken = authUtil.judgeCtripAuth(xml, requestName);
        return null;
    }

}
