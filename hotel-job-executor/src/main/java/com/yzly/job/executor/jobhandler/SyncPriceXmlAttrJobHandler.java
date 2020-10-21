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
 * @create 2020/10/20
 * @desc
 **/
@JobHandler(value = "syncPriceXmlAttrJobHandler")
@Component
public class SyncPriceXmlAttrJobHandler extends IJobHandler {

    @Autowired
    private TaskControllerInter taskControllerInter;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("sync price xml attr job execute start.");
        String result = taskControllerInter.syncRoomPriceXmlByAttr(Integer.valueOf(param));
        XxlJobLogger.log("sync price xml attr job result: " + JSONObject.toJSONString(result));
        return ReturnT.SUCCESS;
    }

}
