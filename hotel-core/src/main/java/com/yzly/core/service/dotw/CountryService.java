package com.yzly.core.service.dotw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.core.domain.dotw.Country;
import com.yzly.core.domain.dotw.Currency;
import com.yzly.core.repository.dotw.CountryRepository;
import com.yzly.core.repository.dotw.CurrencyRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/11/20
 * @desc
 **/
@Service
@CommonsLog
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    public void syncByJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonCountries = jsonObject.getJSONArray("countries");
        JSONObject jsonCountry = jsonCountries.getJSONObject(0);
        JSONArray countries = jsonCountry.getJSONArray("country");
        for (int i = 0; i < countries.size(); i++) {
            JSONObject joc = countries.getJSONObject(i);
            Country country = new Country(joc.getString("name"), joc.getString("code"),
                    joc.getString("regionName"), joc.getString("regionCode"));
            Country coun = countryRepository.findByCode(country.getCode());
            if (coun == null) {
                countryRepository.save(country);
            }
        }
        log.info("sync country success");
    }

    public Country getChina() {
        return countryRepository.findByName("CHINA");
    }

    public void syncCurrencyByJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonCurrencies = jsonObject.getJSONArray("currency");
        JSONObject jsonCurrency = jsonCurrencies.getJSONObject(0);
        JSONArray currencies = jsonCurrency.getJSONArray("option");
        for (int i = 0; i < currencies.size(); i++) {
            JSONObject joc = currencies.getJSONObject(i);
            Currency currency = new Currency(joc.getString("shortcut"), joc.getString("value"),
                    joc.getString("shortcut"));
            Currency cur = currencyRepository.findByCode(currency.getCode());
            if (cur == null) {
                currencyRepository.save(currency);
            }
        }
        log.info("sync currency success");
    }

    public List<Currency> getAllCurrency() {
        return currencyRepository.findAll();
    }

}
