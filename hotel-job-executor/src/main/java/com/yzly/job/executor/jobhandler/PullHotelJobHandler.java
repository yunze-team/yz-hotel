package com.yzly.job.executor.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.yzly.job.core.biz.model.ReturnT;
import com.yzly.job.core.handler.IJobHandler;
import com.yzly.job.core.handler.annotation.JobHandler;
import com.yzly.job.core.log.XxlJobLogger;
import com.yzly.job.executor.dotw.inter.TaskControllerInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lazyb
 * @create 2019/12/11
 * @desc
 **/
@JobHandler(value = "pullHotelJobHandler")
@Component
public class PullHotelJobHandler extends IJobHandler {

    @Autowired
    private TaskControllerInter taskControllerInter;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("pull hotel job execute start.");
        String result = taskControllerInter.pullHotel();
        XxlJobLogger.log("pull hotel job result: " + JSONObject.toJSONString(result));
        return ReturnT.SUCCESS;
    }
}
