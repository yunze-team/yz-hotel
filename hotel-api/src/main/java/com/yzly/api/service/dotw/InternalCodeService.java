package com.yzly.api.service.dotw;

import com.alibaba.fastjson.JSONObject;
import com.yzly.api.common.DCMLHandler;
import com.yzly.core.domain.dotw.City;
import com.yzly.core.domain.dotw.Country;
import com.yzly.core.domain.dotw.Currency;
import com.yzly.core.service.dotw.CityService;
import com.yzly.core.service.dotw.CodeService;
import com.yzly.core.service.dotw.CountryService;
import lombok.extern.apachecommons.CommonsLog;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/11/19
 * @desc
 **/
@Service
@CommonsLog
public class InternalCodeService {

    @Autowired
    private DCMLHandler dcmlHandler;
    @Autowired
    private CountryService countryService;
    @Autowired
    private CityService cityService;
    @Autowired
    private CodeService codeService;

    public List<Country> getAllCountries() {
        log.info("getAllCountries start.");
        return countryService.getAll();
    }

    public void syncCountries() {
        log.info("syncCountries start.");
        Document doc = dcmlHandler.getAllCountries();
        String json = dcmlHandler.sendDotw(doc);
        countryService.syncByJson(json);
    }

    public List<City> getAllCities() {
        return cityService.getAll();
    }

    public void syncCities() {
        log.info("sync cities start.");
        Country country = countryService.getChina();
        Document doc = dcmlHandler.getAllCitiesByCountry(country.getCode(), country.getName());
        String json = dcmlHandler.sendDotw(doc);
        cityService.syncByJson(json);
    }

    public List<Currency> getAllCurrencies() {
        log.info("getAllCurrencies start.");
        return countryService.getAllCurrency();
    }

    public void syncCurrencies() {
        log.info("sync currencies start.");
        Document doc = dcmlHandler.getAllCurrencies();
        String json = dcmlHandler.sendDotw(doc);
        countryService.syncCurrencyByJson(json);
    }

    public JSONObject getrate() {
        return dcmlHandler.getRateBasis();
    }

    public void syncRate() {
        JSONObject jsonObject = dcmlHandler.getRateBasis();
        codeService.syncRateBasis(jsonObject);
    }

    public void syncSalu() {
        JSONObject jsonObject = dcmlHandler.getSalutationsIds();
        codeService.syncSalutations(jsonObject);
    }

    public JSONObject syncLeisureids() {
        JSONObject jsonObject = dcmlHandler.getLeisureids();
        codeService.syncLeisureIds(jsonObject);
        return jsonObject;
    }

    public JSONObject syncBusiness() {
        JSONObject jsonObject = dcmlHandler.getBusinessIds();
        codeService.syncBusinessIds(jsonObject);
        return jsonObject;
    }

    public JSONObject syncAmenitie() {
        JSONObject jsonObject = dcmlHandler.getAmenitieIds();
        codeService.syncAmenitieIds(jsonObject);
        return jsonObject;
    }

    public JSONObject syncRoomAmenitie() {
        JSONObject jsonObject = dcmlHandler.getRoomAmenitieIds();
        codeService.syncRoomAmenitieIds(jsonObject);
        return jsonObject;
    }

    public JSONObject syncHotelClassification() {
        JSONObject jsonObject = dcmlHandler.getHotelClassification();
        codeService.syncHotelClassification(jsonObject);
        return jsonObject;
    }

}
