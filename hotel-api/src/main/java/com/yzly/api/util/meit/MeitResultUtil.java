package com.yzly.api.util.meit;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.util.meit.international.AESUtilUsingCommonDecodec;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.meit.ResultEnum;
import lombok.extern.apachecommons.CommonsLog;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@CommonsLog
public class MeitResultUtil {

    public static MeitResult generateResult(ResultEnum resultEnum, Object data) {
        MeitResult result = new MeitResult();
        result.setCode(resultEnum.getResultCode());
        result.setMessage(resultEnum.getResultMsg());
        result.setSuccess(resultEnum.getResult());
        result.setData(data);
        return result;
    }

    public static String aesSerialResult(MeitResult meitResult) {
        String res = "";
        try {
            res = AESUtilUsingCommonDecodec.encrypt(JSONObject.toJSONString(meitResult));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

}
