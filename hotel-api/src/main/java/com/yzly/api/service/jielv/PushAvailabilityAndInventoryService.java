package com.yzly.api.service.jielv;

import com.yzly.api.common.XmlRequestHandler;
import com.yzly.api.constants.xiecheng.AppHeader;
import com.yzly.api.constants.xiecheng.CommonRequest;
import com.yzly.api.constants.xiecheng.XmlRequest;
import com.yzly.api.exception.YzException;
import com.yzly.api.service.RestHttpService;
import com.yzly.api.constants.xiecheng.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushAvailabilityAndInventoryService {

    @Autowired
    private RestHttpService restHttpService;
    private static Logger logger = LoggerFactory.getLogger(PushAvailabilityAndInventoryService.class);

    public void PushAvailabilityAndInventory(){
        //组装报文
        AppHeader appHeader =new AppHeader();
        CommonRequest commonRequest =null;
        try {
//            SendSerial sendSerial =new SendSerial();
//            CommonRequest commonRequest1 = CommonResquestFactory.createCommonRequest(sendSerial,commonRequest);

            Auth auth =new Auth();
            auth.setID("1");
            auth.setCode("001");
            auth.setCodeContext("testesst");
            XmlRequest xmlRequest =new XmlRequest();
            xmlRequest.setAuth(auth);
            String xml = XmlRequestHandler.getInstance().parseToXml(xmlRequest);

//            XmlRequest esbXmlRequest = XmlRequestFactory.getInstance().createXmlRequest(commonRequest,appHeader);
            logger.debug("报文转义为：————————————》"+xml);
        } catch (YzException e) {
            e.printStackTrace();
        }
//        commonRequest1.setData(commonRequest.getData().getOpenAccount());
//        CommonResponse<SendSerial, JSONObject> commonResponse = restHttpService.doPost(CommonFiled.MessageCode.OPEN_ACCOUNT.getValue(),commonRequest1);
//        if(!commonResponse.getJournal().getReturnCode().equals("000000")){
//            throw new EcipException("安心签开户失败,"+commonResponse.getJournal().getReturnMsg());
//        }
        return;
    }
}
