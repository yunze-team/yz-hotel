package com.yzly.core.service.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.event.EventAttr;
import com.yzly.core.domain.jl.JLCity;
import com.yzly.core.repository.event.EventAttrRepository;
import com.yzly.core.repository.jl.JLCityRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 捷旅api-静态信息同步服务
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Service
@CommonsLog
public class JLStaticService {

    @Autowired
    private JLCityRepository jlCityRepository;
    @Autowired
    private EventAttrRepository eventAttrRepository;

    private static final String JL_CITY_PAGE = "JL_CITY_PAGE";

    /**
     * 按照返回的json存入city
     * @param reJson
     * @throws Exception
     */
    public void syncCityByJson(JSONObject reJson) throws Exception {
        JSONArray cityArray = reJson.getJSONArray("hotelGeoList");
        if (cityArray.size() == 0) {
            throw new Exception("city array is empty");
        }
        for (int i = 0; i < cityArray.size(); i++) {
            JSONObject cityJson = cityArray.getJSONObject(i);
            JLCity jlCity = new JLCity(cityJson.getInteger("countryId"), cityJson.getString("countryCn"), cityJson.getString("countryEn"),
                    cityJson.getInteger("stateId"), cityJson.getString("stateCn"), cityJson.getString("stateEn"),
                    cityJson.getInteger("cityId"), cityJson.getString("cityCn"), cityJson.getString("cityEn"));
            JLCity city = jlCityRepository.findByCityId(jlCity.getCityId());
            if (city != null) {
                continue;
            }
            jlCityRepository.save(jlCity);
        }
        EventAttr attr = eventAttrRepository.findByEventType(JL_CITY_PAGE);
        attr.setEventValue(String.valueOf(Integer.valueOf(attr.getEventValue()) + 1));
        eventAttrRepository.save(attr);
    }

}
