package com.yzly.api.service.jielv;

import com.yzly.api.common.XmlRequestHandler;
import com.yzly.api.constants.AppHeader;
import com.yzly.api.constants.CommonRequest;
import com.yzly.api.constants.SendSerial;
import com.yzly.api.constants.XmlRequest;
import com.yzly.api.exception.YzException;
import com.yzly.api.factories.CommonResquestFactory;
import com.yzly.api.factories.XmlRequestFactory;
import com.yzly.api.service.RestHttpService;
import com.yzly.core.domain.jielv.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushAvailabilityAndInventoryService {

    @Autowired
    private RestHttpService restHttpService;

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
            System.out.println("报文转义为：————————————》"+xml);
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
