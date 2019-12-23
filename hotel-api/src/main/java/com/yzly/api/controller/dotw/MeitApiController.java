package com.yzly.api.controller.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.service.dotw.MeitApiService;
import com.yzly.api.util.meit.international.AESUtilUsingCommonDecodec;
import com.yzly.api.util.meit.international.AuthValidatorUtil;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.meit.ResultEnum;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@RestController
@RequestMapping("/meit")
@CommonsLog
public class MeitApiController {

    @Value("${meituan.clientSecret}")
    private String secret;

    @Autowired
    private MeitApiService meitApiService;

    /**
     * 基础请求传递方法，负责将美团请求进行认证，并解密数据，记录日志
     * @param servletRequest
     * @return
     * @throws Exception
     */
    private JSONObject baseRequestTrans(ServletRequest servletRequest) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String encyptData = request.getParameter("param");
        String traceId = request.getHeader("Request-Trace");
        MeitTraceLog traceLog = meitApiService.addTraceLog(traceId, encyptData, null, null);
        log.info("traceLog: " + JSONObject.toJSONString(traceLog));
        ResultEnum resultEnum = new AuthValidatorUtil().validate(request, secret);
        if (!resultEnum.equals(ResultEnum.SUCCESS)) {
            throw new Exception(resultEnum.toString());
        }
        String reqData = AESUtilUsingCommonDecodec.decrypt(encyptData);
        // 更新美团调用日志的解密数据
        meitApiService.addTraceLog(traceId, encyptData, reqData, traceLog);
        return JSON.parseObject(reqData);
    }

    /**
     * 同步酒店基础数据
     * @param servletRequest
     * @return
     */
    @GetMapping("/hotel_basic")
    public Object hotelBasic(ServletRequest servletRequest) {
        JSONObject request;
        try {
            request = baseRequestTrans(servletRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            return generateFailResult(ResultEnum.FAIL);
        }
        int skip = request.getInteger("skip");
        int limit = request.getInteger("limit");
        MeitResult result = meitApiService.syncHotelBasic(skip, limit);
        String data = JSONObject.toJSONString(result);
        try {
            return AESUtilUsingCommonDecodec.encrypt(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            return generateFailResult(ResultEnum.FAIL);
        }
    }

    // TODO: will finish meit hotel_extend api method
    public Object hotelExtend(ServletRequest servletRequest) {
        JSONObject request;
        try {
            request = baseRequestTrans(servletRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            return generateFailResult(ResultEnum.FAIL);
        }
        return request;
    }

    private MeitResult generateFailResult(ResultEnum resultEnum) {
        MeitResult result = new MeitResult();
        result.setCode(resultEnum.getResultCode());
        result.setMessage(resultEnum.getResultMsg());
        result.setSuccess(resultEnum.getResult());
        return result;
    }

}
