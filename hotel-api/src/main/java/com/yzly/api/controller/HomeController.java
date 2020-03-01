package com.yzly.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lazyb
 * @create 2020/3/1
 * @desc
 **/
@Controller
@RequestMapping(value = "/")
public class HomeController {

    @RequestMapping
    public String index() {
        return "/static/index.html";
    }

    @RequestMapping(value = "home/about")
    public String about() {
        return "/static/about.html";
    }

    @RequestMapping(value = "home/buy")
    public String buy() {
        return "/static/buy.html";
    }

    @RequestMapping(value = "home/information")
    public String information() {
        return "/static/information.html";
    }

    @RequestMapping(value = "home/scenery")
    public String scenery() {
        return "/static/scenery.html";
    }

}
