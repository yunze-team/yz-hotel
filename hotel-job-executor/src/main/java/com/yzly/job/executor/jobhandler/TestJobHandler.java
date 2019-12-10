package com.yzly.job.executor.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.yzly.job.core.biz.model.ReturnT;
import com.yzly.job.core.handler.IJobHandler;
import com.yzly.job.core.handler.annotation.JobHandler;
import com.yzly.job.core.log.XxlJobLogger;
import com.yzly.job.executor.dotw.inter.HotelController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lazyb
 * @create 2019/12/10
 * @desc
 **/
@JobHandler(value = "testJobHandler")
@Component
public class TestJobHandler extends IJobHandler {

    @Autowired
    private HotelController hotelController;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("test job execute start.");
        Object result = hotelController.allHotelInfo("CHINA", "WUHAN", 2, 10);
        XxlJobLogger.log("test job result: " + JSONObject.toJSONString(result));
        return ReturnT.SUCCESS;
    }
}
