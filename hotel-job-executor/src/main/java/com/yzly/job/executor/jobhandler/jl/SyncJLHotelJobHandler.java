package com.yzly.job.executor.jobhandler.jl;

import com.alibaba.fastjson.JSONObject;
import com.yzly.job.core.biz.model.ReturnT;
import com.yzly.job.core.handler.IJobHandler;
import com.yzly.job.core.handler.annotation.JobHandler;
import com.yzly.job.core.log.XxlJobLogger;
import com.yzly.job.executor.dotw.inter.JLController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lazyb
 * @create 2020/5/7
 * @desc
 **/
@JobHandler(value = "syncJLHotelJobHandler")
@Component
public class SyncJLHotelJobHandler extends IJobHandler {

    @Autowired
    private JLController jlController;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("sync jl hotel job execute start.");
        String result = jlController.syncHotel();
        XxlJobLogger.log("sync jl hotel job result: " + JSONObject.toJSONString(result));
        return ReturnT.SUCCESS;
    }
}
