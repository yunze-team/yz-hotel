package com.yzly.api.factories.xiecheng;

import com.alibaba.fastjson.JSON;
import com.yzly.api.common.XmlResponseHandler;
import com.yzly.api.constants.xiecheng.*;
import com.yzly.api.exception.YzException;
import com.yzly.api.models.Result;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class XmlRequestFactory {
    private XmlRequestFactory() {
    }

    private static class XmlRequestFactoryInstance {
        private final static XmlRequestFactory instance = new XmlRequestFactory();
    }

    public static XmlRequestFactory getInstance() {
        return XmlRequestFactoryInstance.instance;
    }


    public XmlRequest createXmlRequest(){
        XmlRequest xmlRequest = new XmlRequest();
        Auth auth = new Auth();
        AppHeader appHeader = new AppHeader();
        xmlRequest.setAuth(auth);
        xmlRequest.setAppHeader(appHeader);
        return xmlRequest;
    }

    public XmlRequest createXmlRequest(CommonRequest<SendSerial,?> commonRequest) throws YzException {
        return createXmlRequest(commonRequest, null);
    }

    public XmlRequest createXmlRequest(CommonRequest<SendSerial,?> commonRequest,AppHeader appHeader) throws YzException {
        SendSerial sendSerial = (SendSerial)commonRequest.getJournal();
        AppHeader appHead = appHeader;
        if(appHeader==null){
            appHead = new AppHeader();
            appHead.setPageEnd("10");
            appHead.setCurrentNum("0");
            appHead.setPageStart("1");
            appHead.setPgUpOrPgDn("1");
            appHead.setTotalNum("20");
            appHead.setTotalFlag("E");
        }

        Auth auth = new Auth();
        auth.setCode("Code0001");
//        sysHeader.setTransCode(sendSerial.getTranCode());
//        sysHeader.setTranMode("ONLINE");
//        String sourceType = "";
//        if (StringUtils.isEmpty(sourceType)) {
//            sysHeader.setSourceType("QY");
//        }else{
//            sysHeader.setSourceType(sourceType);
//        }
//        sysHeader.setMessageCode(sendSerial.getTranCode());
//        sysHeader.setSeqNo(sendSerial.getSendSerialNo());
//        sysHeader.setUserLang("CHINESE");
//        sysHeader.setServerId("");
//        sysHeader.setWsId("");

        XmlRequest XmlRequest = new XmlRequest();
        XmlRequest.setAppHeader(appHead);
        XmlRequest.setAuth(auth);
        XmlRequest.setBody(commonRequest.getData());
        return XmlRequest;
    }

    public XmlRequest createXmlRequest(CommonResponse response) throws YzException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest servletRequest = attributes.getRequest();
        String requestBody = servletRequest.getAttribute("REQUEST_BODY").toString();
        XmlResponse request = XmlResponseHandler.getInstance().transform(requestBody);
        XmlRequest XmlRequest = new XmlRequest();
        Auth auth = request.getAuth();
        XmlRequest.setAuth(JSON.parseObject(JSON.toJSONString(auth), Auth.class));
        XmlRequest.setBody(response.getData() == null?new Object():response.getData());
        XmlRequest.setAppHeader(JSON.parseObject(JSON.toJSONString(request.getAppHeader()), AppHeader.class));
        List<Result> list = new ArrayList<>();
        Result result = new Result();
        result.setRetCode(response.getJournal().getReturnCode());
        result.setRetMsg(response.getJournal().getReturnMsg());
        list.add(result);
        return XmlRequest;
    }

    /**
     * 重载创建 请求对象
     *
     * @param commonRequest
     * @param appHeader
     * @return
     * @throws YzException
     */
    public XmlRequest createNewXmlRequest(CommonRequest<SendSerial, ?> commonRequest,
                                                AppHeader appHeader) throws YzException {
        AppHeader appHead = buildAppHeader(appHeader);
        SendSerial sendSerial = (SendSerial) commonRequest.getJournal();
        XmlRequest XmlRequest = new XmlRequest();
        XmlRequest.setAppHeader(appHead);
        XmlRequest.setBody(commonRequest.getData());
        return XmlRequest;
    }

    /**
     * 构建AppHeader 非空对象
     *
     * @param appHeader
     * @return
     */
    public static AppHeader buildAppHeader(AppHeader appHeader) {
        if (appHeader == null) {
            appHeader = new AppHeader();
            appHeader.setPageEnd("10");
            appHeader.setCurrentNum("0");
            appHeader.setPageStart("1");
            appHeader.setPgUpOrPgDn("1");
            appHeader.setTotalNum("20");
            appHeader.setTotalFlag("E");
            return appHeader;
        }
        return appHeader;
    }


    /**
     * 构建AppHeader 非空对象
     *
     * @param sendSerial
     * @return
     */
    public static SysHeader buildSysHeader( SendSerial sendSerial) {
        SysHeader sysHeader = SysHeader.getNewDefaultSysHeader();
        sysHeader = SysHeader.getNewDefaultSysHeader();
        sysHeader.setSeqNo(sendSerial.getSendSerialNo());
        return sysHeader;
    }
}
