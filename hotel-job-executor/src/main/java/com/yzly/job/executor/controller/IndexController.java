package com.yzly.job.executor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2019/12/10
 * @desc
 **/
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "job executor started.";
    }

}
