package com.yzly.api.common;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-21 16:12
 **/

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.constants.*;
import com.yzly.api.factories.CommonResponseFactory;
import com.yzly.api.models.Result;
import com.yzly.core.domain.jielv.Auth;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *@ClassName CommonResponseHandler
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-21 16:12
 *@Version 1.0
 **/
public class CommonResponseHandler {

    private CommonResponseHandler() {
    }

    private static class CommonResponseInstance {
        private final static CommonResponseHandler instance = new CommonResponseHandler();
    }

    public static CommonResponseHandler getInstance() {
        return CommonResponseInstance.instance;
    }

    public CommonResponse transform(CommonRequest commonRequest, XmlResponse XmlResponse) {
        SendSerial journal = (SendSerial) commonRequest.getJournal();
        Auth auth = XmlResponse.getAuth();
        CommonResponse commonResponse = CommonResponseFactory.createCommonResponse();
        JSONObject data = null;
        if(XmlResponse.getBody() instanceof JSONObject){
            data = (JSONObject) XmlResponse.getBody();
            AppHeader appHeader = XmlResponse.getAppHeader();
        }
        commonResponse.setData(data);
        commonResponse.setJournal(journal);
        return commonResponse;
    }
}
