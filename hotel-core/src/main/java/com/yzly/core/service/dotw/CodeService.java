package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.*;
import com.yzly.core.repository.dotw.*;
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
    @Autowired
    private SalutationsIdsRepository salutationsIdsRepository;
    @Autowired
    private LeisureIdsRepository leisureIdsRepository;
    @Autowired
    private BusinessIdsRepository businessIdsRepository;
    @Autowired
    private AmenitieIdsRepository amenitieIdsRepository;
    @Autowired
    private RoomAmenitieIdsRepository roomAmenitieIdsRepository;
    @Autowired
    private HotelClassificationIdsRepository hotelClassificationIdsRepository;

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

    @Transactional
    public void syncSalutations(JSONObject jsonObject) {
        JSONArray rateArray = jsonObject.getJSONObject("salutations").getJSONArray("option");
        for (int i = 0; i < rateArray.size(); i++) {
            JSONObject rateJson = rateArray.getJSONObject(i);
            String code = rateJson.getString("@value");
            if (salutationsIdsRepository.findByCode(code) != null) {
                continue;
            }
            SalutationsIds salutationsIds = new SalutationsIds(code, rateJson.getString("#text"));
            salutationsIdsRepository.save(salutationsIds);
        }
    }

    @Transactional
    public void syncLeisureIds(JSONObject jsonObject) {
        JSONArray idsArray = jsonObject.getJSONObject("leisures").getJSONArray("option");
        for (int i = 0; i < idsArray.size(); i++) {
            JSONObject idJson = idsArray.getJSONObject(i);
            String code = idJson.getString("@value");
            if (leisureIdsRepository.findByCode(code) != null) {
                continue;
            }
            LeisureIds leisureIds = new LeisureIds(code, idJson.getString("#text"));
            leisureIdsRepository.save(leisureIds);
        }
    }

    @Transactional
    public void syncBusinessIds(JSONObject jsonObject) {
        JSONArray idsArray = jsonObject.getJSONObject("business").getJSONArray("option");
        for (int i = 0; i < idsArray.size(); i++) {
            JSONObject idJson = idsArray.getJSONObject(i);
            String code = idJson.getString("@value");
            if (businessIdsRepository.findByCode(code) != null) {
                continue;
            }
            BusinessIds businessIds = new BusinessIds(code, idJson.getString("#text"));
            businessIdsRepository.save(businessIds);
        }
    }

    @Transactional
    public void syncAmenitieIds(JSONObject jsonObject) {
        JSONArray idsArray = jsonObject.getJSONObject("amenities").getJSONArray("option");
        for (int i = 0; i < idsArray.size(); i++) {
            JSONObject idJson = idsArray.getJSONObject(i);
            String code = idJson.getString("@value");
            if (amenitieIdsRepository.findByCode(code) != null) {
                continue;
            }
            AmenitieIds amenitieIds = new AmenitieIds(code, idJson.getString("#text"));
            amenitieIdsRepository.save(amenitieIds);
        }
    }

    @Transactional
    public void syncRoomAmenitieIds(JSONObject jsonObject) {
        JSONArray idsArray = jsonObject.getJSONObject("amenities").getJSONArray("option");
        for (int i = 0; i < idsArray.size(); i++) {
            JSONObject idJson = idsArray.getJSONObject(i);
            String code = idJson.getString("@value");
            if (roomAmenitieIdsRepository.findByCode(code) != null) {
                continue;
            }
            RoomAmenitieIds roomAmenitieIds = new RoomAmenitieIds(code, idJson.getString("#text"));
            roomAmenitieIdsRepository.save(roomAmenitieIds);
        }
    }

    @Transactional
    public void syncHotelClassification(JSONObject jsonObject) {
        JSONArray idsArray = jsonObject.getJSONObject("classification").getJSONArray("option");
        for (int i = 0; i < idsArray.size(); i++) {
            JSONObject idJson = idsArray.getJSONObject(i);
            String code = idJson.getString("@value");
            if (hotelClassificationIdsRepository.findByCode(code) != null) {
                continue;
            }
            HotelClassificationIds hotelClassificationIds = new HotelClassificationIds(code, idJson.getString("#text"));
            hotelClassificationIdsRepository.save(hotelClassificationIds);
        }
    }

}
