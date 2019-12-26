package com.yzly.api.config.meit;

import com.meituan.hotel.openplatform.MtHotelConfiguration;
import com.meituan.hotel.openplatform.internal.domain.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalConfig {
    @Value("${meituan.partnerId}")
    private String partnerId;
    @Value("${meituan.clientSecret}")
    private String encryptKey;

    @Bean
    public MtHotelConfiguration mtHotelConfiguration(){
        MtHotelConfiguration conf;
        conf = new MtHotelConfiguration(partnerId, encryptKey);
        conf.setEnv(Environment.DEV); // Environment.DEV for developing or testing
        return conf;
    }

}
