package com.yzly.api.common;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-18 10:30
 **/


import com.yzly.api.constants.xiecheng.XmlResponse;
import com.yzly.api.exception.YzException;
import com.yzly.api.factories.xiecheng.XmlResponseFactory;
import com.yzly.api.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *@ClassName XmlRequestHandler
 *@Description 处理将实体类转换为xml报文
 *@Auth frank
 *@Date 2019-11-18 10:30
 *@Version 1.0
 **/
public class XmlResponseHandler<T> {

    private XmlResponseHandler() {
    }

    private static class XmlHandlerInstance {
        private final static XmlResponseHandler instance = new XmlResponseHandler();
    }

    public static XmlResponseHandler getInstance() {
        return XmlHandlerInstance.instance;
    }


    private static Logger logger = LoggerFactory.getLogger(XmlResponseHandler.class);

    public XmlResponse transform(String xml) throws YzException {
        XmlResponse XmlResponse = XmlResponseFactory.getInstance().createXmlResponse();
        try {
            XmlUtils.parseXmlToObject(XmlResponse,xml);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new YzException(e.getMessage());
        }
        return XmlResponse;
    }



}
