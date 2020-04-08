package com.yzly.api.service;

import com.alibaba.fastjson.JSONObject;

import com.yzly.api.common.CommonResponseHandler;
import com.yzly.api.common.XmlRequestHandler;
import com.yzly.api.common.XmlResponseHandler;
import com.yzly.api.constants.*;
import com.yzly.api.exception.YzException;
import com.yzly.api.factories.CommonResponseFactory;
import com.yzly.api.factories.XmlRequestFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 *@ClassName RestHttpServiceImpl
 *@Description TODO
 *@Auth frank
 *@Date 2019-10-30 10:07
 *@Version 1.0
 **/
@Service
public class RestHttpServiceImpl implements RestHttpService {

    private RestTemplate restTemplate;

    public RestHttpServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @Author frank
     * @Description //采用GET请求，数据格式为 application/json，并且返回结果是JSON string
     * @Date 08:36 2019-11-01
     * @Param [path, map]
     * @return com.zbank.ecip.online.common.param.response.CommonResponse
     **/
    @Override
    public CommonResponse doGet(String path, Map map) throws YzException {
        ResponseEntity<String> result = restTemplate.getForEntity(path, String.class, map);
        HttpStatus status = result.getStatusCode();
        String body = result.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        CommonResponse commonResponse = CommonResponseFactory.createCommonResponse(status.toString(), CommonResponse.OK_MESSAGE);
        commonResponse.setData(jsonObject);
        return commonResponse;
    }

    @Override
    public CommonResponse doGet(String path) throws YzException {
        ResponseEntity<String> result = restTemplate.getForEntity(path, String.class);
        HttpStatus status = result.getStatusCode();
        String body = result.getBody();
        CommonResponse commonResponse = CommonResponseFactory.createCommonResponse(status.toString(), CommonResponse.OK_MESSAGE);
        commonResponse.setData(body);
        return commonResponse;
    }

