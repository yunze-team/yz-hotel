package com.yzly.api.util.meit;

import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.meit.ResultEnum;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
public class MeitResultUtil {

    public static MeitResult generateResult(ResultEnum resultEnum, Object data) {
        MeitResult result = new MeitResult();
        result.setCode(resultEnum.getResultCode());
        result.setMessage(resultEnum.getResultMsg());
        result.setSuccess(resultEnum.getResult());
        result.setData(data);
        return result;
    }

}
