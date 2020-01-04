package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.RoomBookingInfo;
import com.yzly.core.domain.dotw.vo.Passenger;
import com.yzly.core.repository.dotw.RoomBookingInfoRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@RestController
@RequestMapping(value = "/test")
@CommonsLog
public class TestController {
    @Autowired
    private RoomBookingInfoRepository roomBookingInfoRepository;
    @Autowired
    private DCMLHandler dcmlHandler;

//    @PostMapping("/booking")
//    public Object testBooking(String allocationDetails) {
//        RoomBookingInfo roomBookingInfo = roomBookingInfoRepository.findByAllocationDetails(allocationDetails);
//        Passenger passenger1 = new Passenger("147", "YAN", "DONG");
//        Passenger passenger2 = new Passenger("148", "LING", "ZHILING");
//        List<Passenger> plist = new ArrayList<>();
//        plist.add(passenger1);
//        plist.add(passenger2);
//        String fromDate = DateTime.now().toString("yyyy-MM-dd");
//        String toDate = DateTime.now().plusDays(1).toString("yyyy-MM-dd");
//        JSONObject jsonObject = dcmlHandler.confirmBooking(roomBookingInfo, plist, fromDate, toDate);
//        log.info("booking return: " + jsonObject);
//        return jsonObject;
//    }

    @PostMapping("/search")
    public Object searchBooking(String firstName, String lastName, String city) {
        return dcmlHandler.searchBooking(new Passenger("147", firstName, lastName), city);
    }

}
