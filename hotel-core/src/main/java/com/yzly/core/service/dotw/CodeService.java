package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.RateBasis;
import com.yzly.core.repository.dotw.RateBasisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author lazyb
 * @create 2019/12/2
 * @desc
 **/
@Service
public class CodeService {

    @Autowired
    private RateBasisRepository rateBasisRepository;

    @Transactional
    public void syncRateBasis(JSONObject jsonObject) {
        // 不考虑非空和数量1的问题
        JSONArray rateArray = jsonObject.getJSONObject("ratebasis").getJSONArray("option");
        for (int i = 0; i < rateArray.size(); i++) {
            JSONObject rateJson = rateArray.getJSONObject(i);
            String code = rateJson.getString("@value");
            if (rateBasisRepository.findByCode(code) != null) {
                continue;
            }
            RateBasis rateBasis = new RateBasis(code, rateJson.getString("#text"));
            rateBasisRepository.save(rateBasis);
        }
    }

    public String getRateValue(String rateId) {
        RateBasis rateBasis = rateBasisRepository.findByCode(rateId);
        if (rateBasis == null) {
            return null;
        }
        return rateBasis.getName();
    }

}
