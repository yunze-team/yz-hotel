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
 * @create 2020/3/5
 * @desc
 **/
@JobHandler(value = "delRoomBooingInfoJobHandler")
@Component
public class DelRoomBooingInfoJobHandler extends IJobHandler {

    @Autowired
    private TaskControllerInter taskControllerInter;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("del room booking info job execute start.");
        String result = taskControllerInter.delAllRoomBookingInfo();
        XxlJobLogger.log("del room booking info job result: " + JSONObject.toJSONString(result));
        return ReturnT.SUCCESS;
    }
}
