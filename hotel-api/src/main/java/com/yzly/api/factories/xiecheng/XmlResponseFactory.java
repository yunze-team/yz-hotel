package com.yzly.api.factories.xiecheng;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 20:07
 **/



import com.yzly.api.constants.xiecheng.AppHeader;
import com.yzly.api.constants.xiecheng.XmlResponse;
import com.yzly.api.exception.YzException;
import com.yzly.api.models.*;
import com.yzly.api.constants.xiecheng.Auth;

import java.util.ArrayList;
import java.util.List;

/**er
 *@ClassName XmlRequestFactory
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 20:07
 *@Version 1.0
 **/
public class XmlResponseFactory {



    private XmlResponseFactory() {
    }

    private static class XmlRequestFactoryInstance {
        private final static XmlResponseFactory instance = new XmlResponseFactory();
    }

    public static XmlResponseFactory getInstance() {
        return XmlRequestFactoryInstance.instance;
    }

    public XmlResponse createXmlResponse() throws YzException {

        AppHeader appHead = new AppHeader();
        XmlResponse XmlResponse = new XmlResponse();

        Auth auth = new Auth();
        List<Result> list = new ArrayList();
        Body body = new Body();
        body.setBodyList(list);

        XmlResponse.setAuth(auth);
        XmlResponse.setAppHeader(appHead);
        XmlResponse.setBody(body);
        return XmlResponse;
    }
}
