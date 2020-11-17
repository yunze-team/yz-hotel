package com.yzly.api.controller.ctrip;

import com.yzly.api.common.CtripHandler;
import com.yzly.api.service.ctrip.CtripApiService;
import com.yzly.core.domain.jl.JLHotelDetail;
import com.yzly.core.service.jl.JLAdminService;
import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author lazyb
 * @create 2020/8/31
 * @desc
 **/
@RestController
@RequestMapping(value = "/api/ctrip/test")
@CommonsLog
public class CtripTestController {

    @Autowired
    private CtripHandler ctripHandler;
    @Autowired
    private JLAdminService jlAdminService;
    @Autowired
    private CtripApiService ctripApiService;

    /**
     * 测试携程酒店推送接口
     * @param hotelId
     * @return
     */
    @GetMapping("/hotel")
    @ResponseBody
    public Object testPushHotel(int hotelId) {
        JLHotelDetail jlHotelDetail = jlAdminService.findByHotelId(hotelId);
        Document doc = ctripHandler.pushHotelDetail(jlHotelDetail);
        try {
            return ctripHandler.sendCtripStatic(doc);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 测试携程房型推送接口
     * @param hotelId
     * @return
     */
    @GetMapping("/room")
    @ResponseBody
    public Object testPushRoom(int hotelId) {
        JLHotelDetail jlHotelDetail = jlAdminService.findByHotelId(hotelId);
        Document doc = ctripHandler.pushBasicRoome(jlHotelDetail);
        try {
            return ctripHandler.sendCtripStatic(doc);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 测试携程查询酒店推送状态接口
     * @param hotelIds
     * @return
     */
    @GetMapping("/query_status")
    @ResponseBody
    public Object testQueryStatus(String hotelIds) {
        List<String> hotelCodes = Arrays.asList(hotelIds.split(","));
        Document doc = ctripHandler.queryHotelStatus(hotelCodes);
        try {
            String xml = ctripHandler.sendCtripStatic(doc);
            return ctripApiService.executeCtripHotelQuery(xml);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping(value = "/xml",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Object testXml(@RequestBody String xml) {
        log.info(xml);
        Document doc = DocumentHelper.createDocument();
        doc.addElement("soap:Envelope");
        return doc.asXML();
    }

}
