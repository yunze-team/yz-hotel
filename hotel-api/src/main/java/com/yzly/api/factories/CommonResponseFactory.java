package com.yzly.api.factories;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 11:28
 **/

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.constants.CommonRequest;
import com.yzly.api.constants.CommonResponse;
import com.yzly.api.constants.SendSerial;
import com.yzly.api.constants.Serial;
import com.yzly.api.exception.YzException;


/**
 *@ClassName CommonResponseFactory
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 11:28
 *@Version 1.0
 **/
public class CommonResponseFactory {

    private CommonResponseFactory() {
    }

    private static class CommonResponseInstance {
        private final static CommonResponseFactory instance = new CommonResponseFactory();
    }

    public static CommonResponseFactory getInstance() {
        return CommonResponseInstance.instance;
    }

 /**
  * @author chenlei
  * @description // 创建调用esb请求需要的{@link CommonResponse}
  * @date 14:54 2019/12/2
  * @param
  * @return
  * @exception
  **/
    public static CommonResponse createCommonResponse() {
        CommonResponse<SendSerial,Object> commonResponse = new CommonResponse<>();
        return commonResponse;
    }
     /**
      * @author chenlei
      * @description //TODO 创建调用esb请求需要的{@link CommonResponse}
      * @date 14:57 2019/12/2
      * @param returnCode 返回码
      * @param returnMsg 返回信息
      * @return
      * @exception
      **/
    public static CommonResponse createCommonResponse(String returnCode,String returnMsg) {
        CommonResponse commonResponse = createCommonResponse();
        SendSerial journal = (SendSerial) commonResponse.getJournal();
        journal.setReturnCode(returnCode);
        journal.setReturnMsg(returnMsg);
        commonResponse.setJournal(journal);
        return commonResponse;
    }

     /**
      * @author chenlei
      * @description //TODO 创建接口返回的信息 ${@link CommonResponse}
      * @date 14:58 2019/12/2
      * @param
      * @return
      * @exception
      **/
    public static CommonResponse createCommonResponse(Serial serial, Object o) {
        return new CommonResponse(serial,o);
    }

     /**
      * @author chenlei
      * @description //TODO 创建异常返回的 {@link CommonResponse}
      * @date 14:59 2019/12/2
      * @param
      * @return
      * @exception
      **/
    public static CommonResponse createErrorResponse(Exception e) {
        CommonResponse commonResponse = new CommonResponse();
        Serial serial = new Serial();
        if((e instanceof YzException) || (e instanceof YzException)){
            serial.setReturnMsg(e.getMessage());
        }else{
            serial.setReturnMsg("fail");
        }
        commonResponse.setJournal(serial);
        return commonResponse;
    }

    public static CommonResponse transfer(CommonResponse<SendSerial, JSONObject> commonResponse, Class<?> clazz){
        return createCommonResponse(commonResponse.getJournal(), JSON.parseObject(commonResponse.getData().toJSONString(), clazz));
    }

    public static CommonResponse transfer(CommonResponse<SendSerial, JSONObject> commonResponse, Class<?> clazz, CommonRequest request){
        return createCommonResponse(request.getJournal(), JSON.parseObject(commonResponse.getData().toJSONString(), clazz));
    }
}
