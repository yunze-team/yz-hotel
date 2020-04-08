package com.yzly.api.factories;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 11:28
 **/


import com.yzly.api.constants.CommonRequest;
import com.yzly.api.constants.SendSerial;
import com.yzly.api.constants.Serial;

/**
 *@ClassName CommonResquestFactory
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 11:28
 *@Version 1.0
 **/
public class CommonResquestFactory {

    private CommonResquestFactory() {
    }

    private static class CommonResquestInstance {
        private final static CommonResquestFactory instance = new CommonResquestFactory();
    }

    public static CommonResquestFactory getInstance() {
        return CommonResquestInstance.instance;
    }

     /**
      * @author chenlei
      * @description //TODO 创建esb请求调用的{@link CommonRequest}
      * @date 15:00 2019/12/2
      * @param
      * @return
      * @exception
      **/
    public static CommonRequest createCommonRequest() {
        CommonRequest commonRequest = new CommonRequest<>();
        return commonRequest;
    }
     /**
      * @author chenlei
      * @description //TODO 创建esb请求调用的{@link CommonRequest}
      * @date 15:01 2019/12/2
      * @param serviceCode 请求服务方的code
      * @param tranCode 交易码
      * @param request 请求调用的{@link CommonRequest}
      * @return
      * @exception
      **/
    public static CommonRequest createCommonRequest(String serviceCode, String tranCode) {
        CommonRequest commonRequest = createCommonRequest();
        SendSerial journal = (SendSerial) commonRequest.getJournal();
        journal.setServiceCode(serviceCode);
        journal.setTranCode(tranCode);
        commonRequest.setJournal(journal);
        return commonRequest;
    }

     /**
      * @author chenlei
      * @description //TODO 接口调用的{@link CommonRequest}
      * @date 15:02 2019/12/2
      * @param
      * @return
      * @exception
      **/
    public static CommonRequest createCommonRequest(Serial serial, Object o) {
        return new CommonRequest(serial,o);
    }
}
