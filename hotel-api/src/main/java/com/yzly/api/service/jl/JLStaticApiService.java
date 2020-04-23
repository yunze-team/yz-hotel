package com.yzly.api.service.jl;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.JLHandler;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.service.EventAttrService;
import com.yzly.core.service.jl.JLStaticService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Service
@CommonsLog
public class JLStaticApiService {

    @Autowired
    private JLStaticService jlStaticService;
    @Autowired
    private EventAttrService eventAttrService;
    @Autowired
    private JLHandler jlHandler;

    private static final String JL_CITY_PAGE = "JL_CITY_PAGE";

    /**
     * 循环获取捷旅城市并入库
     */
    public void syncJlCity() {
        while (1 == 1) {
            EventAttr attr = eventAttrService.findByType(JL_CITY_PAGE);
            try {
                JSONObject reJson = JSONObject.parseObject(jlHandler.cityQuery(Integer.valueOf(attr.getEventValue()))).getJSONObject("result");
                jlStaticService.syncCityByJson(reJson);
            } catch (Exception e) {
                log.error(e.getMessage());
                break;
            }
        }
    }

}
