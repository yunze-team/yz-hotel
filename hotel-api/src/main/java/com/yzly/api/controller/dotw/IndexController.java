package com.yzly.api.controller.dotw;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lazyb
 * @create 2019/11/19
 * @desc
 **/
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "dotw api started.";
    }

}
