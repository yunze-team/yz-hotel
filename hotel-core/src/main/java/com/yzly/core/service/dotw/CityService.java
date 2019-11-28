package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.City;
import com.yzly.core.repository.dotw.CityRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by toby on 2019/11/21.
 */
@Service
@CommonsLog
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public void syncByJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonCities = jsonObject.getJSONArray("cities");
        JSONObject jsonCity = jsonCities.getJSONObject(0);
        JSONArray cities = jsonCity.getJSONArray("city");
        for (int i = 0; i < cities.size(); i++) {
            JSONObject joc = cities.getJSONObject(i);
            City ci = new City(joc.getString("name"), joc.getString("code"),
                    joc.getString("countryName"), joc.getString("countryCode"));
            City city = cityRepository.findByCode(ci.getCode());
            if (city == null) {
                cityRepository.save(ci);
            }
        }
        log.info("sync city success");
    }

    public List<City> getAll() {
        return cityRepository.findAll();
    }

}