    /**
     * @Author frank
     * @Description //采用POST请求，数据格式为 application/json，并且返回结果是JSON string
     * @Date 10:54 2019-10-30
     * @Param [path, map]
     * @return com.zbank.ecip.router.common.param.CommonResponse
     **/
    @Override
    public CommonResponse doPost(String path, Map map) throws YzException {
        //设置Http Header
        HttpHeaders headers = new HttpHeaders();
        //设置请求媒体数据类型
        headers.setContentType(MediaType.APPLICATION_JSON);
        //设置返回媒体数据类型
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(map.toString(), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(path, formEntity, String.class);
        HttpStatus status = result.getStatusCode();
        CommonResponse commonResponse = CommonResponseFactory.createCommonResponse(status.toString(), CommonResponse.OK_MESSAGE);
        commonResponse.setData(result.getBody());
        return commonResponse;
    }

    @Override
    public CommonResponse doPost(String path) throws YzException {
        return doPost(path, (Map)null);
    }



    @Override
    public CommonResponse doPost(String path, CommonRequest commonRequest) throws YzException {
        return doPost(path, commonRequest, null);
    }

    @Override
    public CommonResponse doPost(String path, CommonRequest commonRequest, AppHeader appHeader) throws YzException{
        XmlResponse XmlResponse = httpPost(path, commonRequest, appHeader);
        CommonResponse commonResponse = CommonResponseHandler.getInstance().transform(commonRequest, XmlResponse);
        return commonResponse;
    }

    @Override
    public XmlResponse doPagePost(String path, CommonRequest commonRequest, AppHeader appHeader) throws YzException {

        return httpPost(path,commonRequest,appHeader);
    }

    private XmlResponse httpPost(String path, CommonRequest commonRequest, AppHeader appHeader) throws YzException{
        XmlRequest XmlRequest = XmlRequestFactory.getInstance().createXmlRequest(commonRequest,appHeader);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        String xml = XmlRequestHandler.getInstance().parseToXml(XmlRequest);
//        String xml = "<service><sys-header><data name=\"SYS_HEAD\"><struct><data name=\"MODULE_ID\"><field length=\"2\" scale=\"0\" type=\"string\">CL</field></data><data name=\"TRAN_TIMESTAMP\"><field length=\"9\" scale=\"0\" type=\"string\">094952401</field></data><data name=\"SOURCE_BRANCH_NO\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"BRANCH_ID\"><field length=\"6\" scale=\"0\" type=\"string\">027008</field></data><data name=\"USER_LANG\"><field length=\"7\" scale=\"0\" type=\"string\">CHINESE</field></data><data name=\"RET_STATUS\"><field length=\"1\" scale=\"0\" type=\"string\">S</field></data><data name=\"SEQ_NO\"><field length=\"25\" scale=\"0\" type=\"string\">270A0F1E85BA6528094952401</field></data><data name=\"SOURCE_TYPE\"><field length=\"2\" scale=\"0\" type=\"string\">MT</field></data><data name=\"TRAN_CODE\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"SERVER_ID\"><field length=\"9\" scale=\"0\" type=\"string\">127.0.0.1</field></data><data name=\"MESSAGE_CODE\"><field length=\"4\" scale=\"0\" type=\"string\">204901</field></data><data name=\"SERVICE_CODE\"><field length=\"8\" scale=\"0\" type=\"string\">SVR_FMS</field></data><data name=\"AUTH_PASSWORD\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"APPR_FLAG\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"USER_ID\"><field length=\"0\" scale=\"0\" type=\"string\">0304</field></data><data name=\"PROGRAM_ID\"><field length=\"4\" scale=\"0\" type=\"string\">RB31</field></data><data name=\"AUTH_FLAG\"><field length=\"1\" scale=\"0\" type=\"string\">N</field></data><data name=\"TRAN_TYPE\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"APPR_USER_ID\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"DEST_BRANCH_NO\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"AUTH_USER_ID\"><field length=\"0\" scale=\"0\" type=\"string\"/></data><data name=\"TRAN_DATE\"><field length=\"8\" scale=\"0\" type=\"string\">20211117</field></data><data name=\"TRAN_MODE\"><field length=\"6\" scale=\"0\" type=\"string\">ONLINE</field></data><data name=\"MESSAGE_TYPE\"><field length=\"4\" scale=\"0\" type=\"string\">1200</field></data><data name=\"WS_ID\"><field length=\"2\" scale=\"0\" type=\"string\">05</field></data><data name=\"USERNAME\"><field length=\"3\" scale=\"0\" type=\"string\">崔梦翌</field></data><data name=\"REVERSAL_TRAN_TYPE\"><field length=\"0\" scale=\"0\" type=\"string\"/></data></struct></data></sys-header><app-header><data name=\"APP_HEAD\"><struct><data name=\"PGUP_OR_PGDN\"><field length=\"1\" scale=\"0\" type=\"string\">1</field></data><struct/></data></app-header><local-header><data name=\"LOCAL_HEAD\"><struct/></data></local-header><body><data name=\"ACCEPTMETHOD_NO\"><field length=\"2\" scale=\"0\" type=\"string\">qy</field></data></body></service>";
//        请求体
        HttpEntity<String> formEntity = new HttpEntity<>(xml, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(path, formEntity, String.class);

//        测试
//        String xmlResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><service><sys-header><data name=\"SYS_HEAD\"><struct><data name=\"SERVICE_CODE\"><field length=\"11\" type=\"string\"></field></data><data name=\"SERVICE_SCENE\"><field length=\"2\" type=\"string\"></field></data><data name=\"CONSUMER_ID\"><field length=\"3\" type=\"string\"></field></data><data name=\"SEQ_NO\"><field length=\"32\" type=\"string\">0328102830812838234234303</field></data><data name=\"PROVIDE_ID\"><field length=\"30\" type=\"string\"></field></data><data name=\"PROVIDE_SEQ_NO\"><field length=\"30\" type=\"string\">028102830812830123018230010</field></data><data name=\"SOURCE_CONSUMER_ID\"><field length=\"6\" type=\"string\"></field></data><data name=\"BUSS_SEQ_NO\"><field length=\"50\" type=\"string\"></field></data><data name=\"TRAN_DATE\"><field length=\"8\" type=\"string\"></field></data><data name=\"TRAN_TIMESTAMP\"><field length=\"9\" type=\"string\"></field></data><data name=\"RET_STATUS\"><field length=\"30\" type=\"string\"></field></data><data name=\"RET_ARRAY\"><array><struct><data name=\"RET_CODE\"><field length=\"10\" type=\"string\">CCC</field></data><data name=\"RET_MSG\"><field length=\"512\" type=\"string\">DDD</field></data></struct></array></data></struct></data></sys-header><app-header><data name=\"APP_HEAD\"><struct><data name=\"PAGE_END\"><field length=\"12\" scale=\"0\" type=\"string\">10</field></data><data name=\"CURRENT_NUM\"><field length=\"12\" scale=\"0\" type=\"string\">2</field></data><data name=\"PGUP_OR_PGDN\"><field length=\"12\" scale=\"0\" type=\"string\">1</field></data><data name=\"PAGE_START\"><field length=\"12\" scale=\"0\" type=\"string\">1</field></data><data name=\"TOTAL_NUM\"><field length=\"12\" scale=\"0\" type=\"string\">100</field></data></struct></data></app-header><local-header><data name=\"LOCAL_HEAD\"><struct/></data></local-header><body><data name=\"RET_ARRAY\"><array><struct><data name=\"RET_CODE\"><field length=\"10\" type=\"string\">CCC</field></data><data name=\"RET_MSG\"><field length=\"512\" type=\"string\">DDD</field></data></struct></array></data></body></service>\n";
        XmlResponse XmlResponse = XmlResponseHandler.getInstance().transform(responseEntity.getBody());
        return XmlResponse;
    }

    /**
     * 新 请求改造
     *
     * @param commonRequest
     * @param appHeader
     * @return
     * @throws YzException
     */
    @Override
    public CommonResponse doNewPost(CommonRequest commonRequest, AppHeader appHeader)
            throws YzException {
        XmlRequest XmlRequest = XmlRequestFactory.getInstance().createNewXmlRequest(commonRequest, appHeader);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        String xml = XmlRequestHandler.getInstance().parseToXml(XmlRequest);
        HttpEntity<String> formEntity = new HttpEntity<>(xml, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(commonRequest.getJournal().getReturnCode(), formEntity, String.class);
        XmlResponse XmlResponse = XmlResponseHandler.getInstance().transform(responseEntity.getBody());
        CommonResponse commonResponse = CommonResponseHandler.getInstance().transform(commonRequest, XmlResponse);
        return commonResponse;
    }
}
