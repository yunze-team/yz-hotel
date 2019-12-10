package com.yzly.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author lazyb
 * @create 2019/11/28
 * @desc
 **/
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan("com.yzly")
@ServletComponentScan
@EnableJpaRepositories("com.yzly.core.repository")
@EnableMongoRepositories("com.yzly.core.repository")
@EntityScan("com.yzly.core.domain")
@EnableJpaAuditing
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
