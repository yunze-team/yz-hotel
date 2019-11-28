package com.yzly.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author lazyb
 * @create 2019/11/28
 * @desc
 **/
@SpringBootApplication
@ComponentScan("com.yzly")
@ServletComponentScan
@EnableJpaRepositories("com.yzly.core.repository")
@EntityScan("com.yzly.core.domain")
@EnableJpaAuditing
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
