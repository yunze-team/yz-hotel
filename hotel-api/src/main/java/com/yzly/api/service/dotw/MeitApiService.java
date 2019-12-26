package com.yzly.api.service.dotw;

import com.yzly.api.util.meit.MeitResultUtil;
import com.yzly.core.domain.meit.MeitTraceLog;
import com.yzly.core.domain.meit.dto.MeitHotel;
import com.yzly.core.domain.meit.dto.MeitHotelExt;
import com.yzly.core.domain.meit.dto.MeitResult;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.meit.ResultEnum;
import com.yzly.core.service.meit.MeitService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyb
 * @create 2019/12/23
 * @desc
 **/
@Service
@CommonsLog
public class MeitApiService {

    @Autowired
    private MeitService meitService;

    /**
     * 添加美团调用日志记录
     * @param traceId
     * @param encyptData
     * @param reqData
     * @param meitTraceLog
     * @return
     */
    public MeitTraceLog addTraceLog(String traceId, String encyptData, String reqData, MeitTraceLog meitTraceLog) {
        return meitService.addOrUpdateTrace(traceId, encyptData, reqData, meitTraceLog);
    }

    /**
     * 美团酒店同步方法
     * @param skip
     * @param limit
     * @return
     */
    public Object syncHotelBasic(int skip, int limit) {
        int page = skip / limit + 1;
        List<MeitHotel> mlist = meitService.getHotelBasicList(page, limit, DistributorEnum.MEIT);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotels", mlist);
        return data;
    }

    /**
     * 美团酒店扩展信息同步
     * @param hotelId
     * @return
     */
    public Object syncHotelExtend(String hotelId) {
        List<MeitHotelExt> mlist = meitService.getHotelExtListByIds(hotelId);
        if (mlist == null) {
            return null;
        }
        Map<String, List> data = new HashMap<>();
        data.put("hotelExt", mlist);
        return data;
    }

}
